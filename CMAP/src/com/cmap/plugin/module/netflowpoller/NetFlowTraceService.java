package com.cmap.plugin.module.netflowpoller;

import java.util.List;

import com.cmap.exception.ServiceLayerException;

public interface NetFlowTraceService {

    /**
     * 查找符合條件資料筆數
     * @param nfVO
     * @param searchLikeField
     * @return
     * @throws ServiceLayerException
     */
	public long countNetFlowRecordFromDB(NetFlowTraceVO nfVO, List<String> searchLikeField) throws ServiceLayerException;

	/**
	 * 查找符合條件資料 From DB
	 * @param nfVO
	 * @param startRow
	 * @param pageLength
	 * @param searchLikeField
	 * @return
	 * @throws ServiceLayerException
	 */
	public List<NetFlowTraceVO> findNetFlowRecordFromDB(NetFlowTraceVO nfVO, Integer startRow, Integer pageLength, List<String> searchLikeField) throws ServiceLayerException;

	/**
	 * 查找符合條件資料 From File
	 * @param nfVO
	 * @param startRow
	 * @param pageLength
	 * @return
	 * @throws ServiceLayerException
	 */
	public NetFlowTraceVO findNetFlowRecordFromFile(NetFlowTraceVO nfVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

	/**
	 * 查找符合條件資料總流量
	 * @param nfVO
	 * @param searchLikeField
	 * @return
	 * @throws ServiceLayerException
	 */
	public String getTotalTraffic(NetFlowTraceVO nfVO, List<String> searchLikeField) throws ServiceLayerException;

	/**
	 * 查找資料 By groupId + dataId
	 * @param groupId
	 * @param dataId
	 * @return
	 * @throws ServiceLayerException
	 */
	public NetFlowTraceVO findNetFlowRecordByGroupIdAndDataId(String groupId, String dataId, String fromDateTime) throws ServiceLayerException;
}
