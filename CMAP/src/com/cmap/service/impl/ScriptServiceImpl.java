package com.cmap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.ScriptType;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.ScriptDefaultMappingDAO;
import com.cmap.dao.ScriptInfoDAO;
import com.cmap.dao.ScriptStepDAO;
import com.cmap.dao.ScriptTypeDAO;
import com.cmap.dao.vo.ScriptDAOVO;
import com.cmap.dao.vo.ScriptInfoDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.model.ScriptInfo;
import com.cmap.model.ScriptStepAction;
import com.cmap.security.SecurityUtil;
import com.cmap.service.ScriptService;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.utils.impl.CommonUtils;

@Service("scriptService")
@Transactional
public class ScriptServiceImpl extends CommonServiceImpl implements ScriptService {
	@Log
	private static Logger log;

	@Autowired
    private DeviceDAO deviceDAO;

	@Autowired
	private ScriptInfoDAO scriptInfoDAO;

	@Autowired
	private ScriptDefaultMappingDAO scriptListDefaultDAO;

	@Autowired
	@Qualifier("scriptStepActionDAOImpl")
	private ScriptStepDAO scriptStepActionDAO;

	@Autowired
	@Qualifier("scriptStepCheckDAOImpl")
	private ScriptStepDAO scriptStepCheckDAO;

	@Autowired
	private ScriptTypeDAO scriptTypeDAO;
	
	@Override
	public List<ScriptServiceVO> loadDefaultScript(String deviceListId, List<ScriptServiceVO> script, ScriptType type) throws ServiceLayerException {
		if (script != null && !script.isEmpty()) {
			return script;

		} else if (script == null) {
			script = new ArrayList<>();
		}

		DeviceList device = null;
		if (!StringUtils.equals(deviceListId, "*")) {
			device = deviceDAO.findDeviceListByDeviceListId(deviceListId);
		}

		String deviceModel = device != null ? device.getDeviceModel() : Env.MEANS_ALL_SYMBOL;
		final String scriptCode = scriptListDefaultDAO.findDefaultScriptCodeBySystemVersion(type, deviceModel);

		List<ScriptDAOVO> daovoList = scriptStepActionDAO.findScriptStepByScriptInfoIdOrScriptCode(null, scriptCode);

		if (daovoList == null || (daovoList != null && daovoList.isEmpty())) {
			if (!StringUtils.equals(deviceModel, Env.MEANS_ALL_SYMBOL)) {
				script = loadDefaultScript("*", script, type);	//帶入機器系統版本號查不到腳本時，將版本調整為*號後再查找一次預設腳本

			} else {
				throw new ServiceLayerException("未設定[" + type + "]預設腳本");
			}

		} else {
			if (script == null) {
				script = new ArrayList<>();
			}

			ScriptServiceVO ssVO;
			for (ScriptDAOVO daovo : daovoList) {
				ssVO = new ScriptServiceVO();
				BeanUtils.copyProperties(daovo, ssVO);

				script.add(ssVO);
			}
		}

		return script;
	}

    @Override
    public ScriptInfo loadDefaultScriptInfo(String deviceListId, ScriptType type)throws ServiceLayerException {
        ScriptInfo retEntity = null;

        DeviceList device = null;
        if (!StringUtils.equals(deviceListId, "*")) {
            device = deviceDAO.findDeviceListByDeviceListId(deviceListId);
        }

        String deviceModel = device != null ? device.getDeviceModel() : Env.MEANS_ALL_SYMBOL;
        String scriptCode = scriptListDefaultDAO.findDefaultScriptCodeBySystemVersion(type, deviceModel);

        if (scriptCode == null) {
            if (!StringUtils.equals(deviceModel, Env.MEANS_ALL_SYMBOL)) {
                scriptCode = scriptListDefaultDAO.findDefaultScriptCodeBySystemVersion(type, "*");  //帶入機器系統版本號查不到腳本時，將版本調整為*號後再查找一次預設腳本

            } else {
                throw new ServiceLayerException("未設定[" + type + "]預設腳本");
            }

            if (scriptCode == null) {
                throw new ServiceLayerException("未設定[" + type + "]預設腳本");
            }
        }

        // 查找 Script_Info 資料
        retEntity = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
        return retEntity;
    }

