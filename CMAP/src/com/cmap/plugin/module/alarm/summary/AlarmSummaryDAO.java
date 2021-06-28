package com.cmap.plugin.module.alarm.summary;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface AlarmSummaryDAO extends BaseDAO {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     */
    public long countModuleAlarmSummary(AlarmSummaryVO ibrVO);

    /**
     * 取得符合條件資料(頁面使用)
     * @param ibrVO
     * @param startRow
     * @param pageLength
     * @return Object[0]:ModuleAlarmSummary
     *         Object[1]:DeviceList
     */
    public List<Object[]> findModuleAlarmSummary(AlarmSummaryVO ibrVO, Integer startRow, Integer pageLength);

    /**
     * 取得符合條件資料
     * @param ibrVO
     * @return
     */
    public List<ModuleAlarmSummary> findModuleAlarmSummary(AlarmSummaryVO ibrVO);

    ModuleAlarmSummary findModuleAlarmSummaryByVO(AlarmSummaryVO tlVO);

	ModuleAlarmSummary findModuleAlarmSummaryByPK(Long alarmId);

	void saveOrUpdateAlarmSummary(List<Object> entityList);

	void deleteAlarmSummary(List<Object> entities);

	List<ModuleAlarmSummaryLog> findModuleAlarmSummaryLogByAlarmId(Long alarmId);

}
