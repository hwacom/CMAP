package com.cmap.dao;

import java.util.List;

import com.cmap.model.DeviceList;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.PrtgUserRightSetting;

public interface PrtgDAO extends BaseDAO {

	/**
	 * 查找設定 By PRTG account
	 * @param prtgAccount
	 * @return
	 */
	public PrtgAccountMapping findPrtgAccountMappingByAccount(String prtgAccount);

	/**
	 * 查找設定 PRTG 帳號清單
	 * @return
	 */
	public List<PrtgAccountMapping> findPrtgAccountMappingList();
	
	/**
	 * 查找 PRTG 使用者 Group & Device 權限表
	 * @param prtgAccount
	 * @param settingType
	 * @return
	 */
	public List<PrtgUserRightSetting> findPrtgUserRightSetting(String prtgAccount, String settingType);

	/**
	 * 查找 PRTG 使用者有權限的 Group & Device 清單(含Device細部資料)
	 * @param prtgAccount
	 * @return
	 */
	public List<DeviceList> findPrtgUserRightGroupAndDeviceFullInfo(String prtgAccount);

	/**
	 * 查找 PRTG 使用者有權限的 Group 清單
	 * @param prtgAccount
	 * @return
	 */
	public List<Object[]> findPrtgUserRightGroupList(String prtgAccount);

	/**
	 * 查找 PRTG 使用者有權限的 Device 清單
	 * @param prtgAccount
	 * @return
	 */
	public List<Object[]> findPrtgUserRightDeviceList(String prtgAccount, String groupId);

	/**
	 * 
	 * @param settingValue
	 * @param settingType
	 * @return
	 */
	List<PrtgUserRightSetting> findPrtgUserRightSettingBySettingValueAndType(String settingValue, String settingType);

}
