package com.cmap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.InventoryInfoDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.InventoryInfo;
import com.cmap.service.InventoryInfoService;
import com.cmap.service.vo.InventoryInfoVO;

@Service("inventoryInfoService")
@Transactional
public class InventoryInfoServiceImpl extends CommonServiceImpl implements InventoryInfoService {
	@Log
	private static Logger log;

	@Autowired
	private InventoryInfoDAO inventoryInfoDAO;

	@Override
	public List<InventoryInfoVO> findInventoryInfo(InventoryInfoVO iiVO) throws ServiceLayerException {
		
		List<InventoryInfo> infoList = inventoryInfoDAO.findInventoryInfo(iiVO);
		
		if(infoList == null || infoList.isEmpty()) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		List<InventoryInfoVO> result = new ArrayList<>();
		InventoryInfoVO currVO = null;
		
		for(InventoryInfo info : infoList) {
			currVO = new InventoryInfoVO();
			BeanUtils.copyProperties(info, currVO);
			
			currVO.setCreateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(info.getCreateTime()));
			result.add(currVO);
		}
		
		return result;
	}

}
