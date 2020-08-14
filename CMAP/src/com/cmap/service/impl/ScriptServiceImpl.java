package com.cmap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.cmap.dao.ScriptInfoDAO;
import com.cmap.dao.ScriptStepDAO;
import com.cmap.dao.ScriptTypeDAO;
import com.cmap.dao.vo.ScriptInfoDAOVO;
import com.cmap.dao.vo.ScriptStepDAOVO;
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
	@Qualifier("scriptStepActionDAOImpl")
	private ScriptStepDAO scriptStepActionDAO;

	@Autowired
	@Qualifier("scriptStepCheckDAOImpl")
	private ScriptStepDAO scriptStepCheckDAO;

	@Autowired
	private ScriptTypeDAO scriptTypeDAO;
	
	@Override
	public List<ScriptServiceVO> loadDefaultScript(String deviceListId, ScriptType scriptType) throws ServiceLayerException {
		
		DeviceList device = null;
		List<ScriptInfo> list =  new ArrayList<>();
		if (!StringUtils.equals(deviceListId, Constants.DATA_STAR_SYMBOL)) {
			device = deviceDAO.findDeviceListByDeviceListId(deviceListId);
		}
		
		ScriptInfo info = loadDefaultScriptInfo(device != null?device.getDeviceModel():Constants.DATA_STAR_SYMBOL, scriptType.toString(), null);
		
		if (info == null) {
        	throw new ServiceLayerException("未設定[" + scriptType + "]預設腳本");
        }
		
		List<ScriptStepDAOVO> daovoList = scriptStepActionDAO.findScriptStepByScriptInfoIdOrScriptCode(null, info.getScriptCode());

		List<ScriptServiceVO> script = new ArrayList<>();
		if (daovoList == null || (daovoList != null && daovoList.isEmpty())) {
			throw new ServiceLayerException("未設定[" + scriptType + "]預設腳本");
		} else {
			ScriptServiceVO ssVO;
			for (ScriptStepDAOVO daovo : daovoList) {
				ssVO = new ScriptServiceVO();
				BeanUtils.copyProperties(daovo, ssVO);

				script.add(ssVO);
			}
		}

		return script;
	}

    @Override
    public ScriptInfo loadDefaultScriptInfo(String deviceModel, String scriptType, String undoFlag)throws ServiceLayerException {
        List<ScriptInfo> retEntity = new ArrayList<>();

        ScriptServiceVO vo = findDefaultScriptInfoByScriptTypeAndSystemVersion(scriptType, deviceModel);
    	if(vo != null) {
    		ScriptInfo scriptInfo = new ScriptInfo();
    		BeanUtils.copyProperties(vo, scriptInfo);
    		retEntity.add(scriptInfo);
    	}

        if(retEntity.isEmpty()) {
        	retEntity = scriptInfoDAO.findScriptInfoByScriptTypeCode(scriptType, deviceModel);
        }
        
        if(retEntity.isEmpty() || retEntity == null) {
        	retEntity = scriptInfoDAO.findScriptInfoByScriptTypeCode(scriptType, Constants.DATA_STAR_SYMBOL);
        }
        
        for(ScriptInfo info : retEntity) {
        	if(StringUtils.isNotBlank(undoFlag) && StringUtils.equalsIgnoreCase(undoFlag, Constants.DATA_Y) && StringUtils.isBlank(info.getUndoScriptCode())) {
        		retEntity.remove(info);
        	}else if(StringUtils.isNotBlank(undoFlag) && StringUtils.equalsIgnoreCase(undoFlag, Constants.DATA_N) && StringUtils.isNotBlank(info.getUndoScriptCode())) {
        		retEntity.remove(info);
        	}
        }
        
        if (retEntity.isEmpty() || retEntity == null) {
        	throw new ServiceLayerException("未設定[" + scriptType + "]預設腳本");
        }
        
        return retEntity.get(0);
    }

	@Override
	public ScriptServiceVO findDefaultScriptInfoByScriptTypeAndSystemVersion(String scriptType, String deviceModel) {
		ScriptServiceVO retVO = null;
		try {
			ScriptInfo scriptInfo = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(scriptType, deviceModel);

			if (scriptInfo == null) {
				if (!StringUtils.equals(deviceModel, Constants.DATA_STAR_SYMBOL)) {
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
	
	@Override
	public List<ScriptServiceVO> loadSpecifiedScript(
	        String scriptInfoId, String scriptCode, List<Map<String, String>> varMapList, List<ScriptServiceVO> scripts, String scriptMode) throws ServiceLayerException {
		List<ScriptServiceVO> retScriptList = null;

		if (scripts != null && !scripts.isEmpty()) {
			return scripts;

		} else if (scripts == null) {
			scripts = new ArrayList<>();
		}

		List<ScriptStepDAOVO> daovoList = null;

		if (StringUtils.equals(scriptMode, Constants.SCRIPT_MODE_ACTION)) {
		    daovoList = scriptStepActionDAO.findScriptStepByScriptInfoIdOrScriptCode(scriptInfoId, scriptCode);

		} else if (StringUtils.equals(scriptMode, Constants.SCRIPT_MODE_CHECK)) {
		    daovoList = scriptStepCheckDAO.findScriptStepByScriptInfoIdOrScriptCode(scriptInfoId, scriptCode);
		}

		ScriptServiceVO ssVO;
		for (ScriptStepDAOVO daovo : daovoList) {
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
				retVO.setQueryScriptTypeCode(Arrays.asList(entity.getScriptType().getScriptTypeCode()));
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
					scriptInfoDAO.deleteScriptInfo(info, SecurityUtil.getSecurityUser().getUser().getUserName());
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
	public List<ScriptInfo> getScriptInfoByScriptTypeCode(String scriptTypeCode, String deviceModel) throws ServiceLayerException {
		return scriptInfoDAO.findScriptInfoByScriptTypeCode(scriptTypeCode, deviceModel);
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
