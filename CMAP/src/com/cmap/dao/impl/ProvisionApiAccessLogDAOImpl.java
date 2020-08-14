package com.cmap.dao.impl;

import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.ProvisionApiAccessLogDAO;
import com.cmap.model.ProvisionApiAccessLog;

@Repository("provisionApiAccessLogDAOImpl")
@Transactional
public class ProvisionApiAccessLogDAOImpl extends BaseDaoHibernate implements ProvisionApiAccessLogDAO {
	@Log
    private static Logger log;
		
	@Override
	public ProvisionApiAccessLog findProvisionApiAccessLogByHash(String checkHash) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ProvisionApiAccessLog ")
		  .append(" where 1=1 ")
		  .append(" and checkHash = :checkHash ")
		  .append(" and action <> 'DONE' ")
		  .append(" and accessTime >=  :dateTime ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());
		q.setParameter("checkHash", checkHash);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -5);
		q.setParameter("dateTime", cal.getTime());
		
		return (ProvisionApiAccessLog)q.uniqueResult();
	}
	
	@Override
	public void saveProvisionApiLog(ProvisionApiAccessLog entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}
}
