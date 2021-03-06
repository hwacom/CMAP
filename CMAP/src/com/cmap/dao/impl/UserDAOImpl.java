package com.cmap.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.BehaviorType;
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
	public UserRightSetting findUserRightSetting(String account, String loginMode) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from UserRightSetting urs ")
		  .append(" where 1=1 ")
		  .append(" and urs.account = :account ")
		  .append(" and urs.deleteFlag  = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if(StringUtils.isNotBlank(loginMode)) {
			sb.append(" and urs.loginMode = :loginMode ");
		}
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("account", account);
	    
	    if(StringUtils.isNotBlank(loginMode)) {
	    	q.setParameter("loginMode", loginMode);
		}
	    List<UserRightSetting> result = (List<UserRightSetting>)q.list();
	    return result != null && result.size() > 0 ?result.get(0):null;
	}
	
	@Override
	public UserRightSetting findUserRightSetting(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from UserRightSetting urs ")
		  .append(" where 1=1 ")
		  .append(" and urs.id = :id ")
		  .append(" and urs.deleteFlag  = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("id", id);
	    
	    List<UserRightSetting> result = (List<UserRightSetting>)q.list();
	    return result != null && result.size() > 0 ?result.get(0):null;
	}
	
	@Override
	public long countUserRightSettingsByVO(UserRightServiceVO vo) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(urs.id) ")
		.append(" from UserRightSetting urs ")
		.append(" where 1=1 ")
		.append(" and deleteFlag = '"+Constants.DATA_MARK_NOT_DELETE+"' ");

		if (StringUtils.isNotBlank(vo.getAccount())) {
			sb.append(" and urs.account = :account ");
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			sb.append(" and urs.userName = :userName ");
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			sb.append(" and urs.userGroup = :userGroup ");
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
			q.setParameter("account", vo.getAccount());
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			q.setParameter("userGroup", vo.getUserGroup());
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			q.setParameter("userName", vo.getUserName());
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
			sb.append(" and urs.account = :account ");
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			sb.append(" and urs.userName = :userName ");
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			sb.append(" and urs.userGroup = :userGroup ");
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
			q.setParameter("account", vo.getAccount());
		}
		if (StringUtils.isNotBlank(vo.getUserGroup())) {
			q.setParameter("userGroup", vo.getUserGroup());
		}
		if (StringUtils.isNotBlank(vo.getUserName())) {
			q.setParameter("userName", vo.getUserName());
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
	
	@Override
	public void saveOrUpdateEntities(List<Object> entities) {
		for (Object entity : entities) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
	}
	
	@Override
	public void saveOrUpdateEntity(Object entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}
	

	@Override
	public long countUserLoginFailTimes(String userAccount, String checkTime) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(1) ")
          .append(" from user_behavior_log ubl")
          .append(" where 1=1 ")
          .append(" and ubl.BEHAVIOR = '" + BehaviorType.LOGIN_FAIL_PW.toString() +"'")
          .append(" and ubl.USER_ACCOUNT = :userAccount ")
          .append(" and ubl.BEHAVIOR_TIME >= :checkTime ");
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createNativeQuery(sb.toString());
        q.setParameter("userAccount", userAccount);
        q.setParameter("checkTime", checkTime);

        return DataAccessUtils.longResult(q.list());
	}
}
