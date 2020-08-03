package com.cmap.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.model.SysLoginInfo;

@Repository("sysLoginInfoDAO")
@Transactional
public class SysLoginInfoDAOImpl extends BaseDaoHibernate implements SysLoginInfoDAO {
	@Log
    private static Logger log;
	
	@Override
	public List<SysLoginInfo> findSysLoginInfoBySessionId(List<String> ids){
		StringBuffer sb = new StringBuffer();
		sb.append(" from SysLoginInfo sli ")
		  .append(" where 1=1 ");

		if (ids != null) {
			sb.append(" and sli.sessionId in (:sessionId) ");
		}

	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (ids != null) {
	    	q.setParameterList("sessionId", ids);
	    }

	    return (List<SysLoginInfo>)q.list();
	}

	@Override
	public SysLoginInfo findSysLoginInfoBySessionId(String sessionId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from SysLoginInfo sli ")
		  .append(" where 1=1 ")
		  .append(" and sli.sessionId = :sessionId ")
		  .append(" ORDER BY sli.loginTime desc ");
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("sessionId", sessionId);
	    
	    List<SysLoginInfo> resultList = (List<SysLoginInfo>)q.list();
	    
	    return resultList == null || resultList.isEmpty() ? null:resultList.get(0);
	}
	
	@Override
	public void deleteSysLoginInfo(SysLoginInfo model) {
		getHibernateTemplate().delete(model);
	}
	
	@Override
	public void saveSysLoginInfo(SysLoginInfo model) {
		getHibernateTemplate().saveOrUpdate(model);
	}
	
	@Override
	public void updateLogoutTime(String sessionId) {
		SysLoginInfo info = findSysLoginInfoBySessionId(sessionId);
		Timestamp nowTimestamp = new Timestamp((new Date()).getTime());
		info.setLogoutTime(nowTimestamp);
		saveSysLoginInfo(info);
	}
}
