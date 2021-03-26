package com.cmap.plugin.module.circuit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.service.DeliveryService;
import com.cmap.service.PrtgService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;
import com.cmap.service.vo.ProvisionParameterVO;
import com.cmap.service.vo.ScriptServiceVO;
import com.fasterxml.jackson.databind.JsonNode;

@Service("circuitService")
@Transactional
public class CircuitServiceImpl extends CommonServiceImpl implements CircuitService {
    @Log
    private static Logger log;

    @Autowired
    private CircuitDAO circuitDAO;
    
    @Autowired
	private DeviceDAO deviceDAO;
    
    @Autowired
	private PrtgService prtgService;
    
    @Autowired
	private DeliveryService deliveryService;
    
    @Override
	public List<ModuleCircuitE1OpenList> findModuleE1OpenList(CircuitVO cVO) {
    	return circuitDAO.findModuleE1OpenList(cVO);
    }

	@Override
	public List<ModuleCircuitDiagramInfo> findModuleCircuitDiagramInfo(CircuitVO cVO) throws ServiceLayerException {
		return circuitDAO.findModuleInfo(cVO);
	}

	@Override
	public boolean saveOrUpdateSetting(List<ModuleCircuitDiagramSetting> entities)  {
		circuitDAO.saveOrUpdateSetting(entities);
		return true;
	}
	
	@Override
	public boolean deleteSetting(String e1Ip)  {
		circuitDAO.deleteSetting(e1Ip);
		return true;
	}
	
	@Override
	public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramInfoSetting(String e1Ip) {
		return circuitDAO.findModuleSettingByIp(e1Ip);
	}
	

	@Override
	public Map<String, Object> doE1OpenProvision(JsonNode jsonData, String ip)  {
		
		String retVal = "";
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> showResult = new ArrayList<>();
		
		String DstIP = jsonData.get("DstIP").textValue();
		String DstNode = jsonData.get("DstNode").textValue();
		String DstPort = jsonData.get("DstPort").textValue();
		String DstName = jsonData.get("DstName").textValue();
		String SourceIP = jsonData.get("SourceIP").textValue();
		String SourceNode = jsonData.get("SourceNode").textValue();
		String SourcePort = jsonData.get("SourcePort").textValue();
		String SourceName = jsonData.get("SourceName").textValue();
		String PathNode = jsonData.get("PathNode").textValue();
		int SourceTunnel = 1, DstTunnel = 1;
		//for show
		boolean show = jsonData.has("s")? StringUtils.equalsIgnoreCase(jsonData.get("s").textValue(), Constants.DATA_Y) : false;
		String username = jsonData.get("user").textValue();
		
		List<List<String>> valueList = new ArrayList<>();
		List<String> varValue;
		List<ScriptServiceVO> cmdList = new ArrayList<>();
		DeliveryServiceVO retVO = null;
		List<ModuleCircuitE1OpenList> e1List = new ArrayList<>(); 
		DeliveryParameterVO dpVO = new DeliveryParameterVO();
		
		try {
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(username);
			DeviceList dstDevice = deviceDAO.findDeviceListByDeviceIp(DstIP);
			CircuitVO cVO = new CircuitVO();
			cVO.setQueryIp(DstIP);
			e1List = findModuleE1OpenList(cVO);
			if(!e1List.isEmpty() && e1List != null) {
				DstTunnel = Integer.parseInt(e1List.get(0).getTunnelId())+1;
			}
			
			DeviceList sourceDevice = deviceDAO.findDeviceListByDeviceIp(SourceIP);
			cVO.setQueryIp(SourceIP);
			e1List = findModuleE1OpenList(cVO);
			if(!e1List.isEmpty() && e1List != null) {
				SourceTunnel = Integer.parseInt(e1List.get(0).getTunnelId())+1;
			}
			
			if(dstDevice == null || sourceDevice == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
	            return data;
			}
			/**
			 * 1.Source Device
			 * ["PRI_PATH_NAME","NODE_DST","SEC_PATH_NAME","NODE_PATH","TUNNEL_ID","DST_NAME","DST_IP","PW_ID","DST_PORT"]
			 */
			varValue = new ArrayList<>();
			varValue.add(StringUtils.upperCase("TO"+DstName+"1"));
			varValue.add(DstNode);
			varValue.add(StringUtils.upperCase("TO"+DstName+"2"));
			varValue.add(PathNode);
			varValue.add(""+SourceTunnel);
			varValue.add(DstName);
			varValue.add(DstIP);
			varValue.add(SourceTunnel+"001");
			varValue.add(DstPort);
			valueList.add(varValue);
			
			/**
			 * 2.Dst Device
			 */
			varValue = new ArrayList<>();
			varValue.add(StringUtils.upperCase("TO"+SourceName+"1"));
			varValue.add(SourceNode);
			varValue.add(StringUtils.upperCase("TO"+SourceName+"2"));
			varValue.add(PathNode);
			varValue.add(""+DstTunnel);
			varValue.add(SourceName);
			varValue.add(SourceIP);
			varValue.add(DstTunnel+"001");
			varValue.add(SourcePort);
			valueList.add(varValue);
			
			dpVO.setDeviceId(Arrays.asList(dstDevice.getDeviceId(), sourceDevice.getDeviceId()));
			dpVO.setScriptCode("");// TODO ScriptCode
			dpVO.setVarKey(Arrays.asList(new String[] {"PRI_PATH_NAME","NODE_DST","SEC_PATH_NAME","NODE_PATH","TUNNEL_ID","DST_NAME","DST_IP","PW_ID","DST_PORT"}));
			dpVO.setVarValue(valueList);
		} catch (ServiceLayerException e) {
			data.put("infoMsg", "error");
			data.put("errMsg", "參數不正確!!");
            return data;
		}
		
		try {
			retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, false, username, null, true);

			log.debug("provision " + dpVO.getScriptCode() + " script actoin success!!");
			retVal = retVal.concat(dpVO.getScriptCode()).concat(" ").concat(retVO.getRetMsg()).concat(System.lineSeparator());

			if (show) {
				showResult.add(String.join(",", retVO.getCmdOutputList()).replaceAll(Env.COMM_SEPARATE_SYMBOL, ""));
				
				if(StringUtils.equalsIgnoreCase(Env.ENABLE_CMD_LOG, Constants.DATA_Y)) {
					log.debug("provision show =" + showResult);
					log.debug("provision log =" + retVO.getProvisionLog());						
				}					
			}

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
}
