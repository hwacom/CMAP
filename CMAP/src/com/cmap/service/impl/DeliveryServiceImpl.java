package com.cmap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.BlockType;
import com.cmap.comm.enums.ConnectionMode;
import com.cmap.comm.enums.ScriptType;
import com.cmap.dao.BaseDAO;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.ProvisionLogDAO;
import com.cmap.dao.ScriptInfoDAO;
import com.cmap.dao.vo.DeviceDAOVO;
import com.cmap.dao.vo.ProvisionLogDAOVO;
import com.cmap.dao.vo.ScriptInfoDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceDetailInfo;
import com.cmap.model.DeviceDetailMapping;
import com.cmap.model.DeviceList;
import com.cmap.model.ProvisionAccessLog;
import com.cmap.model.ProvisionLogStep;
import com.cmap.model.ScriptInfo;
import com.cmap.plugin.module.blocked.record.BlockedRecordService;
import com.cmap.plugin.module.blocked.record.BlockedRecordVO;
import com.cmap.plugin.module.blocked.record.ModuleBlockedList;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DeliveryService;
import com.cmap.service.ProvisionService;
import com.cmap.service.StepService;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;
import com.cmap.service.vo.ProvisionServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.cmap.utils.impl.CommonUtils;

@Service("deliveryService")
@Transactional
public class DeliveryServiceImpl extends CommonServiceImpl implements DeliveryService {
	@Log
	private static Logger log;

	@Autowired
	private ScriptInfoDAO scriptInfoDAO;

	@Autowired
	private ProvisionLogDAO provisionLogDAO;

	@Autowired
	private DeviceDAO deviceDAO;

	@Autowired
	private StepService stepService;

	@Autowired
	private ProvisionService provisionService;
	
	@Autowired
	private BlockedRecordService blockedRecordService;
	
	@Override
	public DeliveryServiceVO doDelivery(ConnectionMode connectionMode, DeliveryParameterVO dpVO, boolean sysTrigger,
			String triggerBy, String triggerRemark, boolean chkParameters) throws ServiceLayerException {

		DeliveryServiceVO retVO = new DeliveryServiceVO();
		try {
			/*
			 * Step 1.再次驗證傳入的參數值合法性
			 */
			boolean chkSuccess = false;

			if (chkParameters) {
				chkSuccess = checkDeliveryParameter(dpVO);

			} else {
				chkSuccess = true;
			}

			if (!chkSuccess) {
				throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");

			} else {
				retVO = goDelivery(connectionMode, dpVO, sysTrigger, triggerBy, triggerRemark);
			}

		} catch (ServiceLayerException sle) {
			log.error(sle.toString(), sle);
			throw sle;

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(e);
		}
		return retVO;
	}

