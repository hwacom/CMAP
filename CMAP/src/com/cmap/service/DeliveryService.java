package com.cmap.service;

import java.util.List;

import com.cmap.comm.enums.ConnectionMode;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.plugin.module.blocked.record.BlockedRecordVO;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;

public interface DeliveryService {

	public long countDeviceList(DeliveryServiceVO dsVO) throws ServiceLayerException;

	public List<DeliveryServiceVO> findDeviceList(DeliveryServiceVO dsVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

	public long countScriptList(DeliveryServiceVO dsVO) throws ServiceLayerException;

	public List<DeliveryServiceVO> findScriptList(DeliveryServiceVO dsVO, List<String> groupIds, Integer startRow, Integer pageLength) throws ServiceLayerException;

	public DeliveryServiceVO getScriptInfoByIdOrCode(String scriptInfoId, String scriptCode) throws ServiceLayerException;

	public DeliveryServiceVO getVariableSetting(List<String> groups, List<String> devices, List<String> variables) throws ServiceLayerException;

	public String logAccessRecord(DeliveryServiceVO dsVO) throws ServiceLayerException;

	/**
	 * 使用者/系統供裝派送流程
	 * e.g. 使用者: PORT開關
	 *      系統: VM備援切換
	 * @param connectionMode
	 * @param dpVO
	 * @param sysTrigger
	 * @param triggerBy
	 * @param triggerRemark
	 * @param chkParameters
	 * @return
	 * @throws ServiceLayerException
	 */
	public DeliveryServiceVO doDelivery(ConnectionMode connectionMode, DeliveryParameterVO dpVO, boolean sysTrigger, String triggerBy, String triggerRemark, boolean chkParameters) throws ServiceLayerException;

	/**
	 * 依使用者輸入的查詢條件查詢符合的供裝紀錄筆數
	 * @param dsVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public long countProvisionLog(DeliveryServiceVO dsVO) throws ServiceLayerException;

	/**
	 * 依使用者輸入的查詢條件查詢符合的供裝紀錄資料
	 * @param dsVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<DeliveryServiceVO> findProvisionLog(DeliveryServiceVO dsVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

	/**
	 * 查找使用者點擊的供裝紀錄的provision log
	 * @param logStepId
	 * @return
	 * @throws ServiceLayerException
	 */
	public DeliveryServiceVO getProvisionLogById(String logStepId) throws ServiceLayerException;

	/**
	 * 查找該群組下指定的 Device_Layer 設備
	 * @param groupId
	 * @param deviceLayer
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<DeviceList> findGroupDeviceOfSpecifyLayer(String groupId, List<String> deviceLayer) throws ServiceLayerException;

	/**
	 * 同步設備上封鎖清單(IP)[Cisco]
	 * @param isAdmin
	 * @param prtgLoginAccount
	 * @param brVO
	 * @param dbRecordList
	 * @return
	 * @throws ServiceLayerException
	 */
	public boolean doSyncDeviceIpBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO, List<BlockedRecordVO> dbRecordList) throws ServiceLayerException;

	/**
	 * 同步設備上封鎖清單(Port)[Cisco]
	 * @param isAdmin
	 * @param prtgLoginAccount
	 * @param brVO
	 * @param dbRecordList
	 * @return
	 * @throws ServiceLayerException
	 */
	public boolean doSyncDevicePortBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO, List<BlockedRecordVO> dbRecordList) throws ServiceLayerException;

	/**
	 * 同步設備上封鎖清單(Mac)[Cisco]
	 * @param isAdmin
	 * @param prtgLoginAccount
	 * @param brVO
	 * @param dbRecordList
	 * @return
	 * @throws ServiceLayerException
	 */
	public boolean doSyncDeviceMacBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO, List<BlockedRecordVO> dbRecordList) throws ServiceLayerException;

	/**
	 * IP MAC 綁定/解除供裝前檢核、異動
	 * @param pVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public DeliveryParameterVO checkB4DoBindingDelivery(DeliveryParameterVO pVO) throws ServiceLayerException;

	/**
	 * IP MAC 封鎖/解除供裝前檢核、異動
	 * @param pVO
	 * @return
	 * @throws ServiceLayerException
	 */
	public DeliveryParameterVO checkB4DoIpMacOpenBlockDelivery(DeliveryParameterVO pVO) throws ServiceLayerException;
}
