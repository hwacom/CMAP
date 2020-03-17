package com.cmap.plugin.module.iptracepoller;

import java.util.List;

import com.cmap.dao.BaseDAO;

public interface IpTracePollerDAO extends BaseDAO {

    /**
     * 查找IP_TRACE連線資料 by UK
     * @param clientMac
     * @param startTime
     * @return
     */
    public ModuleIpTrace findModuleIpTraceByUK(String clientIp, String startTime);

    /**
     * 查找IP_TRACE連線資料
     * @param searchVO
     * @param startRow 分頁起始列
     * @param pageLength 分頁長度
     * @return VO格式
     */
    public List<IpTracePollerVO> findModuleIpTrace(IpTracePollerVO searchVO, Integer startRow, Integer pageLength) ;
    
    /**
     * 查找符合條件資料筆數
     * @param searchVO
     * @return 資料筆數
     */
    public long countIpTraceDataFromDB(IpTracePollerVO searchVO);
}
