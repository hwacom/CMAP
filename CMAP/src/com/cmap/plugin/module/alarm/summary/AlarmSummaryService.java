package com.cmap.plugin.module.alarm.summary;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.fasterxml.jackson.databind.JsonNode;

public interface AlarmSummaryService {

    /**
     * 取得符合條件資料筆數
     * @param tlVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleAlarmSummary(AlarmSummaryVO tlVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料(頁面使用)
     * @param tlVO
     * @return
     * @throws ServiceLayerException
     */
    public List<AlarmSummaryVO> findModuleAlarmSummary(AlarmSummaryVO tlVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param tlVO
     * @return
     * @throws ServiceLayerException
     */
    public List<ModuleAlarmSummary> findModuleAlarmSummary(AlarmSummaryVO tlVO) throws ServiceLayerException;

    public ModuleAlarmSummary findAlarmSummary(Long alarmId) throws ServiceLayerException;

    public void createTicket(JsonNode jsonData) throws ServiceLayerException;

    public List<ModuleAlarmSummaryLog> findModuleAlarmSummaryLog(Long alarmId);
    
    /**
     * 新增 OR 更新封鎖紀錄
     * @param tlVOs
     * @throws ServiceLayerException
     */
    public void saveOrUpdateAlarmSummary(List<AlarmSummaryVO> tlVOs, JsonNode jsonData) throws ServiceLayerException;

}
