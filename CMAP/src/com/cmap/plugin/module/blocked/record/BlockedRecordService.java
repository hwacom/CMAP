package com.cmap.plugin.module.blocked.record;

import java.util.List;
import java.util.Map;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.ConfigInfoVO;

public interface BlockedRecordService {

    /**
     * 取得符合條件資料筆數
     * @param brVO
     * @return
     * @throws ServiceLayerException
     */
    public long countModuleBlockedList(BlockedRecordVO brVO) throws ServiceLayerException;

    /**
     * 查詢符合條件資料(頁面使用)
     * @param brVO
     * @return
     * @throws ServiceLayerException
     */
    public List<BlockedRecordVO> findModuleBlockedList(BlockedRecordVO brVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查詢符合條件資料
     * @param brVO
     * @return
     * @throws ServiceLayerException
     */
    public List<ModuleBlockedList> findModuleBlockedList(BlockedRecordVO brVO) throws ServiceLayerException;
    
    /**
     * 新增 OR 更新封鎖紀錄
     * @param brVOs
     * @throws ServiceLayerException
     */
    public void saveOrUpdateRecord(List<BlockedRecordVO> brVOs) throws ServiceLayerException;

    /**
     * 寫入 IP MAC 綁定 紀錄資料
     * @param ciVO
     * @param scriptCode
     * @param varMapList
     * @param actionStatusFlag
     * @param remark
     * @throws ServiceLayerException
     */
    public void writeModuleIpMacBoundListRecord(ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList,
			String actionStatusFlag, String remark) throws ServiceLayerException;

    /**
     * 寫入 MAC 鎖定 紀錄資料
     * @param ciVO
     * @param scriptCode
     * @param varMapList
     * @param actionStatusFlag
     * @param remark
     * @throws ServiceLayerException
     */
    public void writeModuleBlockMacListRecord(ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList,
			String actionStatusFlag, String remark) throws ServiceLayerException;

	/**
	 * 寫入 PORT 鎖定 紀錄資料
	 * @param ciVO
	 * @param scriptCode
	 * @param varMapList
	 * @param actionStatusFlag
	 * @param remark
	 * @throws ServiceLayerException
	 */
    public void writeModuleBlockPortListRecord(ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList,
			String actionStatusFlag, String remark) throws ServiceLayerException;

    /**
     * 寫入 IP 鎖定 紀錄資料
     * @param ciVO
     * @param scriptCode
     * @param varMapList
     * @param actionStatusFlag
     * @param remark
     * @throws ServiceLayerException
     */
    public void writeModuleBlockIpListRecord(ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList,
			String actionStatusFlag, String remark) throws ServiceLayerException;

    /**
	 * 檢核比對DB封鎖記錄清單(IP)
	 * 
	 * @param groupId
	 * @param deviceId
	 * @param ipAddress
	 * @param dbRecordList
	 * @return
	 */
    public BlockedRecordVO checkIpblockedList(String groupId, String deviceId, String ipAddress,
			List<BlockedRecordVO> dbRecordList);

    /**
	 * 比對同步結果與DB資料(IP)
	 * @param dbRecordList
	 * @param resultMap
	 * @return
	 */
	public List<BlockedRecordVO> compareIpblockedList(List<BlockedRecordVO> dbRecordList,
			Map<String, BlockedRecordVO> compareMap);

	/**
     * 檢核比對DB封鎖記錄清單(Port)
     * @param groupId
     * @param deviceId
     * @param portId
     * @param dbRecordList
     * @return
     */
    public BlockedRecordVO checkPortBlockedList(String groupId, String deviceId, String portId, List<BlockedRecordVO> dbRecordList);

    /**
     * 比對同步結果與DB資料(Port)
     * @param dbRecordList
     * @param compareMap
     * @return
     */
    public List<BlockedRecordVO> comparePortBlockedList(List<BlockedRecordVO> dbRecordList, Map<String, BlockedRecordVO> compareMap);


    /**
     * 檢核比對DB封鎖記錄清單(MAC)
     * @param groupId
     * @param deviceId
     * @param macAddress
     * @param dbRecordList
     * @return
     */
    public BlockedRecordVO checkMacBlockedList(String groupId, String deviceId, String macAddress, List<BlockedRecordVO> dbRecordList);
    
    /**
     * 比對同步結果與DB資料(MAC)
     * @param dbRecordList
     * @param compareMap
     * @return
     */
    public List<BlockedRecordVO> compareMacBlockedList(List<BlockedRecordVO> dbRecordList, Map<String, BlockedRecordVO> compareMap);
}