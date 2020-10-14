package com.cmap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.ProvisionApiAccessLogDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.model.ProvisionApiAccessLog;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.ScriptInfo;
import com.cmap.service.DeliveryService;
import com.cmap.service.ProvisionApiService;
import com.cmap.service.PrtgService;
import com.cmap.service.ScriptService;
import com.cmap.service.VersionService;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;
import com.cmap.service.vo.ProvisionParameterVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.impl.EncryptUtils;
import com.fasterxml.jackson.databind.JsonNode;

@Service("provisionApiService")
@Transactional
public class ProvisionApiServiceImpl extends CommonServiceImpl implements ProvisionApiService {
	@Log
	private static Logger log;
	
	@Autowired
	private PrtgService prtgService;

	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private ScriptService scriptService;
	
	@Autowired
	private VersionService versionService;
	
	@Autowired
	private ProvisionApiAccessLogDAO provisionApiAccessLogDAO;

	@Autowired
	private DeviceDAO deviceDAO;
	
	@Override
	public ProvisionApiAccessLog findProvisionApiAccessLog(String checkHash) {
		return provisionApiAccessLogDAO.findProvisionApiAccessLogByHash(checkHash);
	}
	
	@Override
	public boolean saveProvisionApiLog(ProvisionApiAccessLog entity) {
		try {
			provisionApiAccessLogDAO.saveProvisionApiLog(entity);
			return true;
		}catch (Exception e) {
			log.error(e.toString());
			return false;
		}
		
	}
	
	@Override
	public Map<String, Object> getDefaultScriptInfo(JsonNode jsonData, String ip)  {
		
		String infoMsg = "success";
		String errMsg = "";
		Map<String, Object> data = new HashMap<String, Object>();
		ProvisionApiAccessLog paal = null;
		
		try {
			String scriptType = jsonData.has("type")? jsonData.get("type").textValue() : null;
			String scriptCode = jsonData.has("code")? jsonData.get("code").textValue() : null;
			String username = jsonData.get("user").textValue();
			String undoFlag = jsonData.has("undo")? jsonData.get("undo").textValue() : null;
			Iterator<JsonNode> idIt = jsonData.get("deviceId").iterator();

			if((StringUtils.isBlank(scriptType) && StringUtils.isBlank(scriptCode))
					|| (StringUtils.isNotBlank(scriptType) && StringUtils.isNotBlank(scriptCode))
					|| StringUtils.isBlank(username) || idIt == null || !idIt.hasNext()) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
	            return data;
			}

			try {
				PrtgAccountMapping mapping = prtgService.getMappingByAccount(username);
			} catch (ServiceLayerException e) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
	            return data;
			}
			
			//確認ScriptCode是否存在
			ScriptServiceVO vo = null;
			if(StringUtils.isNotBlank(scriptCode)) {
				vo = scriptService.getScriptInfoByScriptCode(scriptCode);
			}
			
			String id = null;
			ScriptInfo info = null;
			List<String> deviceList = new ArrayList<>();
			List<List<String>> varkeyList = new ArrayList<>();
			String deviceIds = null;
			String codeList = null;
			String varkey = null, varkeys = null;
			DeviceList dlEntity = null;
			
			while (idIt.hasNext()) {
				id = idIt.next().asText();
				if(StringUtils.isNumeric(id)) {
					deviceList.add(id);
					deviceIds = deviceIds == null? id : deviceIds.concat(Env.COMM_SEPARATE_SYMBOL).concat(id);
					
					if(StringUtils.isNotBlank(scriptCode)) {
						codeList = scriptCode;
						varkey = vo.getActionScriptVariable() == null ? "  " :vo.getActionScriptVariable().replaceAll("\"", "");
						varkey = varkey.substring(1, varkey.length()-1);
					}else {
						dlEntity = deviceDAO.findDeviceListByGroupAndDeviceId(null, id);
						info = scriptService.loadDefaultScriptInfo(dlEntity.getDeviceModel(), scriptType, undoFlag);
						codeList = codeList == null ? info.getScriptCode() : codeList.concat(Env.COMM_SEPARATE_SYMBOL).concat(info.getScriptCode());
						varkey = info.getActionScriptVariable() == null ? "" :info.getActionScriptVariable().replaceAll("\"", "").substring(1, varkey.length()-1);
					}
					
					if(StringUtils.isNotBlank(varkey)) {						
						varkeys = varkeys == null ? varkey : varkeys.concat(Env.COMM_SEPARATE_SYMBOL).concat(varkey);
						varkeyList.add(Arrays.asList(varkey.split(",")));
						
					}else {
						varkeys = varkeys == null ? "" : varkeys.concat(Env.COMM_SEPARATE_SYMBOL).concat("");
						varkeyList.add(new ArrayList<>());
					}
				}
				
			}
			