	@Override
	public List<ScriptServiceVO> loadSpecifiedScript(
	        String scriptInfoId, String scriptCode, List<Map<String, String>> varMapList, List<ScriptServiceVO> scripts, String scriptMode) throws ServiceLayerException {
		List<ScriptServiceVO> retScriptList = null;

		if (scripts != null && !scripts.isEmpty()) {
			return scripts;

		} else if (scripts == null) {
			scripts = new ArrayList<>();
		}

		List<ScriptDAOVO> daovoList = null;

		if (StringUtils.equals(scriptMode, Constants.SCRIPT_MODE_ACTION)) {
		    daovoList = scriptStepActionDAO.findScriptStepByScriptInfoIdOrScriptCode(scriptInfoId, scriptCode);

		} else if (StringUtils.equals(scriptMode, Constants.SCRIPT_MODE_CHECK)) {
		    daovoList = scriptStepCheckDAO.findScriptStepByScriptInfoIdOrScriptCode(scriptInfoId, scriptCode);
		}

		ScriptServiceVO ssVO;
		for (ScriptDAOVO daovo : daovoList) {
			ssVO = new ScriptServiceVO();
			BeanUtils.copyProperties(daovo, ssVO);

			scripts.add(ssVO);
		}

		if (scripts == null || (scripts != null && scripts.isEmpty())) {
			log.error("查無腳本資料 >> scriptInfoId: " + scriptInfoId + " , scriptCode: " + scriptCode);
			throw new ServiceLayerException("查無腳本資料，請重新操作");
		}

		// 有傳入參數MAP才需跑替換參數值流程
		if (varMapList != null && !varMapList.isEmpty()) {

		    retScriptList = new ArrayList<>();

			for (Map<String, String> varMap : varMapList) {
				for (ScriptServiceVO script : scripts) {
					ScriptServiceVO newVO = new ScriptServiceVO();
					BeanUtils.copyProperties(script, newVO);

					String cmd = newVO.getScriptContent();

					if (cmd.indexOf("%") != -1) {
						String[] strSlice = cmd.split("%");

						for (int i=0; i<strSlice.length; i++) {
							if (i % 2 == 0) {
								continue;

							} else {
								String varKey = Env.SCRIPT_VAR_KEY_SYMBOL + strSlice[i] + Env.SCRIPT_VAR_KEY_SYMBOL;

								if (!varMap.containsKey(varKey)) {
									throw new ServiceLayerException("錯誤的腳本變數");

								} else {
									cmd = cmd.replace(varKey, varMap.get(varKey));
									newVO.setScriptContent(cmd);
								}
							}
						}
					}

					retScriptList.add(newVO);
				}
			}

			return retScriptList;

		} else {
		    return scripts;
		}
	}

