package com.cmap.plugin.module.blocked.record;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface BlockedRecordDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     */
    public long countModuleBlockedList(BlockedRecordVO ibrVO);

    /**
     * 取得符合條件資料(頁面使用)
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleBlockedList
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleBlockedList(BlockedRecordVO ibrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件資料
     * @param ibrVO
     * @return
     */
    public List<ModuleBlockedList> findModuleBlockedList(BlockedRecordVO ibrVO);
    
    /**
     * 取得符合條件的最新一筆資料
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return
     */
    public ModuleBlockedList findLastestModuleBlockedList(BlockedRecordVO ibrVO);	
}
