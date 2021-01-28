package com.cmap.service;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.InventoryInfoCellDetailVO;
import com.cmap.service.vo.InventoryInfoVO;

public interface InventoryInfoService {

	List<InventoryInfoVO> findInventoryInfo(InventoryInfoVO sliVO) throws ServiceLayerException;

	String updateOrInsertInventoryInfo(List<InventoryInfoVO> voList);

	boolean deleteInventoryInfo(List<String> deviceIdList);

	String insertInventoryInfo(List<InventoryInfoVO> voList);

	InventoryInfoCellDetailVO findInvCellDetailData(String deviceId) throws ServiceLayerException;

}