	private DeliveryServiceVO goDelivery(ConnectionMode connectionMode, DeliveryParameterVO dpVO, boolean sysTrigger,
			String triggerBy, String triggerRemark) throws ServiceLayerException {
		DeliveryServiceVO retVO = new DeliveryServiceVO();
		String retMsg = "";

		// Step 1.取得腳本資料
		final String scriptInfoId = dpVO.getScriptInfoId();
		final String scriptCode = dpVO.getScriptCode();
		ScriptInfo scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(scriptInfoId, scriptCode);

		if (scriptInfo == null) {
			throw new ServiceLayerException("無法取得腳本資料，請重新操作");
		}

		// Step 2.替換腳本參數值
		final List<String> varKeyList = dpVO.getVarKey();
		final List<List<String>> deviceVarValueList = dpVO.getVarValue();
		final String actionScript = scriptInfo.getActionScript();

		final Map<String, String> deviceInfo = dpVO.getDeviceInfo();
		final List<String> groupIdList = dpVO.getGroupId();
		final List<String> deviceIdList = dpVO.getDeviceId();
		final String reason = dpVO.getReason();

		ProvisionServiceVO masterVO = new ProvisionServiceVO();
		masterVO.setLogMasterId(UUID.randomUUID().toString());
		masterVO.setBeginTime(new Date());
		masterVO.setUserName(sysTrigger ? triggerBy : SecurityUtil.getSecurityUser().getUsername());

		StringBuffer errorSb = new StringBuffer();
		int deviceCount = deviceIdList != null ? deviceIdList.size() : 1;
		int successCount = 0;
		int failedCount = 0;
		for (int idx = 0; idx < deviceCount; idx++) {

			final String groupId = groupIdList != null ? groupIdList.get(idx) : "N/A";
			final String deviceId = deviceIdList != null ? deviceIdList.get(idx) : "N/A";

			try {
				String groupName;
				String deviceName;
				String deviceListId;

				if (deviceInfo == null || (deviceInfo != null && deviceInfo.isEmpty())) {
					DeviceList deviceList = deviceDAO.findDeviceListByGroupAndDeviceId(groupId, deviceId);

					if (deviceList == null) {
						throw new ServiceLayerException("查無設備資料 >> groupId: " + groupId + " , deviceId: " + deviceId);
					}

					groupName = deviceList.getGroupName();
					deviceName = deviceList.getDeviceName();
					deviceListId = deviceList.getDeviceListId();

				} else {
					groupName = "N/A";
					deviceName = deviceInfo.get(Constants.DEVICE_NAME);
					deviceListId = null;
				}

				String script = actionScript;

				List<Map<String, String>> varMapList = null;
				if (varKeyList != null && !varKeyList.isEmpty()) {
					varMapList = new ArrayList<>();

					/*
					 * Case 1. deviceCount == 1 但 deviceVarValueList.size > 1 ==>>
					 * 表示同1腳本要做多組設定對同1台設備 (e.g.: 防火牆黑名單設定，一次要設定多筆IP) << 1對多 >>
					 *
					 * Case 2. deviceCount > 1 ==>> 預設為1腳本做1組設定對1台設備 (e.g.: 一次對多台設備做供裝) << 1對1 >>
					 *
					 * (目前不支援一次針對多台設備且每1台設備都做多組設定，須調整作法為一次針對1台設備做多組設定) << 多對多>>
					 */
					if (deviceCount > 1) {
						final List<String> valueList = deviceVarValueList.get(idx);
						varMapList.add(composeScriptVarMap(script, varKeyList, valueList));

					} else if (deviceCount == 1) {
						for (List<String> valueList : deviceVarValueList) {
							varMapList.add(composeScriptVarMap(script, varKeyList, valueList));
						}
					}
				}

				// Step 3.呼叫共用執行腳本
				StepServiceVO processVO = null;
				try {
					processVO = stepService.doScript(connectionMode, deviceListId, deviceInfo, scriptInfo, varMapList,
							sysTrigger, triggerBy, triggerRemark, reason);

					masterVO.getDetailVO().addAll(processVO.getPsVO().getDetailVO());

				} catch (Exception e) {
					log.error(e.toString(), e);
				}

				if (processVO == null || (processVO != null && !processVO.isSuccess())) {
					errorSb.append("[" + (idx + 1) + "] >> 群組名稱:【" + groupName + "】/設備名稱:【" + deviceName + "】"
							+ Constants.HTML_BREAK_LINE_SYMBOL).append("失敗原因:" + Constants.HTML_BREAK_LINE_SYMBOL)
							.append(processVO.getMessage())
							.append("------------------------------------------------------------------------------------------------");
					failedCount++;
					continue;
				}

				if (processVO.getCmdProcessLog() != null && !processVO.getCmdProcessLog().isEmpty()) {
					log.debug("processVO.getCmdProcessLog() ==>" + processVO.getCmdProcessLog());
					if (retVO.getProvisionLog() == null) {
						retVO.setProvisionLog(processVO.getCmdProcessLog());
					}
				}
				if (processVO.getCmdOutputList() != null && !processVO.getCmdOutputList().isEmpty()) {
					retVO.setCmdOutputList(processVO.getCmdOutputList());
				}

			} catch (ServiceLayerException sle) {
				log.error(sle.toString(), sle);
				errorSb.append("[" + (idx + 1) + "] >> 群組ID:【" + groupId + "】/設備ID:【" + deviceId + "】"
						+ Constants.HTML_BREAK_LINE_SYMBOL).append("失敗原因:" + Constants.HTML_BREAK_LINE_SYMBOL)
						.append(sle.toString() + Constants.HTML_BREAK_LINE_SYMBOL)
						.append("------------------------------------------------------------------------------------------------");
				failedCount++;
				continue;

			} catch (Exception e) {
				log.error(e.toString(), e);
				errorSb.append("[" + (idx + 1) + "] >> 群組ID:【" + groupId + "】/設備ID:【" + deviceId + "】供裝失敗"
						+ Constants.HTML_BREAK_LINE_SYMBOL);
				failedCount++;
				continue;
			}

			successCount++;
		}

		String msg = "";
		String[] args = null;
		if (deviceCount == 1) {
			if (failedCount == 0) {
				msg = "供裝成功";

			} else if (failedCount == 1) {
				msg = "供裝失敗";
			}

		} else {
			msg = "選定供裝 {0} 筆設備: 成功 {1} 筆；失敗 {2} 筆";
			args = new String[] { String.valueOf(deviceCount), String.valueOf(successCount),
					String.valueOf(failedCount) };
		}

		masterVO.setEndTime(new Date());
		masterVO.setResult(CommonUtils.converMsg(msg, args));
		masterVO.setReason(reason);

		provisionService.insertProvisionLog(masterVO);

		retMsg += CommonUtils.converMsg(msg, args) + Constants.HTML_BREAK_LINE_SYMBOL
				+ Constants.HTML_SEPARATION_LINE_SYMBOL + errorSb.toString();
		retVO.setRetMsg(retMsg);

		return retVO;
	}

	@Override
	public long countDeviceList(DeliveryServiceVO dsVO) throws ServiceLayerException {
		// TODO 自動產生的方法 Stub
		return 0;
	}

	@Override
	public List<DeliveryServiceVO> findDeviceList(DeliveryServiceVO dsVO, Integer startRow, Integer pageLength)
			throws ServiceLayerException {
		// TODO 自動產生的方法 Stub
		return null;
	}

