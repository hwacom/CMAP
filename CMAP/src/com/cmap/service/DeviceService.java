package com.cmap.service;

import java.util.List;

import com.cmap.model.DeviceList;
import com.cmap.model.DeviceLoginInfo;
import com.cmap.service.vo.DeviceLoginInfoServiceVO;

public interface DeviceService {

	/**
	 * 更新 Device_List 資料
	 * @param deviceList
	 */
	public void updateDeviceList(List<DeviceList> deviceList);

	/**
	 * 遞迴查找DEVICE_LOGIN_INFO資料
	 * @param deviceId
	 * @return
	 */
	public DeviceLoginInfo findDeviceLoginInfo(String deviceId);

	public List<DeviceLoginInfo> findDeviceLoginInfoList(DeviceLoginInfoServiceVO vo);

	public boolean deleteDeviceLoginInfo(List<String> deviceIdList);

	public List<DeviceLoginInfoServiceVO> transModel2VO(List<DeviceLoginInfo> entities);

	public String updateDeviceLoginInfo(List<String> deviceIdList, DeviceLoginInfoServiceVO vo);

	public long countDeviceLoginInfoList(DeviceLoginInfoServiceVO vo);

}
