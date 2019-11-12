package com.cmap.plugin.module.firewall;

import java.util.List;
import java.util.Map;
import com.cmap.exception.ServiceLayerException;

public interface FirewallService {

    /**
     * 取得該查詢類別下的查詢結果欄位
     * @param queryType
     * @param fieldType
     * @return
     */
    public List<String> getFieldNameList(String queryType, String fieldType);

    /**
     * 查找基礎設定
     * @param settingName
     * @return
     */
    public List<FirewallVO> findFirewallLogSetting(String settingName);

    /**
     * 取得「動作」選單內容
     * @param queryType
     * @return
     * @throws ServiceLayerException
     */
    public Map<String, String> getActionMenu(String queryType) throws ServiceLayerException;

    /**
     * 查詢符合條件資料筆數 by DB模式
     * @param fVO
     * @param fieldsMap
     * @return
     * @throws ServiceLayerException
     */
    public long countFirewallLogRecordFromDB(FirewallVO fVO, Map<String, List<String>> fieldsMap) throws ServiceLayerException;

    /**
     * 查詢符合條件資料 by DB模式
     * @param fVO
     * @param startRow
     * @param pageLength
     * @param fieldsMap
     * @return
     * @throws ServiceLayerException
     */
    public List<FirewallVO> findFirewallLogRecordFromDB(
            FirewallVO fVO, Integer startRow, Integer pageLength, Map<String, List<String>> fieldsMap) throws ServiceLayerException;

    /**
     * [查詢類別為ALL時]查詢符合條件資料筆數 by DB模式
     * @param fVO
     * @param fieldsMap
     * @return
     * @throws ServiceLayerException
     */
    public long countFirewallLogRecordFromDBbyAll(FirewallVO fVO, Map<String, List<String>> fieldsMap) throws ServiceLayerException;

    /**
     * [查詢類別為ALL時]查詢符合條件資料 by DB模式
     * @param fVO
     * @param startRow
     * @param pageLength
     * @param fieldsMap
     * @return
     * @throws ServiceLayerException
     */
    public List<FirewallVO> findFirewallLogRecordFromDBbyAll(
            FirewallVO fVO, Integer startRow, Integer pageLength, Map<String, List<String>> fieldsMap) throws ServiceLayerException;

    /**
     * 取得查詢類別+月份區間內涵蓋到的table的總筆數概略值
     * @param fVO
     * @return
     * @throws ServiceLayerException
     */
    public long getTableRoughlyTotalCount(FirewallVO fVO) throws ServiceLayerException;
}
