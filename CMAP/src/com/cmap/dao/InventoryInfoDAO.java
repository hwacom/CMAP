package com.cmap.dao;

import java.util.List;

import com.cmap.model.InventoryInfo;
import com.cmap.model.InventoryInfoCellDetail;
import com.cmap.service.vo.InventoryInfoVO;

public interface InventoryInfoDAO extends BaseDAO {

	List<InventoryInfo> findInventoryInfoByDeviceId(List<String> ids);
	
	InventoryInfo findLastInventoryInfoByDeviceId(String deviceId);

	List<InventoryInfo> findInventoryInfo(InventoryInfoVO vo);

	String findMaxInventoryInfoDeviceId();
	
	void saveOrUpdateInventoryInfo(List<InventoryInfo> entityList);

	void deleteInventoryInfo(List<InventoryInfo> entities);

	InventoryInfoCellDetail findInvCellDetailDataByDeviceId(String deviceId);

}
