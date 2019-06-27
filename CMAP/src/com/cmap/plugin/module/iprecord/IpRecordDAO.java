package com.cmap.plugin.module.iprecord;

import java.util.List;

public interface IpRecordDAO {

    /**
     * 取得符合條件資料筆數
     * @param irVO
     * @return
     */
    public long countModuleBlockedIpList(IpRecordVO irVO);

    /**
     * 取得符合條件資料
     * @param irVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedIpList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleBlockedIpList(IpRecordVO irVO, Integer startRow, Integer pageLength);
}
