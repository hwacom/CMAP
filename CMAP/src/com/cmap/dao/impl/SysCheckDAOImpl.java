package com.cmap.dao.impl;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.SysCheckDAO;

@Repository
@Transactional
public class SysCheckDAOImpl extends BaseDaoHibernate implements SysCheckDAO {
	@Log
    private static Logger log;
	
	@Override
	public Integer excuteUpdateSQL(final String sql) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		return session.createNativeQuery(sql).executeUpdate();
	}

}
