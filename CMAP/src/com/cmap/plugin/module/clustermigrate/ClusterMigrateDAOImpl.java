package com.cmap.plugin.module.clustermigrate;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.dao.impl.BaseDaoHibernate;
import com.nimbusds.oauth2.sdk.util.StringUtils;

@Repository("clusterMigrateDAOImpl")
@Transactional
public class ClusterMigrateDAOImpl extends BaseDaoHibernate implements ClusterMigrateDAO {

    @Override
    public List<ModuleClusterMigrateSetting> getClusterMigrateSetting(String settingName) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mcss ")
          .append(" from ModuleClusterMigrateSetting mcss ")
          .append(" where 1=1 ")
          .append(" and mcss.settingName = :settingName ")
          .append(" order by mcss.orderNo ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("settingName", settingName);

        return (List<ModuleClusterMigrateSetting>)q.list();
    }

    @Override
    public List<ModuleClusterMigrateLog> findClusterMigrateLog(
            String logId, String dateStr, String migrateFromCluster, List<String> processFlag) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mcsl ")
          .append(" from ModuleClusterMigrateLog mcsl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(logId)) {
            sb.append(" and mcsl.logId = :logId ");
        }
        if (StringUtils.isNotBlank(dateStr)) {
            sb.append(" and mcsl.dateStr = :dateStr ");
        }
        if (StringUtils.isNotBlank(migrateFromCluster)) {
            sb.append(" and mcsl.migrateFromCluster = :migrateFromCluster ");
        }
        if (processFlag != null && !processFlag.isEmpty()) {
            sb.append(" and mcsl.processFlag in (:processFlag) ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(logId)) {
            q.setParameter("logId", logId);
        }
        if (StringUtils.isNotBlank(dateStr)) {
            q.setParameter("dateStr", dateStr);
        }
        if (StringUtils.isNotBlank(migrateFromCluster)) {
            q.setParameter("migrateFromCluster", migrateFromCluster);
        }
        if (processFlag != null && !processFlag.isEmpty()) {
            q.setParameterList("processFlag", processFlag);
        }

        return (List<ModuleClusterMigrateLog>)q.list();
    }

}
