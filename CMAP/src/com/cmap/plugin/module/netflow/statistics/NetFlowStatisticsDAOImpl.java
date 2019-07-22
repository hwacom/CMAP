package com.cmap.plugin.module.netflow.statistics;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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
    public List<ModuleIpTrafficStatistics> findModuleIpStatistics(String groupId, String statDate, String ipAddress) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mits ")
          .append(" from ModuleIpTrafficStatistics mits ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(groupId)) {
            sb.append(" and mits.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(statDate)) {
            sb.append(" and mits.statDate = :statDate ");
        }
        if (StringUtils.isNotBlank(ipAddress)) {
            sb.append(" and mits.ipAddress = :ipAddress ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(groupId)) {
            q.setParameter("groupId", groupId);
        }
        if (StringUtils.isNotBlank(statDate)) {
            q.setParameter("statDate", statDate);
        }
        if (StringUtils.isNotBlank(ipAddress)) {
            q.setParameter("ipAddress", ipAddress);
        }

        return (List<ModuleIpTrafficStatistics>)q.list();
    }

    @Override
    public ModuleIpTrafficStatistics findModuleIpStatisticsByUK(String groupId, String statDate, String ipAddress) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mits ")
          .append(" from ModuleIpTrafficStatistics mits ")
          .append(" where 1=1 ")
          .append(" and mits.groupId = :groupId ")
          .append(" and mits.statDate = :statDate ")
          .append(" and mits.ipAddress = :ipAddress ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("groupId", groupId);
        q.setParameter("statDate", statDate);
        q.setParameter("ipAddress", ipAddress);

        return (ModuleIpTrafficStatistics)q.uniqueResult();
    }

    @Override
    public void saveOrUpdateModuleIpStatistics(List<ModuleIpTrafficStatistics> entities) {
        // TODO 自動產生的方法 Stub

    }

    @Override
    public List<ModuleIpTrafficStatistics> findModuleIpStatisticsRanking(String groupId,
            String statBeginDate, String statEndDate) {
        // TODO 自動產生的方法 Stub
        return null;
    }
}
