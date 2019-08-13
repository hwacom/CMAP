package com.cmap.plugin.module.ip.mapping;

import java.util.List;
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
import com.cmap.model.MibOidMapping;

@Repository("ipMappingDAO")
@Transactional
public class IpMappingDAOImpl extends BaseDaoHibernate implements IpMappingDAO {
    @Log
    private static Logger log;

    @Autowired
    @Qualifier("secondSessionFactory")
    private SessionFactory secondSessionFactory;

    @Override
    public List<ModuleMacTableExcludePort> findModuleMacTableExcludePort(String groupId, String deviceId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from ModuleMacTableExcludePort mmtep ")
          .append(" where 1=1 ")
          .append(" and mmtep.groupId = :groupId ")
          .append(" and mmtep.deviceId = :deviceId ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("groupId", groupId);
        q.setParameter("deviceId", deviceId);
        return (List<ModuleMacTableExcludePort>)q.list();
    }

    @Override
    public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from MibOidMapping mom ")
          .append(" where 1=1 ")
          .append(" and mom.oidName in (:oidName) ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameterList("oidNames", oidNames);
        return (List<MibOidMapping>)q.list();
    }
}
