package com.cmap.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
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
import com.cmap.model.InventoryInfoCellDetail;
import com.cmap.service.InventoryInfoService;
import com.cmap.service.vo.InventoryInfoCellDetailVO;
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
			
			if(currVO.getGroupName().indexOf(" > ") > 0) {
				String[] names = currVO.getGroupName().split(" > ");
				currVO.setGroupName(names[0]);
				currVO.setGroupName1(names[1]);
				if(names.length > 2) currVO.setGroupName2(names[2]);
				if(names.length > 3) currVO.setGroupName3(names[3]);
				if(names.length > 4) currVO.setGroupName4(names[4]);
			}
			currVO.setCreateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(info.getCreateTime()));
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
				info.setModifyFlag(Constants.DATA_Y);
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
				
				if(updateFlag) {
					info.setUpdateTime(currentTimestamp());
					info.setUpdateBy(currentUserName());
					info.setModifyFlag(Constants.DATA_Y);
					
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
	public boolean deleteInventoryInfo(List<String> deviceIdList) {
		try {
			if(deviceIdList == null || deviceIdList.isEmpty()) {
				log.info("delete without deviceId!!");
				return false;
			}
			List<InventoryInfo> resultList = inventoryInfoDAO.findInventoryInfoByDeviceId(deviceIdList);
			
			inventoryInfoDAO.deleteInventoryInfo(resultList);
			return true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public InventoryInfoCellDetailVO findInvCellDetailData(String deviceId) throws ServiceLayerException {
		
		if(StringUtils.isBlank(deviceId)) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		InventoryInfoCellDetail detail = inventoryInfoDAO.findInvCellDetailDataByDeviceId(deviceId);
		
		if(detail == null) {
			throw new ServiceLayerException("查無紀錄!!");
		}
		
		InventoryInfoCellDetailVO currVO = new InventoryInfoCellDetailVO();
		BeanUtils.copyProperties(detail, currVO);
		currVO.setCreateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(detail.getCreateTime()));
		
		return currVO;
	}
}
