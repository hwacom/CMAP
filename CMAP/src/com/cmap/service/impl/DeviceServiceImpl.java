package com.cmap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.model.DeviceList;
import com.cmap.model.DeviceLoginInfo;
import com.cmap.service.DeviceService;
import com.cmap.service.vo.DeviceLoginInfoServiceVO;
import com.sun.org.apache.xml.internal.security.utils.Base64;

@Service("deviceService")
@Transactional
public class DeviceServiceImpl extends CommonServiceImpl implements DeviceService {
	@Log
	private static Logger log;

	@Autowired
	private DeviceDAO deviceDAO;
	
	@Override
	public void updateDeviceList(List<DeviceList> deviceList) {
		// 更新 or 寫入 DEVICE_LIST 資料
		deviceDAO.saveOrUpdateDeviceList(deviceList);
	}

	@Override
	public DeviceLoginInfo findDeviceLoginInfo(String deviceId) {
		return deviceDAO.findDeviceLoginInfo(deviceId);
	}

	@Override
	public String updateDeviceLoginInfo(List<String> deviceIdList, DeviceLoginInfoServiceVO vo) {
		String resultMag = "更新成功";
		// 更新 or 寫入 DEVICE_LIST 資料
		try {
			DeviceLoginInfoServiceVO dliVO = new DeviceLoginInfoServiceVO();
			dliVO.setQueryDeviceList(deviceIdList);
			List<DeviceLoginInfo> infos = deviceDAO.findDeviceLoginInfoList(dliVO);
			
			List<DeviceLoginInfo> updateInfos = new ArrayList<>();
			boolean updateFlag = false;
			String configBackupMode = "";
			String[] backupMode ;
			for(DeviceLoginInfo info : infos) {
				if(!StringUtils.equals(info.getConnectionMode(), vo.getModifyConnectionMode())) {
					updateFlag = true;
					info.setConnectionMode(vo.getModifyConnectionMode());
				}
				if(!StringUtils.equals(Base64.encode(info.getLoginAccount().getBytes()), vo.getModifyLoginAccount())) {
					updateFlag = true;
					info.setLoginAccount(vo.getModifyLoginAccount());
				}
				if(!StringUtils.equals(Base64.encode(info.getLoginPassword().getBytes()), vo.getModifyLoginPassword())) {
					updateFlag = true;
					info.setLoginPassword(vo.getModifyLoginPassword());
				}
				if(!StringUtils.equals(Base64.encode(info.getEnablePassword().getBytes()), vo.getModifyEnablePassword())) {
					updateFlag = true;
					info.setEnablePassword(vo.getModifyEnablePassword());
				}
				if(!StringUtils.equals(info.getEnableBackup(), vo.getModifyEnableBackup())) {
					updateFlag = true;
					info.setEnableBackup(vo.getModifyEnableBackup());
				}
				//STEP.TFTP+DEVICE.SSH+FILE_SERVER.TFTP 設定預設組態備份的傳輸模式(組態檔取得模式+連接設備模式+檔案上傳模式)
				backupMode = info.getConfigBackupMode().split("\\+");
				configBackupMode = "STEP." + vo.getModifyStep() +"+"+backupMode[1]+"+"+backupMode[2];
				
				if(!StringUtils.equals(info.getConfigBackupMode(), configBackupMode)) {
					updateFlag = true;
					info.setConfigBackupMode(configBackupMode);
				}
				
				if(updateFlag) {
					info.setUpdateTime(currentTimestamp());
					info.setUpdateBy(currentUserName());
					updateInfos.add(info);
				}
			}
		
			if(updateInfos.size() > 0) deviceDAO.saveOrUpdateDeviceLoginInfo(updateInfos);
			
		}catch (Exception e) {
			resultMag = "更新失敗";
		}	
		
		return resultMag ;
	}
	
	@Override
	public boolean deleteDeviceLoginInfo(List<String> deviceIdList) {
		try {
			if(deviceIdList == null || deviceIdList.isEmpty()) {
				log.info("delete without deviceId!!");
				return false;
			}
			
			DeviceLoginInfoServiceVO dliVO = new DeviceLoginInfoServiceVO();
			dliVO.setQueryDeviceList(deviceIdList);
			deviceDAO.deleteDeviceLoginInfo(deviceDAO.findDeviceLoginInfoList(dliVO));
			return true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public long countDeviceLoginInfoList(DeviceLoginInfoServiceVO vo) {
		return deviceDAO.countDeviceLoginInfoList(vo);
	}
	
	@Override
	public List<DeviceLoginInfo> findDeviceLoginInfoList(DeviceLoginInfoServiceVO vo) {
		return deviceDAO.findDeviceLoginInfoList(vo);
	}
	
	@Override
	public List<DeviceLoginInfoServiceVO> transModel2VO(List<DeviceLoginInfo> entities) {
		List<DeviceLoginInfoServiceVO> resultList = new ArrayList<>();
		DeviceLoginInfoServiceVO dliVO ;
		String fileServer = "", step = "";
		
		for(DeviceLoginInfo entity : entities) {
			dliVO = new DeviceLoginInfoServiceVO();
			BeanUtils.copyProperties(entity, dliVO);
			
			if(StringUtils.isNotBlank(entity.getConfigBackupMode())) {
				fileServer = entity.getConfigBackupMode().split("\\+")[0].split("\\.")[1];
				step = entity.getConfigBackupMode().split("\\+")[2].split("\\.")[1];
			}
			dliVO.setFileServer(fileServer);
			dliVO.setStep(step);
			dliVO.setUpdateTimeStr(entity.getUpdateTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(entity.getUpdateTime()) : null);
			
			resultList.add(dliVO);
		}        
        return resultList;
    }
}
