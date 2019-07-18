package com.cmap.plugin.module.netflow.statistics;

import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("netFlowStatisticsDAO")
@Transactional
public class NetFlowStatisticsDAOImpl extends BaseDaoHibernate implements NetFlowStatisticsDAO {
    @Log
    private static Logger log;

    @Autowired
    @Qualifier("secondSessionFactory")
    private SessionFactory secondSessionFactory;

    @Override
    public List<ModuleIpStatistics> findModuleIpStatistics(String groupId, String statDate,
            String ipAddress) {
        // TODO 自動產生的方法 Stub
        return null;
    }

    @Override
    public void saveOrUpdateModuleIpStatistics(List<ModuleIpStatistics> entities) {
        // TODO 自動產生的方法 Stub

    }

    @Override
    public List<ModuleIpStatistics> findModuleIpStatisticsRanking(String groupId,
            String statBeginDate, String statEndDate) {
        // TODO 自動產生的方法 Stub
        return null;
    }
}
