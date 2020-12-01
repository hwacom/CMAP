package com.cmap.service;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.InventoryInfoVO;

public interface InventoryInfoService {

	List<InventoryInfoVO> findInventoryInfo(InventoryInfoVO sliVO) throws ServiceLayerException;

	String updateInventoryInfo(InventoryInfoVO vo);

	boolean deleteInventoryInfo(List<String> deviceIdList);

}
