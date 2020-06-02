package com.cmap.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.UserDAO;
import com.cmap.model.UserRightSetting;
import com.cmap.service.vo.UserRightServiceVO;

@Repository("userDAO")
@Transactional
public class UserDAOImpl extends BaseDaoHibernate implements UserDAO {
	@Log
    private static Logger log;
	
	@Override
	public List<UserRightSetting> findUserRightSetting(String belongGroup, String[] roles, String account) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from UserRightSetting urs ")
		  .append(" where 1=1 ")
		  .append(" and urs.belongGroup = :belongGroup ");

		if (roles != null) {
			sb.append(" and urs.role in (:roles) ");
		}

		sb.append(" and urs.account = :account ")
		  .append(" order by urs.isAdmin desc, urs.denyAccess asc ");

	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("belongGroup", belongGroup);
	    if (roles != null) {
	    	q.setParameterList("roles", roles);
	    }
	    q.setParameter("account", account);

	    return (List<UserRightSetting>)q.list();
	}

	@Override
	public UserRightSetting findUserRightSetting(String account) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from UserRightSetting urs ")
		  .append(" where 1=1 ")
		  .append(" and urs.account = :account ")
		  .append(" and urs.deleteFlag  = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("account", account);

	    return (UserRightSetting)q.uniqueResult();
	}
	
	@Override
	public long countUserRightSettingsByVO(UserRightServiceVO vo) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(urs.id) ")
		.append(" from UserRightSetting urs ")
		.append(" where 1=1 ")
		.append(" and deleteFlag = '"+Constants.DATA_MARK_NOT_DELETE+"' ");

		if (StringUtils.isNotBlank(vo.getAccount())) {
			sb.append(" and urs.account like :account ");
		}
		if (StringUtils.isNotBlank(vo.getPassword())) {
			sb.append(" and urs.password like :password ");
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			sb.append(" and urs.userName like :userName ");
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			sb.append(" and urs.userGroup like :userGroup ");
		}

		if (StringUtils.isNotBlank(vo.getSearchValue())) {
			sb.append(" and ( ")
			.append("       urs.account like :account ")
			.append("       or ")
			.append("       urs.password like :password ")
			.append("       or ")
			.append("       urs.userName like :userName ")
			.append("       or ")
			.append("       urs.userGroup like :userGroup ")
			.append("     ) ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (StringUtils.isNotBlank(vo.getAccount())) {
			q.setParameter("account", "%".concat(vo.getAccount()).concat("%"));
		}
		if (StringUtils.isNotBlank(vo.getPassword())) {
			q.setParameter("password", "%".concat(vo.getPassword()).concat("%"));
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			q.setParameter("userGroup", "%".concat(vo.getUserGroup()).concat("%"));
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			q.setParameter("userName", "%".concat(vo.getUserName()).concat("%"));
		}

		if (StringUtils.isNotBlank(vo.getSearchValue())) {
			q.setParameter("account", "%".concat(vo.getSearchValue()).concat("%"));
			q.setParameter("password", "%".concat(vo.getSearchValue()).concat("%"));
			q.setParameter("userGroup", "%".concat(vo.getSearchValue()).concat("%"));
			q.setParameter("userName", "%".concat(vo.getSearchValue()).concat("%"));
		}
		
		return DataAccessUtils.longResult(q.list());
	}
	
	@Override
	public List<UserRightSetting> findUserRightSettingByVO(UserRightServiceVO vo, Integer startRow, Integer pageLength) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from UserRightSetting urs ")
		.append(" where 1=1 ")
		.append(" and deleteFlag = '"+Constants.DATA_MARK_NOT_DELETE+"' ");

		if (StringUtils.isNotBlank(vo.getAccount())) {
			sb.append(" and urs.account like :account ");
		}
		if (StringUtils.isNotBlank(vo.getPassword())) {
			sb.append(" and urs.password like :password ");
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			sb.append(" and urs.userName like :userName ");
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			sb.append(" and urs.userGroup like :userGroup ");
		}

		if (StringUtils.isNotBlank(vo.getSearchValue())) {
			sb.append(" and ( ")
			.append("       urs.account like :account ")
			.append("       or ")
			.append("       urs.password like :password ")
			.append("       or ")
			.append("       urs.userName like :userName ")
			.append("       or ")
			.append("       urs.userGroup like :userGroup ")
			.append("     ) ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (StringUtils.isNotBlank(vo.getAccount())) {
			q.setParameter("account", "%".concat(vo.getAccount()).concat("%"));
		}
		if (StringUtils.isNotBlank(vo.getPassword())) {
			q.setParameter("password", "%".concat(vo.getPassword()).concat("%"));
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			q.setParameter("userGroup", "%".concat(vo.getUserGroup()).concat("%"));
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			q.setParameter("userName", "%".concat(vo.getUserName()).concat("%"));
		}

		if (StringUtils.isNotBlank(vo.getSearchValue())) {
			q.setParameter("account", "%".concat(vo.getSearchValue()).concat("%"));
			q.setParameter("password", "%".concat(vo.getSearchValue()).concat("%"));
			q.setParameter("userGroup", "%".concat(vo.getSearchValue()).concat("%"));
			q.setParameter("userName", "%".concat(vo.getSearchValue()).concat("%"));
		}

		return (List<UserRightSetting>)q.list();
	}
	

	@Override
	public Integer deleteUserRightSettingById(List<String> ids, String actionBy) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from UserRightSetting urs ")
		.append(" where 1=1 ")
		.append(" and urs.id in (:ids) ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());
		q.setParameter("ids", ids);

		List<UserRightSetting> modelList = (List<UserRightSetting>)q.list();

		Integer successCount = 0;
		if (modelList != null && !modelList.isEmpty()) {
			for (UserRightSetting model : modelList) {
				try {
					model.setDeleteFlag(MARK_AS_DELETE);
					model.setUpdateBy(actionBy);
					model.setUpdateTime(new Timestamp(new Date().getTime()));
					model.setDeleteBy(actionBy);
					model.setDeleteTime(new Timestamp(new Date().getTime()));

					getHibernateTemplate().saveOrUpdate(model);
					successCount++;

				} catch (Exception e) {
					log.error(e.toString(), e);

					continue;
				}
			}
		}

		return successCount;
	}
	
	@Override
	public void saveUserRightSetting(UserRightSetting model) {
		getHibernateTemplate().saveOrUpdate(model);
	}
}
