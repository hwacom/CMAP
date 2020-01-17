package com.cmap.plugin.module.mac.blocked.record;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;

public interface MacBlockedRecordService {

    /**
     * 取得符合條件資料筆數
     * @param mbrVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleBlockedMacList(MacBlockedRecordVO mbrVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param mbrVO
     * @return
     * @throws ServiceLayerException
     */
    public List<MacBlockedRecordVO> findModuleBlockedMacList(MacBlockedRecordVO mbrVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 新增 OR 更新封鎖紀錄
     * @param mbrVOs
     * @throws ServiceLayerException
     */
    public void saveOrUpdateRecord(List<MacBlockedRecordVO> mbrVOs) throws ServiceLayerException;

    /**
     * 檢核比對DB封鎖記錄清單
     * @param groupId
     * @param deviceId
     * @param macAddress
     * @param dbRecordList
     * @return
     */
    public MacBlockedRecordVO checkMacBlockedList(String groupId, String deviceId, String macAddress, List<MacBlockedRecordVO> dbRecordList);
    
    /**
     * 比對同步結果與DB資料
     * @param dbRecordList
     * @param compareMap
     * @return
     */
    public List<MacBlockedRecordVO> compareMacBlockedList(List<MacBlockedRecordVO> dbRecordList, Map<String, MacBlockedRecordVO> compareMap);

}
