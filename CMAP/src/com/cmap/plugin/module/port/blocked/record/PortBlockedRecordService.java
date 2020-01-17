package com.cmap.plugin.module.port.blocked.record;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;

public interface PortBlockedRecordService {

    /**
     * 取得符合條件資料筆數
     * @param irVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleBlockedPortList(PortBlockedRecordVO pbrVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param irVO
     * @return
     * @throws ServiceLayerException
     */
    public List<PortBlockedRecordVO> findModuleBlockedPortList(PortBlockedRecordVO pbrVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 新增 OR 更新封鎖紀錄
     * @param pbrVOs
     * @throws ServiceLayerException
     */
    public void saveOrUpdateRecord(List<PortBlockedRecordVO> pbrVOs) throws ServiceLayerException;

    /**
     * 檢核比對DB封鎖記錄清單
     * @param groupId
     * @param deviceId
     * @param portId
     * @param dbRecordList
     * @return
     */
    public PortBlockedRecordVO checkPortBlockedList(String groupId, String deviceId, String portId, List<PortBlockedRecordVO> dbRecordList);

    /**
     * 比對同步結果與DB資料
     * @param dbRecordList
     * @param compareMap
     * @return
     */
    public List<PortBlockedRecordVO> comparePortBlockedList(List<PortBlockedRecordVO> dbRecordList, Map<String, PortBlockedRecordVO> compareMap);
}
