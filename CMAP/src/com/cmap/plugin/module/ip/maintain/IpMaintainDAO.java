package com.cmap.plugin.module.ip.maintain;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface IpMaintainDAO extends BaseDAO {

    /**
     * 查詢符合條件資料筆數
     * @param imsVO
     * @return
     */
    public long countModuleIpDataSetting(IpMaintainServiceVO imsVO);

    /**
     * 查詢符合條件資料
     * @param imsVO
     * @param startRow
     * @param pageLength
     * @return
     */
    public List<Object[]> findModuleIpDataSetting(
            IpMaintainServiceVO imsVO, Integer startRow, Integer pageLength);

    /**
     * 查找資料 By ID
     * @param settingId
     * @return
     */
    public ModuleIpDataSetting findModuleIpDataSettingById(String settingId);

    /**
     * 查找資料 By Unique Key
     * @param groupId
     * @param ipAddr
     * @return
     */
    public ModuleIpDataSetting findModuleIpDataSettingByUk(String groupId, String ipAddr);

    /**
     * 查找資料 By Unique Key From Secondary DB
     * @param groupId
     * @param ipAddr
     * @return
     */
    public ModuleIpDataSetting findModuleIpDataSettingByUkFromSecondaryDB(String groupId, String ipAddr);
}
