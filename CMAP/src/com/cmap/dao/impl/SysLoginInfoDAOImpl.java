package com.cmap.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.model.SysLoginInfo;
import com.cmap.service.vo.SysLoginInfoVO;

@Repository("sysLoginInfoDAO")
@Transactional
public class SysLoginInfoDAOImpl extends BaseDaoHibernate implements SysLoginInfoDAO {
	@Log
    private static Logger log;
	
	@Override
	public List<SysLoginInfo> findSysLoginInfoBySessionId(List<String> ids){
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(" from SysLoginInfo sli ")
		  .append(" where 1=1 ");

		if (ids != null && !ids.isEmpty()) {
			sb.append(" and sli.sessionId in (:sessionId) ");
		}

	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (ids != null && !ids.isEmpty()) {
	    	q.setParameterList("sessionId", ids);
	    }

	    return (List<SysLoginInfo>)q.list();
	}

	@Override
	public SysLoginInfo findLastSysLoginInfoBySessionId(String sessionId) {
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
	public List<SysLoginInfo> findSysLoginInfo(SysLoginInfoVO vo){
		StringBuffer sb = new StringBuffer();
		sb.append(" from SysLoginInfo sli ")
		  .append(" where 1=1 ");

		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(vo.getQueryDateBegin()) && StringUtils.isNotBlank(vo.getQueryDateEnd()) && StringUtils.isNotBlank(vo.getQueryTimeBegin()) && StringUtils.isNotBlank(vo.getQueryTimeEnd())) {
        	sb.append(" and (sli.loginTime >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and sli.loginTime < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
		
		if (StringUtils.isNotBlank(vo.getQueryUserAccount())) {
			sb.append(" and sli.account = :userAccount ");
		}
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(vo.getQueryDateBegin()) && StringUtils.isNotBlank(vo.getQueryDateEnd()) && StringUtils.isNotBlank(vo.getQueryTimeBegin()) && StringUtils.isNotBlank(vo.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", vo.getQueryDateBegin().concat(" ").concat(vo.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", vo.getQueryDateEnd().concat(" ").concat(vo.getQueryTimeEnd()));
        }
		
		if (StringUtils.isNotBlank(vo.getQueryUserAccount())) {
			q.setParameter("userAccount", vo.getQueryUserAccount());
		}

		if (vo.getStartNum() != null && vo.getPageLength() != null) {
            q.setFirstResult(vo.getStartNum());
            q.setMaxResults(vo.getPageLength());
        }
		
	    return (List<SysLoginInfo>)q.list();
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
		SysLoginInfo info = findLastSysLoginInfoBySessionId(sessionId);
		if(info != null) {
			Timestamp nowTimestamp = new Timestamp((new Date()).getTime());
			info.setLogoutTime(nowTimestamp);
			saveSysLoginInfo(info);
		}
	}
}
