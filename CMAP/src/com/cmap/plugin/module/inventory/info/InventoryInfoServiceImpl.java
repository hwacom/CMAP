package com.cmap.plugin.module.inventory.info;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.InventoryInfoVO;
import com.fasterxml.jackson.databind.JsonNode;

@Service("inventoryInfoService")
@Transactional
public class InventoryInfoServiceImpl extends CommonServiceImpl implements InventoryInfoService {
	@Log
	private static Logger log;

	@Autowired
	private InventoryInfoDAO inventoryInfoDAO;

	@Autowired
	private DatabaseMessageSourceBase messageSource;
	
	final String[] infoFieldNames = new String[] { "deviceId", "probe", "groupName", "deviceName", "deviceIp",
			"deviceType", "brand", "model", "systemVersion", "serialNumber", "manufactureDate", "status", "syncFlag",
			"diffrenceComparison", "uploadTime", "custodian", "department", "user", "northFlag", "remark" };
	
	final String[] serverDetailColumns = new String[] {"deviceId@~hide", "site@~", "siteModRsn@~", "accetNumber@~", "classUse@~selection", "classUseModRsn@~", "statusModRsn@~", "classPrimary@~", "isdmz@~", "idcId@~selection", "rackId@~", "enclosureNo@~", "fromRackUnit@~", "toRackUnit@~", "osName@~", "osVersion@~", "osBit@~", "machineType@~", "cpuSpecification@~", "cpuCore@~", "cpuSpeed@~", "cpuAmount@~", "memorySpecification@~", "memorySize@~", "hdSpecification@~", "publicLanIp@~", "clusterIp@~", "backupLanIp@~", "privateLanIp@~", "manager@~", "manager2@~", "buyDate@~", "startDate@~", "weDate@~", "insertRsn@~", "serviceType@~", "biosFirmware@~", "managementIp@~", "updateTime@~readonly", "updateBy@~readonly"};
	final String[] cellDetailColumns = new String[] {"deviceId@~hide", "frequencyBand@~", "amfIpAddress@~", "enodebType@~", "gnbId@~", "cellIdentify@~", "physicalCellGroupId@~", "physicalCellId@~", "plmn@~", "arfcn@~", "bandWidth@~", "currentTxPower@~", "updateTime@~readonly", "updateBy@~readonly"};
	final String[] vmDetailColumns = new String[] {"deviceId@~hide", "site@~", "siteModRsn@~", "classUse@~selection", "classUseModRsn@~", "classPrimary@~", "isdmz@~", "osName@~", "osVersion@~", "osBit@~", "cpuAmount@~", "memorySize@~", "hdSpecification@~", "tapeSpecification@~", "hdSize@~", "publicLanIp@~", "backupLanIp@~", "manager@~", "manager2@~", "serviceType@~", "building@~", "hypervisor@~", "updateTime@~readonly", "updateBy@~readonly"};
	final String[] storageDetailColumns = new String[] {"deviceId@~hide", "idcId@~selection", "site@~", "rackId@~", "fromRackUnit@~", "toRackUnit@~", "descriptions@~", "classUse@~selection", "storageType@~selection", "protocalLink@~", "controlFirmware@~", "accetNumber@~", "buyDate@~", "totalSpace@~", "usedSpace@~", "leftSpace@~", "updateTime@~readonly", "updateBy@~readonly"};
	final String[] upsDetailColumns = new String[] {"deviceId@~hide", "site@~", "building@~", "kva@~", "idcName@~", "buyDate@~", "updateTime@~readonly", "updateBy@~readonly"};
	
