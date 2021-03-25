package com.cmap.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.MibDAO;
import com.cmap.model.MibOidMapping;
import com.cmap.model.MibValueMapping;

@Repository("mibDAO")
@Transactional
public class MibDAOImpl extends BaseDaoHibernate implements MibDAO {
	@Log
    private static Logger log;
    
	@Override
    public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from MibOidMapping mom ")
          .append(" where 1=1 ")
          .append(" and mom.oidName in (:oidNames) ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameterList("oidNames", oidNames);
        return (List<MibOidMapping>)q.list();
    }

	@Override
	public List<MibOidMapping> findMibOidMappingOfTableEntryByNameLike(String tableOidName) {
		StringBuffer sb = new StringBuffer();
        sb.append(" from MibOidMapping mom ")
          .append(" where 1=1 ")
          .append(" and mom.oidName like :tableOidName ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("tableOidName", tableOidName + ".%");
        return (List<MibOidMapping>)q.list();
	}

	@Override
	public List<MibValueMapping> findMibValueMappingByOidTable(String oidTable) {
		StringBuffer sb = new StringBuffer();
        sb.append(" from MibValueMapping mvm ")
          .append(" where 1=1 ")
          .append(" and mvm.oidTable = :oidTable ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("oidTable", oidTable);
        return (List<MibValueMapping>)q.list();
	}
}
