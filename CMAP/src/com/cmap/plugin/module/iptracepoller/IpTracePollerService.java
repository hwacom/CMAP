package com.cmap.plugin.module.iptracepoller;

import java.util.List;

import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.netflow.NetFlowVO;

public interface IpTracePollerService {

    /**
     * 查找IP_TRACE連線資料
     * @param searchVO
     * @param startRow 分頁起始列
     * @param pageLength 分頁長度
     * @return IP_TRACE資料List(VO格式)
     */
    public List<IpTracePollerVO> findModuleIpTrace(IpTracePollerVO searchVO, Integer startRow, Integer pageLength) throws ServiceLayerException;

    /**
     * 查找符合條件資料筆數
     * @param searchVO
     * @return
     * @throws ServiceLayerException
     */
	public long countIpTraceDataFromDB(IpTracePollerVO searchVO) throws ServiceLayerException;

}
