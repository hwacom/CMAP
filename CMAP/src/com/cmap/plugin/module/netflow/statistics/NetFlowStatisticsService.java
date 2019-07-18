package com.cmap.plugin.module.netflow.statistics;

import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.netflow.NetFlowVO;

public interface NetFlowStatisticsService {

    public NetFlowVO executeNetFlowIpStat() throws ServiceLayerException;
}
