package com.cmap.plugin.module.ip.blocked.record;

import java.util.List;
import com.cmap.dao.BaseDAO;

public interface IpBlockedRecordDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     */
    public long countModuleBlockedIpList(IpBlockedRecordVO ibrVO);

    /**
     * 取得符合條件資料
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedIpList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleBlockedIpList(IpBlockedRecordVO ibrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件的最新一筆資料
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return
     */
    public ModuleBlockedIpList findLastestModuleBlockedIpList(IpBlockedRecordVO ibrVO);
}
