package com.cmap.plugin.module.port.blocked.record;

import java.util.List;
import com.cmap.dao.BaseDAO;

public interface PortBlockedRecordDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param irVO
     * @return
     */
    public long countModuleBlockedPortList(PortBlockedRecordVO pbrVO);

    /**
     * 取得符合條件資料
     * @param irVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedPortList
     *         Object[1]:DeviceList
     *         Object[2]:DevicePortInfo
     */
    public List<Object[]> findModuleBlockedPortList(PortBlockedRecordVO pbrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件的最新一筆資料
     * @param ibrVO
     * @return
     */
    public ModuleBlockedPortList findLastestModuleBlockedPortList(PortBlockedRecordVO pbrVO);
}
