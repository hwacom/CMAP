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
    public List<ModuleIpTrafficStatistics> findModuleIpStatistics(String groupId, String statDate, String ipAddress);

    /**
     * 查找IP流量資料 by UK
     * @param groupId
     * @param statDate
     * @param ipAddress
     * @return
     */
    public ModuleIpTrafficStatistics findModuleIpStatisticsByUK(String groupId, String statDate, String ipAddress);

    /**
     * 新增 or 修改IP流量資料
     * @param entities
     */
    public void saveOrUpdateModuleIpStatistics(List<ModuleIpTrafficStatistics> entities);

    /**
     * 查找IP流量排行榜
     * @param groupId
     * @param statBeginDate
     * @param statEndDate
     * @return
     */
    public List<ModuleIpTrafficStatistics> findModuleIpStatisticsRanking(String groupId, String statBeginDate, String statEndDate);
}