	@Override
	public ScriptServiceVO findDefaultScriptInfoByScriptTypeAndSystemVersion(String scriptType, String systemVersion) throws ServiceLayerException {
		ScriptServiceVO retVO = null;
		try {
			ScriptInfo scriptInfo = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(scriptType, systemVersion);

			if (scriptInfo == null) {
				if (!StringUtils.equals(systemVersion, Constants.DATA_STAR_SYMBOL)) {
					return findDefaultScriptInfoByScriptTypeAndSystemVersion(scriptType, Constants.DATA_STAR_SYMBOL);
				}

			} else {
				retVO = new ScriptServiceVO();
				BeanUtils.copyProperties(scriptInfo, retVO);
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return retVO;
	}

	private ScriptInfoDAOVO transServiceVO2ScriptInfoDAOVO(ScriptServiceVO ssVO) {
		ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
		BeanUtils.copyProperties(ssVO, siDAOVO);
		return siDAOVO;
	}

	@Override
	public long countScriptInfo(ScriptServiceVO ssVO) throws ServiceLayerException {
		long retCount = 0;
		ScriptInfoDAOVO siDAOVO;
		try {
			siDAOVO = transServiceVO2ScriptInfoDAOVO(ssVO);

			retCount = scriptInfoDAO.countScriptInfo(siDAOVO);

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢異常，請重新操作");
		}

		return retCount;
	}

	@Override
	public List<ScriptServiceVO> findScriptInfo(ScriptServiceVO ssVO, Integer startRow, Integer pageLength) throws ServiceLayerException {
		List<ScriptServiceVO> retList = new ArrayList<>();
		ScriptInfoDAOVO siDAOVO;
		try {
			siDAOVO = transServiceVO2ScriptInfoDAOVO(ssVO);

			List<ScriptInfo> entities = scriptInfoDAO.findScriptInfo(siDAOVO, startRow, pageLength);

			if (entities != null && !entities.isEmpty()) {
				ScriptServiceVO vo;
				for (ScriptInfo entity : entities) {
					vo = new ScriptServiceVO();
					BeanUtils.copyProperties(entity, vo);
					vo.setActionScript(StringUtils.replace(entity.getActionScript(), "\r\n", "<br>"));
					vo.setScriptTypeName(entity.getScriptType().getScriptTypeName());
					vo.setCreateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(entity.getCreateTime()));
					vo.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(entity.getUpdateTime()));
					vo.setEnableModify(Env.ENABLE_CM_SCRIPT_MODIFY);

					retList.add(vo);
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢異常，請重新操作");
		}

		return retList;
	}

	@Override
	public ScriptServiceVO getScriptInfoByScriptInfoId(String scriptInfoId) throws ServiceLayerException {
		ScriptServiceVO retVO = new ScriptServiceVO();
		try {
			ScriptInfo entity = scriptInfoDAO.findScriptInfoByIdOrCode(scriptInfoId, null);

			if (entity != null) {
				retVO.setScriptName(entity.getScriptName());
				retVO.setActionScript(entity.getActionScript());
				retVO.setCheckScript(entity.getCheckScript());
				retVO.setActionScriptVariable(entity.getActionScriptVariable());
				retVO.setScriptCode(entity.getScriptCode());
				retVO.setRemark(entity.getActionScriptRemark());
				retVO.setDeviceModel(entity.getDeviceModel());
				retVO.setQueryScriptTypeCode(entity.getScriptType().getScriptTypeCode());
				retVO.setScriptDefault(entity.getSystemDefault());

			} else {
				throw new ServiceLayerException("查無此腳本內容，請重新操作");
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢腳本內容異常，請重新操作");
		}
		return retVO;
	}
	
	@Override
	public ScriptServiceVO getScriptInfoByScriptCode(String scriptCode) throws ServiceLayerException {
		ScriptServiceVO retVO = new ScriptServiceVO();
		try {
			ScriptInfo entity = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);

			if (entity != null) {
				retVO.setScriptName(entity.getScriptName());
				retVO.setActionScript(entity.getActionScript());
				retVO.setCheckScript(entity.getCheckScript());
				retVO.setActionScriptVariable(entity.getActionScriptVariable());

			} else {
				throw new ServiceLayerException("查無此腳本內容，請重新操作");
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢腳本內容異常，請重新操作");
		}
		return retVO;
	}
	
	@Override
	public String deleteScriptInfoByIdOrCode(String scriptInfoId, String scriptCode) throws ServiceLayerException {
		String msg = "選取刪除資料；成功 !!";
		try {
			ScriptInfo info = scriptInfoDAO.findScriptInfoByIdOrCode(scriptInfoId, scriptCode);
			if(info != null) {
				if(StringUtils.equals(Constants.DATA_Y, info.getSystemDefault())) {
					msg = "選取刪除資料；失敗 !! 預設腳本不可進行刪除作業!!";
				}else {
					boolean result = scriptInfoDAO.deleteScriptInfo(info, SecurityUtil.getSecurityUser().getUser().getUserName());
				}
			}else {
				msg = "刪除資料發生錯誤，查無指定腳本，請洽系統管理員!!";
			}
			

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return CommonUtils.converMsg(msg, "");
	}

	@Override
	public com.cmap.model.ScriptType getScriptTypeByCode(String scriptTypeCode) {
		try {
			List<com.cmap.model.ScriptType> scriptTypeList = scriptTypeDAO.findScriptTypeByDefaultFlag(null);

			for (com.cmap.model.ScriptType type : scriptTypeList) {
				if(StringUtils.equals(type.getScriptTypeCode(), scriptTypeCode)) {
					return type;
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	

	@Override
	public String addOrModifyScriptInfo(ScriptInfo info) throws ServiceLayerException {
		try {
			ScriptInfo entity = scriptInfoDAO.findScriptInfoByIdOrCode(null, info.getScriptCode());
			
			final String username = SecurityUtil.getSecurityUser().getUser().getUserName();
			final Timestamp nowTimestamp = new Timestamp((new Date()).getTime());

			if (entity == null) {
				entity = info;
				entity.setCreateBy(username);
				entity.setCreateTime(nowTimestamp);
			}else {
				entity.setScriptCode(info.getScriptCode());
				entity.setScriptName(info.getScriptName());
				entity.setDeviceModel(info.getDeviceModel());
				entity.setActionScript(info.getActionScript());
				entity.setActionScriptRemark(info.getActionScriptRemark());
			}			
			
			entity.setUpdateBy(username);
			entity.setUpdateTime(nowTimestamp);

			for(ScriptStepAction stepAction : entity.getScriptStepActions()) {
				scriptStepActionDAO.delete(stepAction);
			}
			
			List<ScriptStepAction> actions = info.getScriptStepActions();
			for(ScriptStepAction action : actions) {
				action.setCreateBy(username);
				action.setCreateTime(nowTimestamp);
				action.setDeleteFlag(Constants.DATA_N);
				action.setRepeatFlag(Constants.DATA_N);
				action.setUpdateBy(username);
				action.setUpdateTime(nowTimestamp);
				action.setScriptInfo(entity);
			}
			entity.setScriptStepActions(actions);
			
			scriptInfoDAO.saveScriptInfo(entity);

		} catch (Exception e) {
			log.error(e.toString(), e);

		}

		String msg = "異動資料1筆；成功 !!";
		return CommonUtils.converMsg(msg, "");
	}
	

	@Override
	public ScriptInfo getScriptInfoEntityByScriptCode(String scriptCode) throws ServiceLayerException {
		return scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
	}
	
	@Override
	public String deleteScriptTypeByCode(String scriptTypeCode) throws ServiceLayerException {
		String msg = "選取刪除資料；成功 !!";
		try {
			com.cmap.model.ScriptType type = scriptTypeDAO.findScriptTypeNotDefaultByCode(scriptTypeCode);
			if(type != null) {
				if(StringUtils.equals(Constants.DATA_Y, type.getDefaultFlag())) {
					msg = "選取刪除資料；失敗 !! 預設腳本類別不可進行刪除作業!!";
				}else {
					type.setDefaultFlag(Constants.DATA_Y);
					scriptTypeDAO.saveOrUpdateScriptTypeByCode(type);
				}
			}else {
				msg = "刪除資料發生錯誤，查無指定腳本類別，請洽系統管理員!!";
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return CommonUtils.converMsg(msg, "");
	}
	
	@Override
	public String addOrModifyScriptType(com.cmap.model.ScriptType type) throws ServiceLayerException {
		try {
			com.cmap.model.ScriptType entity = scriptTypeDAO.findScriptTypeNotDefaultByCode(type.getScriptTypeCode());
			
			final String username = SecurityUtil.getSecurityUser().getUser().getUserName();
			final Timestamp nowTimestamp = new Timestamp((new Date()).getTime());

			if (entity == null) {
				entity = type;
				entity.setCreateBy(username);
				entity.setCreateTime(nowTimestamp);
				entity.setDefaultFlag(Constants.DATA_N);
				entity.setDeleteFlag(Constants.DATA_N);
			}else {
				entity.setScriptTypeName(type.getScriptTypeName());
			}			
			
			entity.setUpdateBy(username);
			entity.setUpdateTime(nowTimestamp);

			scriptTypeDAO.saveOrUpdateScriptTypeByCode(entity);

		} catch (Exception e) {
			log.error(e.toString(), e);

		}

		String msg = "異動資料成功 !! 腳本類別異動需重新點選功能頁方生效!";
		return CommonUtils.converMsg(msg, "");
	}
}
