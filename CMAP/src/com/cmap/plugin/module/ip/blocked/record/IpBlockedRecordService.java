package com.cmap.plugin.module.ip.blocked.record;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;

public interface IpBlockedRecordService {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleBlockedIpList(IpBlockedRecordVO ibrVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param ibrVO
     * @return
     * @throws ServiceLayerException
     */
    public List<IpBlockedRecordVO> findModuleBlockedIpList(IpBlockedRecordVO ibrVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 新增 OR 更新封鎖紀錄
     * @param ibrVOs
     * @throws ServiceLayerException
     */
    public void saveOrUpdateRecord(List<IpBlockedRecordVO> ibrVOs) throws ServiceLayerException;

    /**
	 * 檢核比對DB封鎖記錄清單
	 * 
	 * @param groupId
	 * @param deviceId
	 * @param ipAddress
	 * @param dbRecordList
	 * @return
	 */
	public IpBlockedRecordVO checkIpblockedList(String groupId, String deviceId, String ipAddress, List<IpBlockedRecordVO> dbRecordList);

	/**
	 * 比對同步結果與DB資料
	 * @param dbRecordList
	 * @param resultMap
	 * @return
	 */
	public List<IpBlockedRecordVO> compareIpblockedList(List<IpBlockedRecordVO> dbRecordList, Map<String, IpBlockedRecordVO> resultMap);
}