			String checkHash = StringUtils.upperCase(EncryptUtils.getSha256(username.concat(new Date().toString())));
			
			paal = new ProvisionApiAccessLog();
			paal.setCheckHash(checkHash);
			paal.setUserIp(ip);
			paal.setUserName(username);
			paal.setAccessTime(new Timestamp((new Date()).getTime()));
			paal.setAction("GET");
			paal.setDeviceIds(deviceIds);
			paal.setScriptCode(codeList);
			paal.setVarKey(varkeys);
			
			saveProvisionApiLog(paal);
			
			// Step 2. 回傳JSON格式
			data.put("hash", checkHash);
			data.put("deviceList", deviceList);
			data.put("varkey", varkeyList);
			data.put("infoMsg", infoMsg);
			data.put("errMsg", errMsg);
			
	    } catch (Exception e) {
            log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            return data;
	    }
		
		return data;
	}
	
	@Override
	public Map<String, Object> doApiProvision(JsonNode jsonData, String ip)  {
		
		ProvisionParameterVO jsVO = new ProvisionParameterVO();
		String retVal = "";
		Map<String, DeliveryParameterVO> dpVOs = new HashMap<>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> showResult = new ArrayList<>();
		
		convertJson2VO(jsVO, jsonData);
		//for show
		boolean show = jsonData.has("s")? StringUtils.equalsIgnoreCase(jsonData.get("s").textValue(), Constants.DATA_Y) : false;
		String username = jsonData.get("user").textValue();
		
		ProvisionApiAccessLog paal = findProvisionApiAccessLog(jsVO.getHash());
		DeliveryServiceVO retVO = null;
		if(paal == null) {
			data.put("infoMsg", "error");
			data.put("errMsg", "參數不正確!!");
            return data;
		}
		
		String[] scriptCodes = paal.getScriptCode().split(Env.COMM_SEPARATE_SYMBOL);
		if(scriptCodes.length != jsVO.getVarValue().size()) {
			data.put("infoMsg", "error");
			data.put("errMsg", "參數不正確!!");
            return data;
		}
		
		try {
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(username);
		} catch (ServiceLayerException e) {
			data.put("infoMsg", "error");
			data.put("errMsg", "參數不正確!!");
            return data;
		}
		
		if(scriptCodes.length > 1) {
			dpVOs = transferProvisionToDeliveryVO(jsVO, paal);
		}else {
			DeliveryParameterVO dpVO = new DeliveryParameterVO();
			dpVO.setDeviceId(Arrays.asList(paal.getDeviceIds()));
			dpVO.setScriptCode(paal.getScriptCode());
			dpVO.setVarKey(Arrays.asList(paal.getVarKey().split(",")));
			dpVO.setVarValue(jsVO.getVarValue());
			
			dpVOs.put(paal.getScriptCode(), dpVO);
		}
		
		try {
			for (String key : dpVOs.keySet()) {
				DeliveryParameterVO dpVO = dpVOs.get(key);

				dpVO = deliveryService.checkB4DoSpecialScript(dpVO);

				retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, false, username, null, true);

				log.debug("provision " + key + " script actoin success!!");
				retVal = retVal.concat(key).concat(" ").concat(retVO.getRetMsg()).concat(System.lineSeparator());

				if (show) {
					showResult.add(String.join(",", retVO.getCmdOutputList()).replaceAll(Env.COMM_SEPARATE_SYMBOL, ""));
					
					if(StringUtils.equalsIgnoreCase(Env.ENABLE_CMD_LOG, Constants.DATA_Y)) {
						log.debug("provision show =" + showResult);
						log.debug("provision log =" + retVO.getProvisionLog());						
					}					
				}
			}

			paal.setAction("DONE");
			paal.setActionIp(ip);
			paal.setActionUser(username);
			paal.setActionTime(new Timestamp((new Date()).getTime()));
			saveProvisionApiLog(paal);
		} catch (ServiceLayerException e) {
			log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            return data;
		}
		
		data.put("infoMsg", retVal);
		data.put("errMsg", "");
		if(show) {
			data.put("r", showResult);
		}
		
		return data;
	}

	private Map<String, DeliveryParameterVO> transferProvisionToDeliveryVO(ProvisionParameterVO jsVO, ProvisionApiAccessLog paal){
		Map<String, DeliveryParameterVO> dpVOs = new HashMap<>();
		
		DeliveryParameterVO dpVO = null;
		String[] scriptCodes = paal.getScriptCode().split(Env.COMM_SEPARATE_SYMBOL);
		String[] deviceIds = paal.getDeviceIds().split(Env.COMM_SEPARATE_SYMBOL);
		String[] varkeys = paal.getVarKey().split(Env.COMM_SEPARATE_SYMBOL);
		
		for (int i = 0; i < scriptCodes.length ; i++) {
			if(dpVOs.containsKey(scriptCodes[i])){
				
				dpVO = dpVOs.get(scriptCodes[i]);
				List<String> tempDeviceList = dpVO.getDeviceId();
				List<List<String>> tempVarValueList = dpVO.getVarValue();
				
				tempDeviceList.add(deviceIds[i]);
				tempVarValueList.add(jsVO.getVarValue().get(i));
				dpVO.setDeviceId(tempDeviceList);
				dpVO.setVarValue(tempVarValueList);
				
				dpVOs.put(dpVO.getScriptCode(), dpVO);
				
			}else {				
				dpVO = new DeliveryParameterVO();				
				dpVO.setDeviceId(Arrays.asList(deviceIds[i]));
				dpVO.setReason(jsVO.getReason());
				dpVO.setScriptCode(scriptCodes[i]);
				dpVO.setVarKey(Arrays.asList(varkeys[i].split(",")));
				dpVO.setVarValue(Arrays.asList(jsVO.getVarValue().get(i)));
				
				dpVOs.put(scriptCodes[i], dpVO);
			}
		}
		
		return dpVOs;
	}

	private void convertJson2VO(Object vo, final JsonNode jsonData) {

		Iterator<String> it = jsonData.fieldNames();

		while (it.hasNext()) {
			try {
				final String fieldName = it.next();
				Class<?> fieldNameType = vo.getClass().getDeclaredField(fieldName).getType();

				final JsonNode fieldNode = jsonData.findValue(fieldName);

				if (fieldNameType.isAssignableFrom(String.class)) {
					PropertyUtils.setProperty(vo, fieldName, fieldNode.asText());

				} else if (fieldNameType.isAssignableFrom(Integer.class)) {
					PropertyUtils.setProperty(vo, fieldName, Integer.parseInt(fieldNode.asText()));

				} else if (fieldNameType.isAssignableFrom(List.class)) {
					String[] nodeValues = null;
					List<Object> list = new ArrayList<>();
					Iterator<JsonNode> tmpNode = fieldNode.iterator();
					JsonNode curNode = null;
					String curText = null;
					List<String> tmpList = null;
					Iterator<JsonNode> secArray = null;
					
					while (tmpNode.hasNext()) {
						curNode = tmpNode.next();

						if (curNode.isArray()) {
							tmpList = new ArrayList<>();
							secArray = curNode.iterator();
							
							while(secArray.hasNext()) {
								tmpList.add(secArray.next().asText());
							}
							
							list.add(tmpList);
						} else {
							curText = curNode.asText();

							if (curText.indexOf("\r\n") != -1) {
								nodeValues = curText.split("\r\n");
								list.add(Arrays.asList(nodeValues));
							} else {
								list.add(curText);
							}
						}
					}

					PropertyUtils.setProperty(vo, fieldName, list);
				}
			}catch (NoSuchFieldException e) {
				// TODO: handle exception
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}
	
	@Override
	public Map<String, Object> doApiConfigBackup(JsonNode jsonData, String ip)  {
		
		//String infoMsg = "success";
		String errMsg = "";
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			// Step1.接收Json參數
			String deviceUserName = jsonData.get("user").textValue();
			String configType = jsonData.has("configType") ? jsonData.findValue("configType").asText() : null;
			String deviceIp = jsonData.get("deviceIp").textValue();

			if(StringUtils.isBlank(deviceUserName) || StringUtils.isBlank(deviceIp)) {
				log.error("deviceDAO.findDeviceListByDeviceIp miss param, deviceIp[ "+deviceIp+"]");
				log.error("deviceDAO.findDeviceListByDeviceIp miss param, deviceUserName[ "+deviceUserName+"]");
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
	            return data;
			}
			// Step2.使用傳入的設備IP查詢設備資料,非納管設備則視為例外
			List<String> deviceListIds = new ArrayList<>(); //配合原有backupConfig規格封裝為deviceListIds(但其實一次只會放一個IP)
			String deviceIds = null;
			DeviceList dlEntity = null;
			
			VersionServiceVO retVO = null;
			// 查device_list資料表
			dlEntity = deviceDAO.findDeviceListByDeviceIp(deviceIp);
			if (dlEntity == null) {
				log.error("deviceDAO.findDeviceListByDeviceIp , deviceIp["+deviceIp+"] is not in device_list");
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				return data;
			}
			log.debug("deviceDAO.findDeviceListByDeviceIp done");
			deviceListIds.add(dlEntity.getDeviceListId());
			deviceIds = deviceIds == null? dlEntity.getDeviceId() : deviceIds.concat(Env.COMM_SEPARATE_SYMBOL).concat(dlEntity.getDeviceId());
			// 傳入設備異動者資訊
			retVO = versionService.backupConfig(configType, deviceListIds, false, Constants.SYSLOG_BK_TRIGGER_NAME, deviceUserName, deviceIp);
			log.debug("versionService.backupConfig done");
			ProvisionApiAccessLog paal = new ProvisionApiAccessLog();
			String checkHash = StringUtils.upperCase(EncryptUtils.getSha256(deviceUserName.concat(new Date().toString())));
			paal.setCheckHash(checkHash);
			paal.setUserIp(ip); //呼叫API的來源ip
			paal.setUserName( Constants.SYSLOG_BK_TRIGGER_NAME); //呼叫API的來源用戶
			paal.setActionIp(deviceIp); //設備IP(from Json)
			paal.setActionUser(deviceUserName); //設備設定檔異動時使用的帳號(from Json)
			paal.setAccessTime(new Timestamp((new Date()).getTime()));
			paal.setActionTime(new Timestamp((new Date()).getTime()));
			paal.setAction("DONE");
			paal.setDeviceIds(deviceIds);
			paal.setScriptCode("");
			paal.setVarKey("");
			log.debug("ProvisionApiAccessLog.userName = " + Constants.SYSLOG_BK_TRIGGER_NAME);
			log.debug("ProvisionApiAccessLog.userIp = " + ip);
			log.debug("ProvisionApiAccessLog.actionUserName = " + deviceUserName);
			log.debug("ProvisionApiAccessLog.actionIp = " + deviceIp);
			saveProvisionApiLog(paal);
			log.debug("saveProvisionApiLog done");
			// Step 2. 回傳JSON格式
			data.put("infoMsg", retVO.getRetMsg());
			data.put("errMsg", errMsg);
			
	    } catch (Exception e) {
            log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            return data;
	    }
		
		return data;
	}
}
