package com.cmap.plugin.module.inventory.info;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.InventoryInfoVO;
import com.fasterxml.jackson.databind.JsonNode;

public interface InventoryInfoService {

	List<InventoryInfoVO> findInventoryInfo(InventoryInfoVO sliVO) throws Exception;

	String updateOrInsertInventoryInfo(List<InventoryInfoVO> voList);

	boolean deleteInventoryInfo(List<String> deviceIdList, String deleteRsn);

	String updateInventoryDetail(JsonNode jsonData);
	
	String insertInventoryInfo(List<InventoryInfoVO> voList);

	public Map<String, Object> findInvDetailData(String deviceId, String deviceType) throws ServiceLayerException;

	public List<Map<String, Object>> findInvInfoAndDetailData(InventoryInfoVO iiVO) throws ServiceLayerException;

	String[] getExportDetailFieldNames(String deviceType);

	String[] getExportDetailColumnTitles(String deviceType);

}
