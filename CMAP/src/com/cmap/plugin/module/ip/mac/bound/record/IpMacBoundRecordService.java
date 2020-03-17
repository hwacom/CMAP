package com.cmap.plugin.module.ip.mac.bound.record;

import java.util.List;

import com.cmap.exception.ServiceLayerException;

public interface IpMacBoundRecordService {

    /**
     * 取得符合條件資料筆數
     * @param ibrVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleIpMacBoundList(IpMacBoundRecordVO ibrVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param ibrVO
     * @return
     * @throws ServiceLayerException
     */
    public List<IpMacBoundRecordVO> findModuleIpMacBoundList(IpMacBoundRecordVO ibrVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 新增 OR 更新封鎖紀錄
     * @param ibrVOs
     * @throws ServiceLayerException
     */
    public void saveOrUpdateRecord(List<IpMacBoundRecordVO> ibrVOs) throws ServiceLayerException;

    /**
	 * 檢核比對DB封鎖記錄清單
	 * 
	 * @param deviceId
	 * @param ipAddress
	 * @param macAddress
	 * @param dbRecordList
	 * @return
	 */
//	public IpMacBoundRecordVO checkIpMacBoundList(String deviceId, String ipAddress, String macAddress, List<IpMacBoundRecordVO> dbRecordList);

	/**
	 * 比對同步結果與DB資料
	 * @param dbRecordList
	 * @param resultMap
	 * @return
	 */
//	public List<IpMacBoundRecordVO> compareIpMacBoundList(List<IpMacBoundRecordVO> dbRecordList, Map<String, IpMacBoundRecordVO> resultMap);

}
