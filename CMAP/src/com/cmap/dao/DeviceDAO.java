package com.cmap.dao;

import java.sql.Timestamp;
import java.util.List;

import com.cmap.dao.vo.DeviceDAOVO;
import com.cmap.model.DeviceDetailInfo;
import com.cmap.model.DeviceDetailMapping;
import com.cmap.model.DeviceList;
import com.cmap.model.DeviceLoginInfo;
import com.cmap.service.vo.DeviceLoginInfoServiceVO;

public interface DeviceDAO extends BaseDAO {

	public DeviceList findDeviceListByDeviceListId(String deviceListId);

	public DeviceList findDeviceListByDeviceIp(String deviceIp);

	public DeviceList findDeviceListByGroupAndDeviceId(String groupId, String deviceId);

	public List<DeviceList> findDeviceListByDAOVO(DeviceDAOVO dlDAOVO);

	public long countDeviceListAndLastestVersionByDAOVO(DeviceDAOVO dlDAOVO);

	public List<Object[]> findDeviceListAndLastestVersionByDAOVO(DeviceDAOVO dlDAOVO, Integer startRow, Integer pageLength);

	public void saveOrUpdateDeviceList(List<DeviceList> entityList);

	public List<DeviceList> findDistinctDeviceListByGroupIdsOrDeviceIds(List<String> groupIds, List<String> deviceIds);

	public List<Object[]> getGroupIdAndNameByGroupIds(List<String> groupIds);

	public List<Object[]> getDeviceIdAndNameByDeviceIds(List<String> deviceIds);

	public List<DeviceDetailInfo> findDeviceDetailInfo(String groupId, String deviceId, String infoName);

	public List<DeviceDetailMapping> findDeviceDetailMapping(String targetInfoName);

	public boolean deleteDeviceDetailInfoByInfoName(String groupId, String deviceId, String infoName, Timestamp deleteTime,
			String deleteBy) throws Exception;

	public DeviceLoginInfo findDeviceLoginInfo(String deviceId);

	public void saveOrUpdateDeviceLoginInfo(List<DeviceLoginInfo> entityList);

	public long countDeviceLoginInfoList(DeviceLoginInfoServiceVO vo);
	
	public List<DeviceLoginInfo> findDeviceLoginInfoList(DeviceLoginInfoServiceVO vo);

	public void deleteDeviceLoginInfo(List<DeviceLoginInfo> entities);

	List<Object[]> findDeviceListForReport();
}