	@Override
	public long countScriptList(DeliveryServiceVO dsVO) throws ServiceLayerException {
		long retVal = 0;
		try {
			ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
			BeanUtils.copyProperties(dsVO, siDAOVO);

			retVal = scriptInfoDAO.countScriptInfo(siDAOVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢發生異常，請嘗試重新操作");
		}
		return retVal;
	}

	@Override
	public List<DeliveryServiceVO> findScriptList(DeliveryServiceVO dsVO, List<String> groupIds, Integer startRow, Integer pageLength)
			throws ServiceLayerException {
		List<DeliveryServiceVO> retList = new ArrayList<>();
		try {
			ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
			BeanUtils.copyProperties(dsVO, siDAOVO);
			
			List<ScriptInfo> entities = scriptInfoDAO.findScriptInfo(siDAOVO, startRow, pageLength);
			List<DeviceList> devices = deviceDAO.findDistinctDeviceListByGroupIdsOrDeviceIds(groupIds, null);
			List<String> deviceModels = new ArrayList<>();
			devices.forEach(s -> deviceModels.add(s.getDeviceModel()));
			
			DeliveryServiceVO vo;
			if (entities != null && !(entities.isEmpty())) {
				for (ScriptInfo entity : entities) {
					if(deviceModels.contains(entity.getDeviceModel()) || entity.getDeviceModel().equals(Env.MEANS_ALL_SYMBOL)){						
						vo = new DeliveryServiceVO();
						BeanUtils.copyProperties(entity, vo);
						vo.setScriptTypeName(entity.getScriptType().getScriptTypeName());
						retList.add(vo);
					}					
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢發生異常，請嘗試重新操作");
		}
		return retList;
	}

	@Override
	public DeliveryServiceVO getScriptInfoByIdOrCode(String scriptInfoId, String scriptCode) throws ServiceLayerException {
		DeliveryServiceVO retVO = new DeliveryServiceVO();
		try {
			ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
			if (StringUtils.isNotBlank(scriptInfoId)) {
				siDAOVO.setQueryScriptInfoId(scriptInfoId);
			}
			if (StringUtils.isNotBlank(scriptCode)) {
				siDAOVO.setQueryScriptCode(scriptCode);
			}
			
			List<ScriptInfo> entities = scriptInfoDAO.findScriptInfo(siDAOVO, null, null);

			if (entities != null && !entities.isEmpty()) {
				ScriptInfo entity = entities.get(0);
				BeanUtils.copyProperties(entity, retVO);
				retVO.setScriptTypeName(entity.getScriptType().getScriptTypeName());

			} else {
				throw new ServiceLayerException("查詢此腳本資料，請重新查詢");
			}

		} catch (ServiceLayerException sle) {
			log.error(sle.toString(), sle);
			throw sle;

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢發生異常，請嘗試重新操作");
		}
		return retVO;
	}

	@Override
	public DeliveryServiceVO getVariableSetting(List<String> groups, List<String> devices, List<String> variables)
			throws ServiceLayerException {
		DeliveryServiceVO retVO = new DeliveryServiceVO();

		try {
			if ((groups == null || (groups != null && groups.isEmpty()))
					|| (devices == null || (devices != null && devices.isEmpty()))
					|| (variables == null || (variables != null && variables.isEmpty()))) {
				throw new ServiceLayerException("查詢客製變數選單發生錯誤<br>(錯誤描述:傳入參數錯誤)");

			} else if (groups.size() != devices.size()) {
				throw new ServiceLayerException("查詢客製變數選單發生錯誤<br>(錯誤描述:傳入參數錯誤，群組與設備數量不一致)");
			}

			/*
			 * deviceVarMap 資料結構: 第一層Key: 群組+設備ID 第二層Key: 變數名稱 第二層Value: 該設備變數值選單
			 */
			Map<String, Map<String, List<DeviceDetailInfo>>> deviceVarMap = new HashMap<>();
			/*
			 * 查詢此腳本的變數是否有系統客製函式
			 */
			for (String key : variables) {
				List<DeviceDetailMapping> mapping = deviceDAO.findDeviceDetailMapping(key);

				if (mapping == null || (mapping != null && mapping.isEmpty())) {
					continue;

				} else {
					Map<String, List<DeviceDetailInfo>> varMap = null;

					for (int i = 0; i < devices.size(); i++) {
						final String groupId = groups.get(i);
						final String deviceId = devices.get(i);
						final String mapKey = groupId + Env.COMM_SEPARATE_SYMBOL + deviceId;

						List<DeviceDetailInfo> infos = deviceDAO.findDeviceDetailInfo(null, groupId, deviceId, key);

						if (deviceVarMap.containsKey(mapKey)) {
							varMap = deviceVarMap.get(mapKey);

						} else {
							varMap = new HashMap<>();
						}

						varMap.put(key, infos);
						deviceVarMap.put(mapKey, varMap);
					}
				}
			}

			retVO.setDeviceVarMap(deviceVarMap);

		} catch (ServiceLayerException sle) {
			log.error(sle.toString(), sle);
			throw sle;

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢客製變數選單發生錯誤，請改以手動輸入或重新操作");
		}
		return retVO;
	}

	private boolean checkDeliveryParameter(DeliveryParameterVO dpVO) {
		try {
			// Step 1.檢核腳本是否存在
			ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
			siDAOVO.setQueryScriptInfoId(dpVO.getScriptInfoId());
			siDAOVO.setQueryScriptCode(dpVO.getScriptCode());

			List<ScriptInfo> scriptList = scriptInfoDAO.findScriptInfo(siDAOVO, null, null);

			if (scriptList == null || (scriptList != null && scriptList.isEmpty())) {
				return false;
			}

			ScriptInfo dbEntity = scriptList.get(0);

			// Step 2.檢核JSON內Script_Info_Id與Script_Code是否匹配
			final String dbScriptCode = dbEntity.getScriptCode();
			final String jsonScriptCode = dpVO.getScriptCode();

			if (!dbScriptCode.equals(jsonScriptCode)) {
				return false;
			}

			// Step 3.檢核JSON內VarKey與系統內設定的腳本變數欄位是否相符
			final String dbVarKeyJSON = dbEntity.getActionScriptVariable();
			final List<String> dbVarKeyList = (List<String>) transJSON2Object(dbVarKeyJSON, ArrayList.class);
			if(StringUtils.isNotBlank(dbEntity.getCheckScriptVariable())) {
				dbVarKeyList.addAll((List<String>) transJSON2Object(dbEntity.getCheckScriptVariable(), ArrayList.class));
			}
			final List<String> jsonVarKeyList = dpVO.getVarKey();

			if (dbVarKeyList.size() != jsonVarKeyList.size()) {
				return false;

			} else {
				if (!dbVarKeyList.containsAll(jsonVarKeyList)) {
					return false;
				}
			}

			// Step 4.檢核變數值(VarValue)是否有缺
			// VarValue資料結構: List(設備)<List(VarValue)>
			final List<List<String>> jsonVarValueList = dpVO.getVarValue();
			final List<String> jsonDeviceIdList = dpVO.getDeviceId();

			if (jsonDeviceIdList.size() != jsonVarValueList.size()) {
				return false;

			} else {
				boolean success = true;
				for (List<String> varValueList : jsonVarValueList) {
					if (dbVarKeyList.size() != varValueList.size()) {
						success = false;
						break;
					}
				}

				if (!success) {
					return false;
				}
			}

			// Step 5.檢核設備是否皆存在 且 是否與JSON內設備ID相符
			final List<String> jsonGroupIdList = dpVO.getGroupId();

			if (jsonGroupIdList.size() != jsonDeviceIdList.size()) {
				return false;
			}

			int i = 0;
			for (String deviceId : jsonDeviceIdList) {
				String groupId = jsonGroupIdList.get(i);

				final String jsonDeviceId = jsonDeviceIdList.get(i);
				DeviceList deviceEntity = deviceDAO.findDeviceListByGroupAndDeviceId(groupId, deviceId);

				if (deviceEntity == null
						|| (deviceEntity != null && !deviceEntity.getDeviceId().equals(jsonDeviceId))) {
					return false;
				}

				i++;
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;
		}

		return true;
	}

	private Map<String, String> composeScriptVarMap(String script, List<String> varKeyList, List<String> valueList) {
		Map<String, String> varMap = new HashMap<>();
		for (int i = 0; i < varKeyList.size(); i++) {
			final String vKey = Env.SCRIPT_VAR_KEY_SYMBOL + varKeyList.get(i) + Env.SCRIPT_VAR_KEY_SYMBOL;
			final String vValue = valueList.get(i);

			if (script.indexOf(vKey) != -1) {
				script = StringUtils.replace(script, vKey, vValue);
			}

			varMap.put(vKey, vValue);
		}

		return varMap;
	}

	@Override
	public String logAccessRecord(DeliveryServiceVO dsVO) throws ServiceLayerException {
		String uuid = null;
		try {
			Integer step = dsVO.getDeliveryStep();

			if (step == 0) {
				uuid = UUID.randomUUID().toString();
				ProvisionAccessLog access = new ProvisionAccessLog();
				access.setLogId(uuid);
				access.setIpAddress(dsVO.getIpAddr());
				access.setMacAddress(dsVO.getMacAddr());
				access.setCreateTime(new Timestamp(dsVO.getActionTime().getTime()));
				access.setCreateBy(dsVO.getActionBy());
				access.setUpdateTime(access.getCreateTime());
				access.setUpdateBy(dsVO.getCreateBy());
				provisionLogDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, access);

			} else {

			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return uuid;
	}

	@Override
	public long countProvisionLog(DeliveryServiceVO dsVO) throws ServiceLayerException {
		long retCount = 0;
		try {
			ProvisionLogDAOVO daovo = new ProvisionLogDAOVO();
			BeanUtils.copyProperties(dsVO, daovo);
			daovo.setQueryGroupId(dsVO.getQueryGroup());
			daovo.setQueryDeviceId(dsVO.getQueryDevice());
			daovo.setQueryBeginTimeStart(dsVO.getQueryTimeBegin());
			daovo.setQueryBeginTimeEnd(dsVO.getQueryTimeEnd());

			retCount = provisionLogDAO.countProvisionLogByDAOVO(daovo);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(""); // TODO
		}
		return retCount;
	}

	@Override
	public List<DeliveryServiceVO> findProvisionLog(DeliveryServiceVO dsVO, Integer startRow, Integer pageLength)
			throws ServiceLayerException {
		List<DeliveryServiceVO> retList = new ArrayList<>();
		try {
			ProvisionLogDAOVO daovo = new ProvisionLogDAOVO();
			BeanUtils.copyProperties(dsVO, daovo);
			daovo.setQueryGroupId(dsVO.getQueryGroup());
			daovo.setQueryDeviceId(dsVO.getQueryDevice());
			daovo.setQueryBeginTimeStart(dsVO.getQueryTimeBegin());
			daovo.setQueryBeginTimeEnd(dsVO.getQueryTimeEnd());

			List<Object[]> entities = provisionLogDAO.findProvisionLogByDAOVO(daovo, startRow, pageLength);

			if (entities != null && !entities.isEmpty()) {
				DeliveryServiceVO vo;

				for (Object[] entity : entities) {
					final String logStepId = Objects.toString(entity[2]);
					final Timestamp beginTime = (entity[4] != null) ? (Timestamp) entity[4] : null;
					final String userName = Objects.toString(entity[5], "(未知)");
					final String groupName = Objects.toString(entity[6], "(未知)");
					final String deviceName = Objects.toString(entity[7], "(未知)");
					final String systemVersion = Objects.toString(entity[8], "(未知)");
					final String scriptName = Objects.toString(entity[9], "(未知)");
					final String reason = Objects.toString(entity[10], "");
					final String result = Objects.toString(entity[11], "(未知)");
					final String provisionLog = Objects.toString(entity[12], "");

					vo = new DeliveryServiceVO();
					vo.setLogStepId(logStepId);
					vo.setDeliveryBeginTime(
							beginTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(beginTime) : "");
					vo.setCreateBy(userName);
					vo.setGroupName(groupName);
					vo.setDeviceName(deviceName);
					vo.setSystemVersion(systemVersion);
					vo.setScriptName(scriptName);
					vo.setDeliveryReason(reason);
					vo.setDeliveryResult(result);
					vo.setProvisionLog(provisionLog);

					retList.add(vo);
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException(""); // TODO
		}
		return retList;
	}

	@Override
	public DeliveryServiceVO getProvisionLogById(String logStepId) throws ServiceLayerException {
		DeliveryServiceVO retVO = new DeliveryServiceVO();
		try {
			ProvisionLogStep step = provisionLogDAO.findProvisionLogStepById(logStepId);

			if (step != null) {
				retVO.setProvisionLog(step.getProcessLog());
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return retVO;
	}

	@Override
	public List<DeviceList> findGroupDeviceOfSpecifyLayer(String groupId, List<String> deviceLayer)
			throws ServiceLayerException {
		List<DeviceList> retList = null;
		try {
			DeviceDAOVO dlDAOVO = new DeviceDAOVO();
			dlDAOVO.setGroupId(groupId);
			dlDAOVO.setDeviceLayerList(deviceLayer);
			retList = deviceDAO.findDeviceListByDAOVO(dlDAOVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查找 " + deviceLayer + " 設備失敗");
		}
		return retList;
	}

	@Override
    public  boolean doSyncDeviceIpBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO,
			List<BlockedRecordVO> dbRecordList) throws ServiceLayerException {
		
		List<DeviceList> deviceList = new ArrayList<DeviceList>();
		List<String> scriptList = new ArrayList<>();
		List<String> searchLayer = new ArrayList<String>();
		searchLayer.add(Env.DEVICE_LAYER_L3);
		
		if (Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE != null) {
			scriptList.addAll(Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE);
		}
		if (isAdmin) {
			//deviceList = findGroupDeviceOfSpecifyLayer(null, Env.DEVICE_LAYER_L3);

			// 若使用者為管理者，多查出中心端的IP控制腳本
			if (Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE_4_ADMIN != null) {
				scriptList.addAll(Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE_4_ADMIN);
			}
			// 若使用者為管理者，多查出LC Layer Device
			searchLayer.add(Env.DEVICE_LAYER_LC);
		}
		
		if (StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            deviceList.addAll(findGroupDeviceOfSpecifyLayer(brVO.getQueryGroupId(),searchLayer));
        } else if (brVO.getQueryGroupIdList() != null && !brVO.getQueryGroupIdList().isEmpty()) {
        	for (String groupId : brVO.getQueryGroupIdList()) {
        		deviceList.addAll(findGroupDeviceOfSpecifyLayer(groupId, searchLayer));
			}
        }
		
		Map<String, BlockedRecordVO> result = new HashMap<String, BlockedRecordVO>();
		String reason = prtgLoginAccount + "點選同步Switch IP封鎖記錄 按鈕";
		
		DeliveryServiceVO dsVO;
		DeliveryParameterVO dpVO;
		List<BlockedRecordVO> updateList = new ArrayList<>();
		BlockedRecordVO nowbrVO = null;
		String ipAddress = null;
		String blockCmd = null;
		ScriptInfo scriptInfo ;
		ScriptInfo info ;
		
		for (String scriptCode : scriptList) {
			for (DeviceList device : deviceList) {

				try {
					dpVO = new DeliveryParameterVO();
					dpVO.setGroupId(Arrays.asList(device.getGroupId()));
					dpVO.setDeviceId(Arrays.asList(device.getDeviceId()));
					dpVO.setScriptCode(scriptCode);
					dpVO.setVarKey(null);
					dpVO.setVarValue(null);
					dpVO.setReason(reason);

					dsVO = doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, prtgLoginAccount, reason, false);
					if (dsVO.getProvisionLog() != null) {
						String provisionLog = dsVO.getProvisionLog();						
						ipAddress = null;
						blockCmd = null;
						
						scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
						if(provisionLog.contains(scriptInfo.getActionScript())) {
							provisionLog = provisionLog.substring(provisionLog.indexOf(scriptInfo.getActionScript())+scriptInfo.getActionScript().length());
						}
						
						//判斷acl封鎖
						while(provisionLog.contains("deny ip host")) {
							provisionLog = provisionLog.substring(provisionLog.indexOf("deny ip host"));
							ipAddress = provisionLog.substring(provisionLog.indexOf("deny ip host")+12, provisionLog.indexOf(" any")).trim();
							blockCmd = provisionLog.substring(provisionLog.indexOf("deny ip host"), provisionLog.indexOf(" any")+4).trim();
							provisionLog = provisionLog.substring(provisionLog.indexOf(" any")+4).trim();
							
							nowbrVO = blockedRecordService.checkIpblockedList(device.getGroupId(), device.getDeviceId(), ipAddress , dbRecordList);
							
							
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("acl封鎖同步");
								
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ACL", device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
								updateList.add(nowbrVO);							
								
							}else {
								if(StringUtils.isBlank(nowbrVO.getScriptCode())) {
									info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ACL", device.getDeviceModel());
									if(info != null) {
										nowbrVO.setScriptCode(info.getScriptCode());
										nowbrVO.setScriptName(info.getScriptName());
										
										updateList.add(nowbrVO);
									}
								}
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
							}
						}
						
						//判斷arp封鎖
						while(provisionLog.contains("0000.0000.0001")) {
							provisionLog = provisionLog.substring(provisionLog.indexOf("Internet"));
							ipAddress = provisionLog.substring(provisionLog.indexOf("Internet")+8, provisionLog.indexOf(" 0000.0000.0001")).replaceAll("-", "").trim();
							blockCmd = provisionLog.substring(provisionLog.indexOf("Internet"), provisionLog.indexOf(" ARPA")+5);
							provisionLog = provisionLog.substring(provisionLog.indexOf(" ARPA")+5).trim();

							nowbrVO = blockedRecordService.checkIpblockedList(device.getGroupId(), device.getDeviceId(), ipAddress,dbRecordList);
							
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("arp封鎖同步");
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ARP", device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
								updateList.add(nowbrVO);
							}else {				
								if(StringUtils.isBlank(nowbrVO.getScriptCode())) {
									info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ARP", device.getDeviceModel());
									if(info != null) {
										nowbrVO.setScriptCode(info.getScriptCode());
										nowbrVO.setScriptName(info.getScriptName());
										
										updateList.add(nowbrVO);
									}
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
							}
						}
					}

				} catch (Exception e) {
					log.error(e.toString(), e);

					// 設備執行腳本失敗則跳過此設備
					break;
				}

			}
		}
		
		updateList.addAll(blockedRecordService.compareIpblockedList(dbRecordList, result));
		
		if (updateList != null && !updateList.isEmpty()) {
			blockedRecordService.saveOrUpdateRecord(updateList);
			return true;
        }
		return false;
	}

	@Override
    public  boolean doSyncDevicePortBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO,
			List<BlockedRecordVO> dbRecordList) throws ServiceLayerException {
		
		List<DeviceList> deviceList = new ArrayList<DeviceList>();
		List<String> scriptList = new ArrayList<>();
		List<String> searchLayer = new ArrayList<String>();
		searchLayer.add(Env.DEVICE_LAYER_L3);
		
		if (Env.DELIVERY_SYNC_SWITCH_PORT_RECORD_SCRIPT_CODE != null) {
			scriptList.addAll(Env.DELIVERY_SYNC_SWITCH_PORT_RECORD_SCRIPT_CODE);
			//show interfaces status disabled
		}
		if (isAdmin) {
			// 若使用者為管理者，多查出LC Layer Device
			searchLayer.add(Env.DEVICE_LAYER_LC);
		}
		
		if (StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            deviceList.addAll(findGroupDeviceOfSpecifyLayer(brVO.getQueryGroupId(),searchLayer));
        } else if (brVO.getQueryGroupIdList() != null && !brVO.getQueryGroupIdList().isEmpty()) {
        	for (String groupId : brVO.getQueryGroupIdList()) {
        		deviceList.addAll(findGroupDeviceOfSpecifyLayer(groupId, searchLayer));
			}
        }
		
		Map<String, BlockedRecordVO> result = new HashMap<String, BlockedRecordVO>();
		String reason = prtgLoginAccount + "點選同步Switch Port封鎖記錄 按鈕";
		
		DeliveryServiceVO dsVO;
		DeliveryParameterVO dpVO;
		List<BlockedRecordVO> updateList = new ArrayList<>();
		BlockedRecordVO nowbrVO = null;
		String portId = null;
		ScriptInfo scriptInfo ;
		ScriptInfo info ;
		
		for (String scriptCode : scriptList) {
			for (DeviceList device : deviceList) {

				try {
					dpVO = new DeliveryParameterVO();
					dpVO.setGroupId(Arrays.asList(device.getGroupId()));
					dpVO.setDeviceId(Arrays.asList(device.getDeviceId()));
					dpVO.setScriptCode(scriptCode);
					dpVO.setVarKey(null);
					dpVO.setVarValue(null);
					dpVO.setReason(reason);

					dsVO = doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, prtgLoginAccount, reason, false);
					if (dsVO.getProvisionLog() != null) {
						String provisionLog = dsVO.getProvisionLog();						
						portId = null;

						scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
						if(provisionLog.contains(scriptInfo.getActionScript())) {
							provisionLog = provisionLog.substring(provisionLog.indexOf(scriptInfo.getActionScript())+scriptInfo.getActionScript().length());
						}
						
						while(provisionLog.contains("disabled")) {
							portId = provisionLog.substring(0, provisionLog.indexOf(" disabled")).trim();
							provisionLog = provisionLog.substring(provisionLog.indexOf("1000BaseTX")+10).trim();

							nowbrVO = blockedRecordService.checkPortBlockedList(device.getGroupId(), device.getDeviceId(), portId,dbRecordList);
														
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("Port封鎖同步");
								
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.PORT_BLOCK.toString(), device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getPort(), nowbrVO);
								updateList.add(nowbrVO);
							}else {								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getPort(), nowbrVO);
							}
						}
					}

				} catch (Exception e) {
					log.error(e.toString(), e);

					// 設備執行腳本失敗則跳過此設備
					break;
				}

			}
		}
		
		updateList.addAll(blockedRecordService.comparePortBlockedList(dbRecordList, result));
		
		if (updateList != null && !updateList.isEmpty()) {
			blockedRecordService.saveOrUpdateRecord(updateList);
			return true;
        }
		return false;
	}
	

	@Override
    public  boolean doSyncDeviceMacBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO,
			List<BlockedRecordVO> dbRecordList) throws ServiceLayerException {
		
		List<DeviceList> deviceList = new ArrayList<DeviceList>();
		List<String> scriptList = new ArrayList<>();
		List<String> searchLayer = new ArrayList<String>();
		searchLayer.add(Env.DEVICE_LAYER_L3);
		
		if (Env.DELIVERY_SYNC_MAC_BLOCK_RECORD_SCRIPT_CODE != null) {
			scriptList.addAll(Env.DELIVERY_SYNC_MAC_BLOCK_RECORD_SCRIPT_CODE);
			//show interfaces status disabled
		}
		if (isAdmin) {
			// 若使用者為管理者，多查出LC Layer Device
			searchLayer.add(Env.DEVICE_LAYER_LC);
		}
		
		if (StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            deviceList.addAll(findGroupDeviceOfSpecifyLayer(brVO.getQueryGroupId(),searchLayer));
        } else if (brVO.getQueryGroupIdList() != null && !brVO.getQueryGroupIdList().isEmpty()) {
        	for (String groupId : brVO.getQueryGroupIdList()) {
        		deviceList.addAll(findGroupDeviceOfSpecifyLayer(groupId, searchLayer));
			}
        }
		
		Map<String, BlockedRecordVO> result = new HashMap<String, BlockedRecordVO>();
		String reason = prtgLoginAccount + "點選同步Switch Mac封鎖記錄 按鈕";
		
		DeliveryServiceVO dsVO;
		DeliveryParameterVO dpVO;
		List<BlockedRecordVO> updateList = new ArrayList<>();
		BlockedRecordVO nowbrVO = null;
		String macAddress = null;
		ScriptInfo scriptInfo ;
		ScriptInfo info ;
		
		for (String scriptCode : scriptList) {
			for (DeviceList device : deviceList) {

				try {
					dpVO = new DeliveryParameterVO();
					dpVO.setGroupId(Arrays.asList(device.getGroupId()));
					dpVO.setDeviceId(Arrays.asList(device.getDeviceId()));
					dpVO.setScriptCode(scriptCode);
					dpVO.setVarKey(null);
					dpVO.setVarValue(null);
					dpVO.setReason(reason);

					dsVO = doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, prtgLoginAccount, reason, false);
					if (dsVO.getProvisionLog() != null) {
						String provisionLog = dsVO.getProvisionLog();						
						macAddress = null;

						scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
						if(provisionLog.contains(scriptInfo.getActionScript())) {
							provisionLog = provisionLog.substring(provisionLog.indexOf(scriptInfo.getActionScript())+scriptInfo.getActionScript().length());
						}
						
						while(provisionLog.contains("Drop")) {
							//2    3415.9ece.d9bb    STATIC      Drop
							macAddress = provisionLog.substring(provisionLog.indexOf(".")-4, provisionLog.indexOf("STATIC")).trim();
							provisionLog = provisionLog.substring(provisionLog.indexOf("Drop")+4).trim();

							nowbrVO = blockedRecordService.checkMacBlockedList(device.getGroupId(), device.getDeviceId(), macAddress,dbRecordList);
														
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("Mac封鎖同步");
								
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.MAC_BLOCK.toString(), device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getMacAddress(), nowbrVO);
								updateList.add(nowbrVO);
							}else {								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getMacAddress(), nowbrVO);
							}
						}
					}

				} catch (Exception e) {
					log.error(e.toString(), e);

					// 設備執行腳本失敗則跳過此設備
					break;
				}

			}
		}
		
		updateList.addAll(blockedRecordService.compareMacBlockedList(dbRecordList, result));
		
		if (updateList != null && !updateList.isEmpty()) {
			blockedRecordService.saveOrUpdateRecord(updateList);
			return true;
        }
		return false;
	}
	
	@Override
    public  DeliveryParameterVO checkB4DoBindingDelivery(DeliveryParameterVO pVO) throws ServiceLayerException {
		//檢核IP MAC 綁定相關，放入global參數
		List<String> scriptListBind = new ArrayList<String>();
		List<String> scriptListUnbind = new ArrayList<String>();
		if (Env.SCRIPT_CODE_OF_IP_MAC_BIND != null) {
			scriptListBind.addAll(Env.SCRIPT_CODE_OF_IP_MAC_BIND);
		}
		if (Env.SCRIPT_CODE_OF_IP_MAC_UNBIND != null) {
			scriptListUnbind.addAll(Env.SCRIPT_CODE_OF_IP_MAC_UNBIND);
		}
		
		if(scriptListBind.contains(pVO.getScriptCode()) || scriptListUnbind.contains(pVO.getScriptCode())) {
			List<String> varKey = pVO.getVarKey();
			List<List<String>> varValue = pVO.getVarValue();
			List<List<String>> newVarValue = new ArrayList<List<String>>();
			List<String> deviceIdList = pVO.getDeviceId();
			
			ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
			siDAOVO.setQueryScriptInfoId(pVO.getScriptInfoId());
			siDAOVO.setQueryScriptCode(pVO.getScriptCode());
			List<ScriptInfo> scriptList = scriptInfoDAO.findScriptInfo(siDAOVO, null, null);

			if (scriptList == null || (scriptList != null && scriptList.isEmpty())) {
				throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
			}

			ScriptInfo dbEntity = scriptList.get(0);
			
			// Step 3.檢核JSON內VarKey與系統內設定的腳本變數欄位是否相符
			final String dbVarKeyJSON = dbEntity.getCheckScriptVariable();
			final List<String> dbVarKeyList = (List<String>) transJSON2Object(dbVarKeyJSON, ArrayList.class);
			String keyGlobal = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
			String keyNoFlag = Env.KEY_VAL_OF_NO_FLAG_WITH_CMD.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
			
			if(dbVarKeyList != null && dbVarKeyList.size() > 0 && 
					StringUtils.isNotBlank(keyGlobal) 
					&& StringUtils.isNotBlank(keyNoFlag)) {
				
				int addKey = 0;
				if(dbVarKeyList.contains(keyGlobal)) {
					varKey.add(keyGlobal);
					addKey++;
				}
				if(dbVarKeyList.contains(keyNoFlag)) {
					varKey.add(keyNoFlag);	
					addKey++;
				}
				
				pVO.setVarKey(varKey);
				
				BlockedRecordVO brVO;
	            List<ModuleBlockedList> recordList = null;            	              
            	List<String> valueList = null;
            	Map<String, String>varMap = null;
            	Map<String, Integer>recordPortMap = null;
            	String compareValue = null, currentPort4Unbind = null;
            	String noFlagValue = "";
            	
				int deviceCount = deviceIdList == null ? 0: deviceIdList.size();
				//逐筆設備替換
				for (int idx = 0; idx < deviceCount; idx++) {
	                varMap = new HashMap<String, String>();
	                valueList = varValue.get(idx);
	                for(int i=0; i < varKey.size()-addKey; i++) {
	                	varMap.put(varKey.get(i), valueList.get(i));
                	}
	                
	                brVO = new BlockedRecordVO();  
	                brVO.setQueryDeviceId(deviceIdList.get(idx));
	                brVO.setQueryBlockType(BlockType.IP_MAC.toString());
					brVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));	                
	                recordList = blockedRecordService.findModuleBlockedList(brVO);

	                recordPortMap = new HashMap<String, Integer>();
	                for(ModuleBlockedList bVO : recordList) {
	                	if(!recordPortMap.containsKey(bVO.getPort())) {
	                		recordPortMap.put(bVO.getPort(), 1);
	                	}else {
	                		recordPortMap.put(bVO.getPort(), recordPortMap.get(bVO.getPort())+1);
	                	}
	                	//如果比對IP、MAC與紀錄相同為解鎖作業
	                	if(StringUtils.equalsIgnoreCase(bVO.getIpAddress(), varMap.get(Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "")))
	                			&& StringUtils.equalsIgnoreCase(bVO.getMacAddress(), varMap.get(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "")))) {
	                		currentPort4Unbind = bVO.getPort();
	                	}
					}
	                
	                
	                if(varKey.contains(keyGlobal)) {
		                //綁定行為時，如果同設備中沒有其他封鎖紀錄使用相同port，global加入該port
		                if(scriptListBind.contains(pVO.getScriptCode())) {
		                	compareValue = varMap.get(Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""));
		                	if(recordPortMap.size() == 0) {
		                		valueList.add(compareValue);
		                		
		                	}else {		                		
		                		if(recordList.get(0).getGlobalValue().contains(compareValue)) {
		                			//有相同時，放進相同值
		                			valueList.add(recordList.get(0).getGlobalValue());
		                		}else {
		                			valueList.add(recordList.get(0).getGlobalValue()+","+compareValue); 
		                		}
		                	}
		                	noFlagValue = "";             	
		                }else {
		                	//解除綁定，如果同設備中沒有其他封鎖紀錄使用相同port，global取消該port		                	
	                		if(StringUtils.isEmpty(currentPort4Unbind)) {
	                			throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
	                		}
		                	if(!recordList.get(0).getGlobalValue().equalsIgnoreCase(currentPort4Unbind) && recordPortMap.get(currentPort4Unbind) == 1) {
		                		String globalValue = recordList.get(0).getGlobalValue().replaceAll(currentPort4Unbind+",", "");
	                			globalValue = globalValue.replaceAll(","+currentPort4Unbind+",", "");
	                			globalValue = globalValue.replaceAll(","+currentPort4Unbind, "");
	                			valueList.add(globalValue); 
	                			noFlagValue = "" ;//no flag 放空值	                			        		
		                	}else {
		                		valueList.add(recordList.get(0).getGlobalValue());
		                		if(recordPortMap.get(currentPort4Unbind) == 1) {
		                			noFlagValue = "no" ;//no flag 放值
		                		}else {
		                			noFlagValue = "" ;//no flag 放空值
		                		}
		                	}              	
		                }   
	                }	                	
	                
	                if(varKey.contains(keyNoFlag)) {
	                	valueList.add(noFlagValue);
	                }
	                
	                newVarValue.add(valueList);
				}
				pVO.setVarValue(newVarValue);
			}
		}
		
		return pVO;
	}
	
	@Override
    public  DeliveryParameterVO checkB4DoIpMacOpenBlockDelivery(DeliveryParameterVO pVO) throws ServiceLayerException {
		//檢核IP MAC 綁定相關，放入global參數
		List<String> macOpenScriptCodeList = Env.SCRIPT_CODE_OF_MAC_OPEN;
        List<String> macBlockScriptCodeList = Env.SCRIPT_CODE_OF_MAC_BLOCK;
		
        List<String> ipOpenScriptCodeList = Env.SCRIPT_CODE_OF_IP_OPEN;
        List<String> ipBlockScriptCodeList = Env.SCRIPT_CODE_OF_IP_BLOCK;
        
        String blockType = null;
        boolean blockFlag = false;
        if(macOpenScriptCodeList.contains(pVO.getScriptCode())) {
        	blockType = BlockType.MAC.toString();    
        	
        }else if (macBlockScriptCodeList.contains(pVO.getScriptCode())) {
        	blockType = BlockType.MAC.toString();
        	blockFlag = true;
        	
        }else if (ipOpenScriptCodeList.contains(pVO.getScriptCode())) {
        	blockType = BlockType.IP.toString();
        	
        }else if(ipBlockScriptCodeList.contains(pVO.getScriptCode())) {
        	blockType = BlockType.IP.toString();
        	blockFlag = true;
        	
        }
        
		if(StringUtils.isNoneBlank(blockType)) {
			List<String> varKey = pVO.getVarKey();
			List<List<String>> varValue = pVO.getVarValue();
			List<List<String>> newVarValue = new ArrayList<List<String>>();
			List<String> deviceIdList = pVO.getDeviceId();
			
			ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
			siDAOVO.setQueryScriptInfoId(pVO.getScriptInfoId());
			siDAOVO.setQueryScriptCode(pVO.getScriptCode());
			List<ScriptInfo> scriptList = scriptInfoDAO.findScriptInfo(siDAOVO, null, null);

			if (scriptList == null || (scriptList != null && scriptList.isEmpty())) {
				throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
			}

			ScriptInfo dbEntity = scriptList.get(0);
			
			// Step 3.檢核JSON內VarKey與系統內設定的腳本變數欄位是否相符
			final String dbVarKeyJSON = dbEntity.getCheckScriptVariable();
			final List<String> dbVarKeyList = (List<String>) transJSON2Object(dbVarKeyJSON, ArrayList.class);
			String keyGlobal = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
			
			if(dbVarKeyList != null && dbVarKeyList.size() > 0 && StringUtils.isNotBlank(keyGlobal) ) {
				
				if(dbVarKeyList.contains(keyGlobal)) {
					varKey.add(keyGlobal);
				}
				
				pVO.setVarKey(varKey);
				
				BlockedRecordVO brVO;           	              
            	List<String> valueList = null;
            	Map<String, String>varMap = null;
            	
				int deviceCount = deviceIdList == null ? 0: deviceIdList.size();
				//逐筆設備替換
				for (int idx = 0; idx < deviceCount; idx++) {
	                varMap = new HashMap<String, String>();
	                valueList = varValue.get(idx);
	                for(int i=0; i < varKey.size() -1; i++) {
	                	varMap.put(varKey.get(i), valueList.get(i));
                	}

	                brVO = new BlockedRecordVO();  
	                brVO.setQueryDeviceId(deviceIdList.get(idx));
	                brVO.setQueryBlockType(blockType);
					brVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
					
	                if(varKey.contains(keyGlobal)) {
	                	
		                //綁定行為時，計算同設備資料筆數放入Entry Num
		                if(blockFlag) {                
								List<ModuleBlockedList> bList = blockedRecordService.findModuleBlockedList(brVO);
								
								//從250往回設定
								for ( int i = 250; i <= 1 ; i--) {
									boolean checkFlag = true;
									for(ModuleBlockedList record : bList) {
										if(StringUtils.equals(record.getGlobalValue(), String.valueOf(i))) {
											checkFlag = false;
											break;
										}
										
									}
									if(checkFlag) {
										valueList.add( String.valueOf(i));
										break;
									}
								}
		                }else {
		                	//解除綁定，放入紀錄中global Value 的Entry Num
		                	brVO.setQueryMacAddress(varMap.get(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "")));
		                	List<ModuleBlockedList> recordList = blockedRecordService.findModuleBlockedList(brVO);
		                	
		                	if(recordList != null && recordList.size() >0) {
		                		valueList.add(recordList.get(0).getGlobalValue());
		                	}else {
		                		throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
		                	}
		                }   
	                }
	                newVarValue.add(valueList);
				}
				pVO.setVarValue(newVarValue);
			}
		}
		
		return pVO;
	}
}
