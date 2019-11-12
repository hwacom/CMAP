package com.cmap.plugin.module.mac.blocked.record;

import java.util.List;
import com.cmap.dao.BaseDAO;

public interface MacBlockedRecordDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param mbrVO
     * @return
     */
    public long countModuleBlockedMacList(MacBlockedRecordVO mbrVO);

    /**
     * 取得符合條件資料
     * @param mbrVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedMacList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleBlockedMacList(MacBlockedRecordVO mbrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件的最新一筆資料
     * @param mbrVO
     * @param startRow
     * @param pageLength
     * @return
     */
    public ModuleBlockedMacList findLastestModuleBlockedMacList(MacBlockedRecordVO mbrVO);
}
