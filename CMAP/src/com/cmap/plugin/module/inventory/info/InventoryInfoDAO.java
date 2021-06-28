package com.cmap.plugin.module.inventory.info;

import java.util.List;

import com.cmap.dao.BaseDAO;
import com.cmap.service.vo.InventoryInfoVO;

public interface InventoryInfoDAO extends BaseDAO {

	List<InventoryInfo> findInventoryInfoByDeviceId(List<String> ids);
	
	InventoryInfo findLastInventoryInfoByDeviceId(String deviceId);

	List<InventoryInfo> findInventoryInfo(InventoryInfoVO vo);

	String findMaxInventoryInfoDeviceId();
	
	void saveOrUpdateInventoryInfo(List<InventoryInfo> entityList);

	void deleteInventoryInfo(List<InventoryInfo> entities);

	void saveOrUpdateInventory(List<Object> entityList);
	
	public Object[] findInvDetailDataByDeviceId(String deviceId, String deviceType);

	public Object findInvDetailDataByDeviceIdType(String deviceId, String deviceType);

	public List<Object[]> findInvInfoAndDetailDataByDeviceIdType(InventoryInfoVO vo);

}
