package com.cmap.plugin.module.ip.mac.bound.record;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface IpMacBoundRecordDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     */
    public long countModuleIpMacBoundList(IpMacBoundRecordVO ibrVO);

    /**
     * 取得符合條件資料
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleIpMacBoundList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleIpMacBoundList(IpMacBoundRecordVO ibrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件的最新一筆資料
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return
     */
    public ModuleIpMacBoundList findLastestModuleIpMacBoundList(IpMacBoundRecordVO ibrVO);
}
