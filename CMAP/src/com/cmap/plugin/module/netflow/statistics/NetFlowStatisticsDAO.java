package com.cmap.plugin.module.netflow.statistics;

import java.util.List;
import com.cmap.dao.BaseDAO;

public interface NetFlowStatisticsDAO extends BaseDAO {

    /**
     * 查找IP流量資料
     * @param groupId
     * @param statDate
     * @param ipAddress
     * @return
     */
    public List<ModuleIpStatistics> findModuleIpStatistics(String groupId, String statDate, String ipAddress);

    /**
     * 新增 or 修改IP流量資料
     * @param entities
     */
    public void saveOrUpdateModuleIpStatistics(List<ModuleIpStatistics> entities);

    /**
     * 查找IP流量排行榜
     * @param groupId
     * @param statBeginDate
     * @param statEndDate
     * @return
     */
    public List<ModuleIpStatistics> findModuleIpStatisticsRanking(String groupId, String statBeginDate, String statEndDate);
}