	@Override
	public List<InventoryInfoVO> findInventoryInfo(InventoryInfoVO iiVO) throws Exception {
		
		List<InventoryInfo> infoList = inventoryInfoDAO.findInventoryInfo(iiVO);
		
		if(infoList == null || infoList.isEmpty()) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		List<InventoryInfoVO> result = new ArrayList<>();
		InventoryInfoVO currVO = null;
		
		for(InventoryInfo info : infoList) {
			currVO = new InventoryInfoVO();
			BeanUtils.copyProperties(info, currVO);
			
			if(currVO.getGroupName().indexOf(" > ") > 0) {
				String[] names = currVO.getGroupName().split(" > ");
				currVO.setGroupName(names[0]);
				if(names.length > 1) currVO.setGroupName1(names[1]);
				if(names.length > 2) currVO.setGroupName2(names[2]);
				if(names.length > 3) currVO.setGroupName3(names[3]);
				if(names.length > 4) currVO.setGroupName4(names[4]);
			}
			currVO.setCreateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(info.getCreateTime()));
			currVO.setUploadTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(info.getUploadTime()));
			result.add(currVO);
		}
		
		return result;
	}

	@Override
	public String insertInventoryInfo(List<InventoryInfoVO> voList) {
		try {
			List<InventoryInfo> infoList = new ArrayList<>();
			InventoryInfo info;
			long maxDeviceId = -1;
			
			for(InventoryInfoVO vo : voList) {
				info = new InventoryInfo();
				BeanUtils.copyProperties(vo, info);
				
				if(maxDeviceId < 0) {
					String maxDeviceIdString = Objects.toString(inventoryInfoDAO.findMaxInventoryInfoDeviceId(), "0");
					maxDeviceId = Long.parseLong(maxDeviceIdString.replaceAll("UA", ""));
				}
				
				maxDeviceId ++;
				info.setDeviceId("UA" + String.format("%05d", maxDeviceId));
				info.setCreateTime(currentTimestamp());
				info.setCreateBy(currentUserName());
				info.setUpdateTime(currentTimestamp());
				info.setUpdateBy(currentUserName());
				info.setSyncFlag(Constants.DATA_N);
				infoList.add(info);

			}
			
			if(!infoList.isEmpty() && infoList.size() > 0) {
				inventoryInfoDAO.saveOrUpdateInventoryInfo(infoList);
			}
			
		}catch (Exception e) {
			return "新增失敗";
		}	
		
		return "新增成功" ;
	}
	
	@Override
	public String updateOrInsertInventoryInfo(List<InventoryInfoVO> voList) {
		String action = "更新";
		// 更新 or 寫入 DEVICE_LIST 資料
		try {
			List<InventoryInfo> infoList = new ArrayList<>();
			boolean updateFlag;
			InventoryInfo info;
			long maxDeviceId = -1;
			
			for(InventoryInfoVO vo : voList) {
				
				updateFlag = false;
				info = new InventoryInfo();
				BeanUtils.copyProperties(vo, info);
				
				if(StringUtils.isBlank(info.getDeviceId())) {
					if(maxDeviceId < 0) {
						String maxDeviceIdString = Objects.toString(inventoryInfoDAO.findMaxInventoryInfoDeviceId(), "0");
						maxDeviceId = Long.parseLong(maxDeviceIdString.replaceAll("UA", ""));
					}
					maxDeviceId ++;
					info.setDeviceId("UA" + String.format("%05d", maxDeviceId));
					info.setCreateTime(currentTimestamp());
					info.setCreateBy(currentUserName());
					action = "新增";
				}else {
					info = inventoryInfoDAO.findLastInventoryInfoByDeviceId(vo.getDeviceId());
				}
	
				if(!StringUtils.equals(vo.getModifyProbe(), info.getProbe())) {
					info.setProbe(vo.getModifyProbe());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyGroup(), info.getGroupName())) {
					info.setGroupName(vo.getModifyGroup());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyDeviceName(), info.getDeviceName())) {
					info.setDeviceName(vo.getModifyDeviceName());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyDeviceIp(), info.getDeviceIp())) {
					info.setDeviceIp(vo.getModifyDeviceIp());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyDeviceType(), info.getDeviceType())) {
					info.setDeviceType(vo.getModifyDeviceType());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyDeviceType(), info.getDeviceType())) {
					info.setDeviceType(vo.getModifyDeviceType());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyBrand(), info.getBrand())) {
					info.setBrand(vo.getModifyBrand());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyModel(), info.getModel())) {
					info.setModel(vo.getModifyModel());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifySystemVersion(), info.getSystemVersion())) {
					info.setSystemVersion(vo.getModifySystemVersion());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifySerialNumber(), info.getSerialNumber())) {
					info.setSerialNumber(vo.getModifySerialNumber());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyManufactureDate(), info.getManufactureDate())) {
					info.setManufactureDate(vo.getModifyManufactureDate());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyStatus(), info.getStatus())) {
					info.setStatus(vo.getModifyStatus());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifySyncFlag(), info.getSyncFlag())) {
					info.setSyncFlag(vo.getModifySyncFlag());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyCustodian(), info.getCustodian())) {
					info.setCustodian(vo.getModifyCustodian());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyDepartment(), info.getDepartment())) {
					info.setDepartment(vo.getModifyDepartment());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyUser(), info.getUser())) {
					info.setUser(vo.getModifyUser());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyNorthFlag(), info.getNorthFlag())) {
					info.setNorthFlag(vo.getModifyNorthFlag());
					updateFlag = true;
				}
				if(!StringUtils.equals(vo.getModifyRemark(), info.getRemark())) {
					info.setRemark(vo.getModifyRemark());
					updateFlag = true;
				}
				if(updateFlag) {
					info.setUpdateTime(currentTimestamp());
					info.setUpdateBy(currentUserName());
					
					infoList.add(info);
				}

			}
			
			if(!infoList.isEmpty() && infoList.size() > 0) {
				inventoryInfoDAO.saveOrUpdateInventoryInfo(infoList);
			}
			
		}catch (Exception e) {
			return action + "失敗";
		}	
		
		return action + "成功" ;
	}
	
	@Override
	public boolean deleteInventoryInfo(List<String> deviceIdList, String deleteRsn) {
		try {
			if(deviceIdList == null || deviceIdList.isEmpty()) {
				log.info("delete without deviceId!!");
				return false;
			}
			List<InventoryInfo> resultList = inventoryInfoDAO.findInventoryInfoByDeviceId(deviceIdList);
			
			List<InventoryInfo> deleteList = new ArrayList<>();
			
			for(InventoryInfo info :resultList) {
				info.setDeleteTime(currentTimestamp());
				info.setDeleteBy(currentUserName());
				info.setDeleteRsn(deleteRsn);
				info.setDeleteFlag(Constants.DATA_Y);
				
				deleteList.add(info);
			}
//			inventoryInfoDAO.deleteInventoryInfo(resultList);
			inventoryInfoDAO.saveOrUpdateInventoryInfo(deleteList);
			return true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public String updateInventoryDetail(JsonNode jsonData) {
		String action = "更新";

		try {
			String deviceId = jsonData.findValue("deviceId").asText();
			String deviceType = jsonData.findValue("deviceType").asText();
			
			Iterator<String> keys = jsonData.fieldNames();
			Map<String, String> updateMap = new HashMap<>();
			while (keys.hasNext()) {
				String key = keys.next();
				System.out.println("key鍵是:" + key + ", 值是:" + jsonData.findValue(key).asText());
				if(key.startsWith("input")) {
					updateMap.put(key.substring(5), jsonData.findValue(key).asText());
				}
			}
			
			List<Object> entities = new ArrayList<>();
			Object columnValue = null;
			boolean updateFlag = false;
			
			switch (deviceType.toLowerCase()) {
			case "server":
				InventoryInfoServerDetail serverData = (InventoryInfoServerDetail) inventoryInfoDAO.findInvDetailDataByDeviceIdType(deviceId, deviceType);
				for (Map.Entry<String, String> entry : updateMap.entrySet()) {
					columnValue = entry.getValue();
					if(entry.getKey().endsWith("Time")) {
						columnValue = Timestamp.valueOf((String)columnValue);
					}
					if(!columnValue.equals(org.apache.commons.beanutils.BeanUtils.getProperty(serverData, entry.getKey()))){
						org.apache.commons.beanutils.BeanUtils.setProperty(serverData, entry.getKey(), columnValue);
						updateFlag = true;
					}
				}
				if(updateFlag) {
					serverData.setUpdateTime(currentTimestamp());
					serverData.setUpdateBy(currentUserName());
					entities.add(serverData);
				}
				break;
				
			case "cell":
				InventoryInfoCellDetail cellData = (InventoryInfoCellDetail) inventoryInfoDAO.findInvDetailDataByDeviceIdType(deviceId, deviceType);
				for (Map.Entry<String, String> entry : updateMap.entrySet()) {
					columnValue = entry.getValue();
					if(entry.getKey().endsWith("Time")) {
						columnValue = Timestamp.valueOf((String)columnValue);
					}
					if(!columnValue.equals(org.apache.commons.beanutils.BeanUtils.getProperty(cellData, entry.getKey()))){
						updateFlag = true;
					}
				}
				if(updateFlag) {
					cellData.setUpdateTime(currentTimestamp());
					cellData.setUpdateBy(currentUserName());
					entities.add(cellData);
				}
				break;
				
			case "vm":
				InventoryInfoVmDetail vmData = (InventoryInfoVmDetail) inventoryInfoDAO.findInvDetailDataByDeviceIdType(deviceId, deviceType);
				for (Map.Entry<String, String> entry : updateMap.entrySet()) {
					columnValue = entry.getValue();
					if(entry.getKey().endsWith("Time")) {
						columnValue = Timestamp.valueOf((String)columnValue);
					}
					if(!columnValue.equals(org.apache.commons.beanutils.BeanUtils.getProperty(vmData, entry.getKey()))){
						org.apache.commons.beanutils.BeanUtils.setProperty(vmData, entry.getKey(), columnValue);
						updateFlag = true;
					}
				}
				if(updateFlag) {
					vmData.setUpdateTime(currentTimestamp());
					vmData.setUpdateBy(currentUserName());
					entities.add(vmData);
				}
				break;
				
			case "storage":
				InventoryInfoStorageDetail storageData = (InventoryInfoStorageDetail) inventoryInfoDAO.findInvDetailDataByDeviceIdType(deviceId, deviceType);
				for (Map.Entry<String, String> entry : updateMap.entrySet()) {
					columnValue = entry.getValue();
					if(entry.getKey().endsWith("Time")) {
						columnValue = Timestamp.valueOf((String)columnValue);
					}
					if(!columnValue.equals(org.apache.commons.beanutils.BeanUtils.getProperty(storageData, entry.getKey()))){
						org.apache.commons.beanutils.BeanUtils.setProperty(storageData, entry.getKey(), columnValue);
						updateFlag = true;
					}
				}
				if(updateFlag) {
					storageData.setUpdateTime(currentTimestamp());
					storageData.setUpdateBy(currentUserName());
					entities.add(storageData);
				}
				break;
				
			case "ups":
				InventoryInfoUpsDetail upsData = (InventoryInfoUpsDetail) inventoryInfoDAO.findInvDetailDataByDeviceIdType(deviceId, deviceType);
				for (Map.Entry<String, String> entry : updateMap.entrySet()) {
					columnValue = entry.getValue();
					if(entry.getKey().endsWith("Time")) {
						columnValue = Timestamp.valueOf((String)columnValue);
					}
					if(!columnValue.equals(org.apache.commons.beanutils.BeanUtils.getProperty(upsData, entry.getKey()))){
						org.apache.commons.beanutils.BeanUtils.setProperty(upsData, entry.getKey(), columnValue);
						updateFlag = true;
					}
				}
				if(updateFlag) {
					upsData.setUpdateTime(currentTimestamp());
					upsData.setUpdateBy(currentUserName());
					entities.add(upsData);
				}
				break;
				
			default:
				break;
			}
			
			if(entities != null && !entities.isEmpty()) {
				inventoryInfoDAO.saveOrUpdateInventory(entities);//.insertEntities(BaseDAO.TARGET_PRIMARY_DB, entities);
			}
			
		}catch (Exception e) {
			log.error(e.getMessage());
			return action + "失敗";
		}	
		
		return action + "成功" ;
	}
	
	@Override
	public Map<String, Object> findInvDetailData(String deviceId, String deviceType) throws ServiceLayerException {
		
		if(StringUtils.isBlank(deviceId) || StringUtils.isBlank(deviceType)) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		Object[] detail = inventoryInfoDAO.findInvDetailDataByDeviceId(deviceId, deviceType);
		
		if(detail == null) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		String[] UI_TABLE_COLUMNS = getExportDetailFieldNames(deviceType);
		
		Map<String, Object> retMap = new LinkedHashMap<>();
		for(int i = 0; i < UI_TABLE_COLUMNS.length; i++) {
			Object value = detail[i];
			String[] columns = UI_TABLE_COLUMNS[i].split(Env.COMM_SEPARATE_SYMBOL);
			if(columns[0].endsWith("Time")) {
				value = Constants.FORMAT2_YYYYMMDD_HH24MISS.format(value);
			}
			retMap.put(messageSource.getMessage("func.plugin.inventory.detail.".concat(columns[0]), Locale.TAIWAN, null).concat(Env.COMM_SEPARATE_SYMBOL).concat(UI_TABLE_COLUMNS[i]), value);
		}
				
		return retMap;
	}
	
	@Override
	public List<Map<String, Object>> findInvInfoAndDetailData(InventoryInfoVO iiVO) throws ServiceLayerException {
		
		try {
			List<Object[]> infoList = inventoryInfoDAO.findInvInfoAndDetailDataByDeviceIdType(iiVO);
			List<Map<String, Object>> retList = new ArrayList<>();
			
			if(infoList == null || infoList.isEmpty()) {
				return null;
			}
			
			Map<String, Object> retMap = null;		
			InventoryInfo infoDatas = null;
			Object infoDetailDatas = null;
			String[] groupNames = null;
			String[] UI_TABLE_COLUMNS = getExportDetailFieldNames(iiVO.getQueryDeviceType());
			
			for(int i = 0 ; i < infoList.size() ; i++) {
				retMap = new LinkedHashMap<>();
				infoDatas = (InventoryInfo) infoList.get(i)[0];
				
				for(int j = 0; j < infoFieldNames.length; j++) {
					String value = org.apache.commons.beanutils.BeanUtils.getProperty(infoDatas, infoFieldNames[j]);
					//groupName欄位
					if(j == 2) {
						groupNames = value.split(" > ");
						//groupName, "groupName1", "groupName2","groupName3", "groupName4"
						retMap.put(infoFieldNames[j], groupNames[0]);
						retMap.put("groupName1", groupNames.length > 1 ? groupNames[1] : "");
						retMap.put("groupName2", groupNames.length > 2 ? groupNames[2] : "");
						retMap.put("groupName3", groupNames.length > 3 ? groupNames[3] : "");
						retMap.put("groupName4", groupNames.length > 4 ? groupNames[4] : "");
						
					} else {
						retMap.put(infoFieldNames[j], value);
					}
				}
				
				infoDetailDatas = infoList.get(i)[1];
				for(int k = 0; k < UI_TABLE_COLUMNS.length; k++) {
					String[] columns = UI_TABLE_COLUMNS[k].split(Env.COMM_SEPARATE_SYMBOL);
					String value = org.apache.commons.beanutils.BeanUtils.getProperty(infoDetailDatas, columns[0]);					
					if(columns[0].endsWith("Time")) {
						value = Constants.FORMAT_YYYYMMDD_HH24MISS.format(Constants.FORMAT2_YYYYMMDD_HH24MISS.parse(value));
					}
					if((columns.length > 1 && !StringUtils.equalsIgnoreCase("hide", columns[1]) || columns.length == 1)) {
						retMap.put(columns[0], value);
					}
				}
				
				retList.add(retMap);
			}
			
			return retList;
		} catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException(e.getMessage());
        }
		
	}
	
	@Override
	public String[] getExportDetailFieldNames(String deviceType) {
		switch (deviceType.toLowerCase()) {
		case "server":
			return serverDetailColumns;
			
		case "cell":
			return cellDetailColumns;
			
		case "vm":
			return vmDetailColumns;
			
		case "storage":
			return storageDetailColumns;
			
		case "ups":
			return upsDetailColumns;
			
		default:
			return null;
		}
	}
	
	@Override
	public String[] getExportDetailColumnTitles(String deviceType) {
		String[] columns = getExportDetailFieldNames(deviceType);
		List<String> titleList = new ArrayList<>();
		
		for(int i = 0; i < columns.length; i++) {
			String[] data = columns[i].split(Env.COMM_SEPARATE_SYMBOL);
			if((data.length > 1 && !StringUtils.equalsIgnoreCase("hide", data[1]) || data.length == 1)) titleList.add(messageSource.getMessage("func.plugin.inventory.detail.".concat(data[0]), Locale.TAIWAN, null));
		}
		String[] array = new String[titleList.size()];
		
		return titleList.toArray(array);
	}
	
}
