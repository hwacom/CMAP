package com.cmap.plugin.module.port.blocked.record;

import java.util.List;

public interface PortBlockedRecordDAO {

    /**
     * 取得符合條件資料筆數
     * @param irVO
     * @return
     */
    public long countModuleBlockedPortList(PortBlockedRecordVO irVO);

    /**
     * 取得符合條件資料
     * @param irVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedPortList
     *         Object[1]:DeviceList
     *         Object[2]:DevicePortInfo
     */
    public List<Object[]> findModuleBlockedPortList(PortBlockedRecordVO irVO, Integer startRow, Integer pageLength);
}
