package com.cmap.plugin.module.netflow.statistics;

import java.util.Date;
import java.util.Map;
import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.netflow.NetFlowVO;

public interface NetFlowStatisticsService {

    public NetFlowVO executeNetFlowIpStatistics() throws ServiceLayerException;

    public void calculateIpTrafficStatistics(
            String groupId, Date statDate, Map<String, Map<String, Integer>> ipTrafficMap) throws ServiceLayerException;
}
