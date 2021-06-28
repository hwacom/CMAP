package com.cmap.plugin.module.circuit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
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
	public List<CircuitOpenListVO> findModuleE1OpenList(CircuitOpenListVO cVO) {
    	
    	List<ModuleCircuitE1OpenList> dataList = circuitDAO.findModuleE1OpenList(cVO);
    	List<CircuitOpenListVO> resultList = new ArrayList<>();
    	CircuitOpenListVO currVO;
    	for(ModuleCircuitE1OpenList data : dataList) {
    		currVO = new CircuitOpenListVO();
    		BeanUtils.copyProperties(data, currVO);
    		currVO.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(data.getUpdateTime()));
    		
    		resultList.add(currVO);
    	}
    	return resultList;
    }

	@Override
	public List<Map<String, Object>> findModuleCircuitDiagramInfo(CircuitVO cVO) throws ServiceLayerException {
		List<Object[]> entities = circuitDAO.findModuleInfoAndList(cVO);
		//mcdi.neName, mcdi.e1gwIp, mcdi.srPrefixSid, mcdi.localAreaCsrIp, mcbl.srcPortNumber 

		Map<String, Object> dataMap = new HashMap<>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<String> portList = new ArrayList<String>();
		
        for (Object[] entity : entities) {
        	String neName = Objects.toString(entity[0], "");
        	String e1gwIp = Objects.toString(entity[1], "");
        	String srPrefixSid = Objects.toString(entity[2], "");
        	String localAreaCsrIp = Objects.toString(entity[3], "");
        	String srcPortNumber = Objects.toString(entity[4], "");
            
        	if(!dataMap.isEmpty() && StringUtils.equalsIgnoreCase(srPrefixSid, (String)dataMap.get("sid"))) {
        		portList.add(srcPortNumber);
        		
        	}else {
        		if(portList.size() > 0) {
					Collections.sort(portList, new Comparator<String>() {
						public int compare(String s1, String s2) {
							try {
								return Integer.parseInt(s1) - Integer.parseInt(s2);
							} catch (NumberFormatException e) {
								return s1.compareTo(s2);
							}
						}
					});
        			
        			dataMap.put("port", portList);
            		dataList.add(dataMap);
            		
            		portList = new ArrayList<String>();
        		}
        		
        		dataMap = new HashMap<>();
            	dataMap.put("neName", neName);
            	dataMap.put("e1gwIp", e1gwIp);
            	dataMap.put("sid", srPrefixSid);
            	dataMap.put("csrIp", localAreaCsrIp);
            	portList.add(srcPortNumber);
        	}
        	
        }
        
        if(portList.size() > 0) {
        	Collections.sort(portList, new Comparator<String>() {
				public int compare(String s1, String s2) {
					try {
						return Integer.parseInt(s1) - Integer.parseInt(s2);
					} catch (NumberFormatException e) {
						return s1.compareTo(s2);
					}
				}
			});
        	
			dataMap.put("port", portList);
    		dataList.add(dataMap);
		}
		
		return dataList;
	}

	@Override
	public List<ModuleCircuitDiagramInfo> findModuleCircuitDiagramInfoByVO(CircuitVO cVO){
		return circuitDAO.findModuleInfoAndListByVO(cVO);
	}
	
	@Override
	public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramSetting(CircuitOpenListVO cVO) {
    	return circuitDAO.findModuleCircuitDiagramSetting(cVO);
    }
	
	@Override
	public boolean saveOrUpdateCircuitData(List<Object> entities)  {
		circuitDAO.saveOrUpdateCircuitData(entities);
		return true;
	}
	
	@Override
	public boolean deleteCircuitData(List<Object> entities)  {
		circuitDAO.deleteCircuitData(entities);
		return true;
	}
	
	@Override
	public Map<String, Object> doE1OpenProvision(JsonNode jsonData)  {
		
		String retVal = "";
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> showResult = new ArrayList<>();
		
		//"lci", "rci", "sei", "dei", "ses", "des", "sp", "dp"
		String localCsrIp = jsonData.get("lci").textValue();
		String remoteCsrIp = jsonData.get("rci").textValue();
		String srcE1Ip = jsonData.get("sei").textValue();
		String dstE1Ip = jsonData.get("dei").textValue();
		String srcE1Sid = jsonData.get("ses").textValue();
		String dstE1Sid = jsonData.get("des").textValue();
		String srcPort = jsonData.get("sp").textValue();
		String dstPort = jsonData.get("dp").textValue();
		
		//for show
		boolean show = jsonData.has("s")? StringUtils.equalsIgnoreCase(jsonData.get("s").textValue(), Constants.DATA_Y) : false;
		String username = jsonData.get("user").textValue();
		
		List<List<String>> valueList = new ArrayList<>();
		List<String> varValue;
		DeliveryServiceVO retVO = null;
		List<CircuitOpenListVO> e1List = new ArrayList<>(); 
		DeliveryParameterVO dpVO = new DeliveryParameterVO();
		String srcDeviceId, dstDeviceId;
		String srcRemark = "";
		String dstRemark = "";
		
		try {
			CircuitOpenListVO cVO = new CircuitOpenListVO();
			cVO.setQuerySrcE1gwSID(srcE1Sid);
			cVO.setQueryDstE1gwSID(dstE1Sid);
			cVO.setQuerySrcPort(srcPort);
			cVO.setQueryDstPort(dstPort);
			e1List = findModuleE1OpenList(cVO);
			
			if(!e1List.isEmpty() && e1List != null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "該線路已開通!");
	            return data;
			}
			
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(username);
			DeviceList srcDevice = deviceDAO.findDeviceListByDeviceIp(srcE1Ip);
			DeviceList dstDevice = deviceDAO.findDeviceListByDeviceIp(dstE1Ip);
			
			if(srcDevice == null || dstDevice == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
	            return data;
			}

			/**
			 * SrcNode_to_DstNode_Path_1th 
			 * SrcNode_to_DstNode_Path_2th
			 * SrcNode_to_DstNode_Path_3th
			 *  LOCAL_AREA_CSR_IP
			 *  REMOTE_AREA_CSR_IP
			 *  DST_E1GW_IP
			 *  DST_E1GW_SID
			 *  PORT_NUMBER
			 *  PSEUDOWIRE_ID = "10"+ port
			 */
			String[] columns = new String[] {"PATH_1TH", "PATH_2TH", "PATH_3TH", "LOCAL_AREA_CSR_IP", "REMOTE_AREA_CSR_IP", "DST_E1GW_IP", "DST_E1GW_SID", "PORT_NUMBER", "PSEUDOWIRE_ID"};
			
			/**
			 * 1.Source Device
			 * ["PATH_1TH", "PATH_2TH", "PATH_3TH", "LOCAL_AREA_CSR_IP", "REMOTE_AREA_CSR_IP", "DST_E1GW_IP", "DST_E1GW_SID", "PORT_NUMBER", "PSEUDOWIRE_ID"]
			 */
			varValue = new ArrayList<>();
			//1. src Device
			varValue.add(StringUtils.upperCase(srcE1Sid.concat("_to_").concat(dstE1Sid).concat("_Path_1th")));
			varValue.add(StringUtils.upperCase(srcE1Sid.concat("_to_").concat(dstE1Sid).concat("_Path_2th")));
			varValue.add(StringUtils.upperCase(srcE1Sid.concat("_to_").concat(dstE1Sid).concat("_Path_3th")));
			varValue.add(localCsrIp);
			varValue.add(remoteCsrIp);
			varValue.add(dstE1Ip);
			varValue.add(dstE1Sid);
			varValue.add(dstPort);
			varValue.add("10".concat(dstPort));
			for(int i = 0; i < columns.length ; i++) {
				if(i != 0) {
					srcRemark = srcRemark.concat(", ");
				}
				srcRemark = srcRemark.concat(columns[i]).concat("=").concat(varValue.get(i));
			}
			valueList.add(varValue);
			
			
			/**
			 * 2.Dst Device
			 */
			varValue = new ArrayList<>();
			varValue.add(StringUtils.upperCase(dstE1Sid.concat("_to_").concat(srcE1Sid).concat("_Path_1th")));
			varValue.add(StringUtils.upperCase(dstE1Sid.concat("_to_").concat(srcE1Sid).concat("_Path_2th")));
			varValue.add(StringUtils.upperCase(dstE1Sid.concat("_to_").concat(srcE1Sid).concat("_Path_3th")));
			varValue.add(remoteCsrIp);
			varValue.add(localCsrIp);
			varValue.add(srcE1Ip);
			varValue.add(srcE1Sid);
			varValue.add(srcPort);
			varValue.add("10".concat(srcPort));
			for(int i = 0; i < columns.length ; i++) {
				if(i != 0) {
					dstRemark = dstRemark.concat(", ");
				}
				dstRemark = dstRemark.concat(columns[i]).concat("=").concat(varValue.get(i));
			}
			valueList.add(varValue);
			
			srcDeviceId = srcDevice.getDeviceId();
			dstDeviceId = dstDevice.getDeviceId();
			dpVO.setDeviceId(Arrays.asList(srcDevice.getDeviceId(), dstDevice.getDeviceId()));
			dpVO.setScriptCode("TRA_001");
			dpVO.setVarKey(Arrays.asList(new String[] {"PATH_1TH", "PATH_2TH", "PATH_3TH", "LOCAL_AREA_CSR_IP", "REMOTE_AREA_CSR_IP", "DST_E1GW_IP", "DST_E1GW_SID", "PORT_NUMBER", "PSEUDOWIRE_ID"}));
			dpVO.setVarValue(valueList);
		} catch (ServiceLayerException e) {
			log.error(e.toString(), e);
			data.put("infoMsg", "error");
			data.put("errMsg", "參數不正確!!");
            return data;
		}
		
		try {
			retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, false, username, null, true);

			if(retVO.getRetMsg().contains("error")) {
				data.put("infoMsg", "error");
				data.put("errMsg", retVO.getRetMsg());
	            return data;
			}
			
			CircuitOpenListVO cVO = new CircuitOpenListVO();
			cVO.setQuerySrcE1gwSID(srcE1Sid);
			cVO.setQuerySrcPort(srcPort);
			List<ModuleCircuitDiagramSetting> srcSetting = circuitDAO.findModuleCircuitDiagramSetting(cVO);
			cVO.setQuerySrcE1gwSID(dstE1Sid);
			cVO.setQuerySrcPort(dstPort);
			List<ModuleCircuitDiagramSetting> dstSetting = circuitDAO.findModuleCircuitDiagramSetting(cVO);
			
			List<Object> updateList = new ArrayList<>();
			ModuleCircuitDiagramSetting updateData = null;
			if(srcSetting.size() > 0) {
				updateData = srcSetting.get(0);
				updateData.setUsedFlag(Constants.DATA_Y);
				updateList.add(updateData);
			}
			if(dstSetting.size() > 0) {
				updateData = dstSetting.get(0);
				updateData.setUsedFlag(Constants.DATA_Y);
				updateList.add(updateData);
			}
			
			CircuitVO ciVO = new CircuitVO();
			ciVO.setQuerySrPrefixSidList(Arrays.asList(srcE1Sid, dstE1Sid));
			List<ModuleCircuitDiagramInfo> infoList = circuitDAO.findModuleInfoAndListByVO(ciVO);
			
			
			for(ModuleCircuitDiagramInfo info : infoList) {
				
				if(StringUtils.equals(srcE1Sid, info.getSrPrefixSid())){
					
					ModuleCircuitE1OpenList srcOpenData = new ModuleCircuitE1OpenList();
//					BeanUtils.copyProperties(info, srcOpenData);
					log.info("for debug info = " + info.getCircleName() + ", data = " + srcOpenData.getCircleName());
					srcOpenData.setCircleName(info.getCircleName());
					srcOpenData.setStationName(info.getStatinName());
					srcOpenData.setStationEngName(info.getStatinEngName());
					srcOpenData.setNeName(info.getNeName());
					srcOpenData.setCreateTime(currentTimestamp());
					srcOpenData.setCreateBy(currentUserName());
					srcOpenData.setUpdateTime(currentTimestamp());
					srcOpenData.setUpdateBy(currentUserName());
					
					srcOpenData.setDeviceId(srcDeviceId);
					srcOpenData.setSrcSid(srcE1Sid);
					srcOpenData.setDstSid(dstE1Sid);
					srcOpenData.setLocalCsrIp(localCsrIp);
					srcOpenData.setRemoteCsrIp(remoteCsrIp);
					srcOpenData.setSrcE1gwIp(srcE1Ip);
					srcOpenData.setDstE1gwIp(dstE1Ip);
					srcOpenData.setSrcPortNumber(srcPort);
					srcOpenData.setDstPortNumber(dstPort);
					srcOpenData.setRemark(srcRemark);
					
					updateList.add(srcOpenData);
				}else if(StringUtils.equals(dstE1Sid, info.getSrPrefixSid())){
					ModuleCircuitE1OpenList dstOpenData = new ModuleCircuitE1OpenList();
//					BeanUtils.copyProperties(info, dstOpenData);
					log.info("for debug info = " + info.getCircleName() + ", data = " + dstOpenData.getCircleName());
					dstOpenData.setCircleName(info.getCircleName());
					dstOpenData.setStationName(info.getStatinName());
					dstOpenData.setStationEngName(info.getStatinEngName());
					dstOpenData.setNeName(info.getNeName());
					dstOpenData.setCreateTime(currentTimestamp());
					dstOpenData.setCreateBy(currentUserName());
					dstOpenData.setUpdateTime(currentTimestamp());
					dstOpenData.setUpdateBy(currentUserName());
					
					dstOpenData.setDeviceId(dstDeviceId);
					dstOpenData.setSrcSid(dstE1Sid);
					dstOpenData.setDstSid(srcE1Sid);
					dstOpenData.setLocalCsrIp(remoteCsrIp);
					dstOpenData.setRemoteCsrIp(localCsrIp);
					dstOpenData.setSrcE1gwIp(dstE1Ip);
					dstOpenData.setDstE1gwIp(srcE1Ip);
					dstOpenData.setSrcPortNumber(dstPort);
					dstOpenData.setDstPortNumber(srcPort);
					dstOpenData.setRemark(dstRemark);
					
					updateList.add(dstOpenData);
				}
			}
			
			saveOrUpdateCircuitData(updateList);
			
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
			data.put("errMsg", "指令執行有誤，請洽資訊人員!!");
            return data;
		}
		
		data.put("infoMsg", retVal);
		data.put("errMsg", "");
		if(show) {
			data.put("r", showResult);
		}
		
		return data;
	}

	@Override
	public Map<String, Object> doL3BGPCircuitOpenProvision(JsonNode jsonData)  {
		
		String retVal = "";
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> showResult = new ArrayList<>();
		
		//"lci", "rci", "sei", "dei", "sp", "dp"
		String localCsrIp = jsonData.get("lci").textValue();
		String remoteCsrIp = jsonData.get("rci").textValue();
		String srcE1Ip = jsonData.get("sei").textValue();
		String dstE1Ip = jsonData.get("dei").textValue();
		String srcPort = jsonData.get("sp").textValue();
		String dstPort = jsonData.get("dp").textValue();
		int srcIdx = 1;
		int dstIdx = 1;
		int globalIdx = 160;
		
		//for show
		boolean show = jsonData.has("s")? StringUtils.equalsIgnoreCase(jsonData.get("s").textValue(), Constants.DATA_Y) : false;
		String username = jsonData.get("user").textValue();
		
		List<List<String>> valueList = new ArrayList<>();
		List<String> varValue;
		DeliveryServiceVO retVO = null;
		List<CircuitOpenListVO> e1List = new ArrayList<>(); 
		DeliveryParameterVO dpVO = new DeliveryParameterVO();
		String srcDeviceId, dstDeviceId;
		String srcRemark = "";
		String dstRemark = "";
		
		try {
			CircuitOpenListVO cVO = new CircuitOpenListVO();
			cVO.setQuerySrcE1gwIP(localCsrIp);
			cVO.setQueryDstE1gwIP(remoteCsrIp);
			cVO.setQuerySrcPort(srcPort);
			cVO.setQueryDstPort(dstPort);
			e1List = findModuleE1OpenList(cVO);
			if(!e1List.isEmpty() && e1List != null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "該線路已開通!");
	            return data;
			}
			
			cVO = new CircuitOpenListVO();
			cVO.setQuerySrcE1gwIP(localCsrIp);
			e1List = findModuleE1OpenList(cVO);
			if(!e1List.isEmpty() && e1List != null) srcIdx = e1List.size() + 1;
			
			cVO = new CircuitOpenListVO();
			cVO.setQuerySrcE1gwIP(remoteCsrIp);
			e1List = findModuleE1OpenList(cVO);
			if(!e1List.isEmpty() && e1List != null) dstIdx = e1List.size() + 1;
			
			cVO = new CircuitOpenListVO();
			e1List = findModuleE1OpenList(cVO);
			if(!e1List.isEmpty() && e1List != null) globalIdx = e1List.size() + 160;
			
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(username);
			DeviceList srcDevice = deviceDAO.findDeviceListByDeviceIp(srcE1Ip);
			DeviceList dstDevice = deviceDAO.findDeviceListByDeviceIp(dstE1Ip);
			
			if(srcDevice == null || dstDevice == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
	            return data;
			}

			/**
			 * 
				conf t
					router bgp 65001
					 bgp router-id %sei%
					 neighbor %dei% remote-as 65001
					 neighbor %dei% update-source lo
					 
					 address-family vpnv4 unicast
					 neighbor %dei% activate
					 exit-address-family
					 
					 address-family ipv4 vrf IPI
					 redistribute connected
					 exit-address-family
					exit
					
				
					interface %sp%.%idx%
					 ip vrf forwarding IPI
					 ip address 192.%idx2%.%idx%.1/24
					 encapsulation dot1q %idx%0
					 #mtu 9000
					exit
				exit
				wr
			 */
			String[] columns = new String[] {"sei", "dei", "sp", "idx", "idx2"};
			
			/**
			 * 1.Source Device
			 * ["sei", "dei", "sp", "idx", "idx2"]
			 */
			varValue = new ArrayList<>();
			//1. src Device
			varValue.add(localCsrIp);
			varValue.add(remoteCsrIp);
			varValue.add(srcPort);
			varValue.add(""+srcIdx);
			varValue.add(""+globalIdx);
			for(int i = 0; i < columns.length ; i++) {
				if(i != 0) {
					srcRemark = srcRemark.concat(", ");
				}
				srcRemark = srcRemark.concat(columns[i]).concat("=").concat(varValue.get(i));
			}
			valueList.add(varValue);
			
			
			/**
			 * 2.Dst Device
			 */
			varValue = new ArrayList<>();
			varValue.add(remoteCsrIp);
			varValue.add(localCsrIp);
			varValue.add(dstPort);
			varValue.add(""+dstIdx);
			varValue.add(""+(globalIdx+1));
			for(int i = 0; i < columns.length ; i++) {
				if(i != 0) {
					dstRemark = dstRemark.concat(", ");
				}
				dstRemark = dstRemark.concat(columns[i]).concat("=").concat(varValue.get(i));
			}
			valueList.add(varValue);
			
			srcDeviceId = srcDevice.getDeviceId();
			dstDeviceId = dstDevice.getDeviceId();
			dpVO.setDeviceId(Arrays.asList(srcDevice.getDeviceId(), dstDevice.getDeviceId()));
			dpVO.setScriptCode("TRA_002");
			dpVO.setVarKey(Arrays.asList(new String[] {"sei", "dei", "sp", "idx", "idx2"}));
			dpVO.setVarValue(valueList);
		} catch (ServiceLayerException e) {
			log.error(e.toString(), e);
			data.put("infoMsg", "error");
			data.put("errMsg", "參數不正確!!");
            return data;
		}
		
		try {
			retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, false, username, null, true);

			if(retVO.getRetMsg().contains("error")) {
				data.put("infoMsg", "error");
				data.put("errMsg", retVO.getRetMsg());
	            return data;
			}
			
			CircuitOpenListVO cVO = new CircuitOpenListVO();
			cVO.setQuerySrcE1gwIP(srcE1Ip);
			cVO.setQuerySrcPort(srcPort);
			List<ModuleCircuitDiagramSetting> srcSetting = circuitDAO.findModuleCircuitDiagramSetting(cVO);
			cVO.setQuerySrcE1gwIP(dstE1Ip);
			cVO.setQuerySrcPort(dstPort);
			List<ModuleCircuitDiagramSetting> dstSetting = circuitDAO.findModuleCircuitDiagramSetting(cVO);
			
			List<Object> updateList = new ArrayList<>();
			ModuleCircuitDiagramSetting updateData = null;
			if(srcSetting.size() > 0) {
				updateData = srcSetting.get(0);
				updateData.setUsedFlag(Constants.DATA_Y);
				updateList.add(updateData);
			}
			if(dstSetting.size() > 0) {
				updateData = dstSetting.get(0);
				updateData.setUsedFlag(Constants.DATA_Y);
				updateList.add(updateData);
			}
			
			CircuitVO ciVO = new CircuitVO();
			ciVO.setQueryE1gwIpList(Arrays.asList(srcE1Ip, dstE1Ip));
			List<ModuleCircuitDiagramInfo> infoList = circuitDAO.findModuleInfoAndListByVO(ciVO);
			
			
			for(ModuleCircuitDiagramInfo info : infoList) {
				
				if(StringUtils.equals(localCsrIp, info.getLocalAreaCsrIp())){
					
					ModuleCircuitE1OpenList srcOpenData = new ModuleCircuitE1OpenList();
//					BeanUtils.copyProperties(info, srcOpenData);
					log.info("for debug info = " + info.getCircleName() + ", data = " + srcOpenData.getCircleName());
					srcOpenData.setCircleName(info.getCircleName());
					srcOpenData.setStationName(info.getStatinName());
					srcOpenData.setStationEngName(info.getStatinEngName());
					srcOpenData.setNeName(info.getNeName());
					srcOpenData.setCreateTime(currentTimestamp());
					srcOpenData.setCreateBy(currentUserName());
					srcOpenData.setUpdateTime(currentTimestamp());
					srcOpenData.setUpdateBy(currentUserName());
					
					srcOpenData.setDeviceId(srcDeviceId);
					srcOpenData.setSrcSid("");
					srcOpenData.setDstSid("");
					srcOpenData.setLocalCsrIp("");
					srcOpenData.setRemoteCsrIp("");
					srcOpenData.setSrcE1gwIp(localCsrIp);
					srcOpenData.setDstE1gwIp(remoteCsrIp);
					srcOpenData.setSrcPortNumber(srcPort);
					srcOpenData.setDstPortNumber(dstPort);
					srcOpenData.setRemark(srcRemark);
					
					updateList.add(srcOpenData);
				}else if(StringUtils.equals(remoteCsrIp, info.getLocalAreaCsrIp())){
					ModuleCircuitE1OpenList dstOpenData = new ModuleCircuitE1OpenList();
//					BeanUtils.copyProperties(info, dstOpenData);
					log.info("for debug info = " + info.getCircleName() + ", data = " + dstOpenData.getCircleName());
					dstOpenData.setCircleName(info.getCircleName());
					dstOpenData.setStationName(info.getStatinName());
					dstOpenData.setStationEngName(info.getStatinEngName());
					dstOpenData.setNeName(info.getNeName());
					dstOpenData.setCreateTime(currentTimestamp());
					dstOpenData.setCreateBy(currentUserName());
					dstOpenData.setUpdateTime(currentTimestamp());
					dstOpenData.setUpdateBy(currentUserName());
					
					dstOpenData.setDeviceId(dstDeviceId);
					dstOpenData.setSrcSid("");
					dstOpenData.setDstSid("");
					dstOpenData.setLocalCsrIp("");
					dstOpenData.setRemoteCsrIp("");
					dstOpenData.setSrcE1gwIp(remoteCsrIp);
					dstOpenData.setDstE1gwIp(localCsrIp);
					dstOpenData.setSrcPortNumber(dstPort);
					dstOpenData.setDstPortNumber(srcPort);
					dstOpenData.setRemark(dstRemark);
					
					updateList.add(dstOpenData);
				}
			}
			
			saveOrUpdateCircuitData(updateList);
			
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
			data.put("errMsg", "指令執行有誤，請洽資訊人員!!");
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
