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
import com.cmap.dao.ScriptInfoDAO;
import com.cmap.dao.vo.ScriptInfoDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ScriptInfo;
import com.cmap.model.ScriptStepAction;

@Repository
@Transactional
public class ScriptInfoDAOImpl extends BaseDaoHibernate implements ScriptInfoDAO {
	@Log
    private static Logger log;
	
	@Override
	public long countScriptInfo(ScriptInfoDAOVO daovo) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(si.scriptInfoId) ")
		  .append(" from ScriptInfo si ")
		  .append(" where 1=1 ")
		  .append(" and si.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (daovo.getQueryScriptTypeCode() != null && !daovo.getQueryScriptTypeCode().isEmpty()) {
			sb.append(" and si.scriptType.scriptTypeCode in (:scriptTypeCode) ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryScriptInfoId())) {
			sb.append(" and si.scriptInfoId = :scriptInfoId ");
		}
		if (StringUtils.isNotBlank(daovo.getQuerySystemDefault())) {
			sb.append(" and si.systemDefault = :systemDefault ");
		}
		if (!daovo.isAdmin()) {
			sb.append(" and si.adminOnly = '").append(Constants.DATA_N).append("' ");
		}
		
		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       si.scriptName like :searchValue ")
			  .append("       or ")
			  .append("       si.scriptType.scriptTypeName like :searchValue ")
			  .append("       or ")
			  .append("       si.deviceModel like :searchValue ")
			  .append("       or ")
			  .append("       si.actionScript like :searchValue ")
			  .append("       or ")
			  .append("       si.actionScriptRemark like :searchValue ")
			  .append("       or ")
			  .append("       si.checkScript like :searchValue ")
			  .append("       or ")
			  .append("       si.checkScriptRemark like :searchValue ")
			  .append("     ) ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (daovo.getQueryScriptTypeCode() != null && !daovo.getQueryScriptTypeCode().isEmpty()) {
			q.setParameter("scriptTypeCode", daovo.getQueryScriptTypeCode());
		}
		if (StringUtils.isNotBlank(daovo.getQueryScriptInfoId())) {
			q.setParameter("scriptInfoId", daovo.getQueryScriptInfoId());
		}
		if (StringUtils.isNotBlank(daovo.getQuerySystemDefault())) {
			q.setParameter("systemDefault", daovo.getQuerySystemDefault());
		}
		
		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(daovo.getSearchValue()).concat("%"));
	    }

		return DataAccessUtils.longResult(q.list());
	}

	@Override
	public List<ScriptInfo> findScriptInfo(ScriptInfoDAOVO daovo, Integer startRow, Integer pageLength) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ScriptInfo si ")
		  .append(" where 1=1 ")
		  .append(" and si.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (daovo.getQueryScriptTypeCode() != null && !daovo.getQueryScriptTypeCode().isEmpty()) {
			sb.append(" and si.scriptType.scriptTypeCode in (:scriptTypeCode) ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryScriptInfoId())) {
			sb.append(" and si.scriptInfoId = :scriptInfoId ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryScriptCode())) {
			sb.append(" and si.scriptCode = :scriptCode ");
		}
		if (StringUtils.isNotBlank(daovo.getQuerySystemDefault())) {
			sb.append(" and si.systemDefault = :systemDefault ");
		}
		if (!daovo.isAdmin()) {
			sb.append(" and si.adminOnly = '").append(Constants.DATA_N).append("' ");
		}

		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       si.scriptName like :searchValue ")
			  .append("       or ")
			  .append("       si.scriptType.scriptTypeName like :searchValue ")
			  .append("       or ")
			  .append("       si.deviceModel like :searchValue ")
			  .append("       or ")
			  .append("       si.actionScript like :searchValue ")
			  .append("       or ")
			  .append("       si.actionScriptRemark like :searchValue ")
			  .append("       or ")
			  .append("       si.checkScript like :searchValue ")
			  .append("       or ")
			  .append("       si.checkScriptRemark like :searchValue ")
			  .append("     ) ");
		}
		if (StringUtils.isNotBlank(daovo.getOrderColumn())) {
			sb.append(" order by ").append(daovo.getOrderColumn()).append(" ").append(daovo.getOrderDirection());

		} else {
			sb.append(" order by si.updateTime desc, si.scriptType.scriptTypeName asc ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (daovo.getQueryScriptTypeCode() != null && !daovo.getQueryScriptTypeCode().isEmpty()) {
			q.setParameter("scriptTypeCode", daovo.getQueryScriptTypeCode());
		}
		if (StringUtils.isNotBlank(daovo.getQueryScriptInfoId())) {
			q.setParameter("scriptInfoId", daovo.getQueryScriptInfoId());
		}
		if (StringUtils.isNotBlank(daovo.getQueryScriptCode())) {
			q.setParameter("scriptCode", daovo.getQueryScriptCode());
		}
		if (StringUtils.isNotBlank(daovo.getQuerySystemDefault())) {
			q.setParameter("systemDefault", daovo.getQuerySystemDefault());
		}
		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(daovo.getSearchValue()).concat("%"));
	    }
	    if (startRow != null && pageLength != null) {
	    	q.setFirstResult(startRow);
		    q.setMaxResults(pageLength);
	    }

		return (List<ScriptInfo>)q.list();
	}

	@Override
	public ScriptInfo findScriptInfoByIdOrCode(String scriptInfoId, String scriptCode) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ScriptInfo si ")
		  .append(" where 1=1 ")
		  .append(" and si.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (StringUtils.isNotBlank(scriptInfoId)) {
			sb.append(" and si.scriptInfoId = :scriptInfoId ");
		}
		if (StringUtils.isNotBlank(scriptCode)) {
			sb.append(" and si.scriptCode = :scriptCode ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (StringUtils.isNotBlank(scriptInfoId)) {
			q.setParameter("scriptInfoId", scriptInfoId);
		}
		if (StringUtils.isNotBlank(scriptCode)) {
			q.setParameter("scriptCode", scriptCode);
		}

		List<ScriptInfo> retList = (List<ScriptInfo>)q.list();
		return (retList != null && !retList.isEmpty()) ? retList.get(0) : null;
	}

	@Override
	public List<ScriptInfo> findScriptInfoByScriptTypeCode(String scriptTypeCode, String deviceModel) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ScriptInfo si ")
		  .append(" where 1=1 ")
		  .append(" and si.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (StringUtils.isNotBlank(scriptTypeCode)) {
			sb.append(" and si.scriptType.scriptTypeCode = :scriptTypeCode ");
		}
		if (StringUtils.isNotBlank(deviceModel)) {
			sb.append(" and si.deviceModel like :deviceModel ");
		}
		
		sb.append(" order by si.scriptCode");
		
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (StringUtils.isNotBlank(deviceModel)) {
			q.setParameter("deviceModel", deviceModel);
		}
		if (StringUtils.isNotBlank(scriptTypeCode)) {
			q.setParameter("scriptTypeCode", scriptTypeCode);
		}

		return (List<ScriptInfo>)q.list();
	}
	
	@Override
	public ScriptInfo findDefaultScriptInfoByScriptTypeAndDeviceModel(String scriptType, String deviceModel) throws ServiceLayerException {
		StringBuffer sb = new StringBuffer();
		sb.append(" select si ")
		  .append(" from ScriptInfo si ")
		  .append("     ,ScriptDefaultMapping sdm ")
		  .append(" where 1=1 ")
		  .append(" and si.scriptCode = sdm.defaultScriptCode ")
		  .append(" and si.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and sdm.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and sdm.scriptType = :scriptType ")
		  .append(" and sdm.deviceModel = :deviceModel ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());
		q.setParameter("scriptType", scriptType);
		q.setParameter("deviceModel", deviceModel);

		List<ScriptInfo> retList = (List<ScriptInfo>)q.list();
		return (retList != null && !retList.isEmpty()) ? retList.get(0) : null;
	}
	

	@Override
	public boolean deleteScriptInfo(ScriptInfo info, String actionBy) {
		if (info != null) {
			info.setDeleteFlag(MARK_AS_DELETE);
			info.setUpdateBy(actionBy);
			info.setUpdateTime(new Timestamp(new Date().getTime()));
			info.setDeleteBy(actionBy);
			info.setDeleteTime(new Timestamp(new Date().getTime()));

			for(ScriptStepAction action : info.getScriptStepActions()) {
				action.setDeleteFlag(MARK_AS_DELETE);
				action.setUpdateBy(actionBy);
				action.setUpdateTime(new Timestamp(new Date().getTime()));
				action.setDeleteBy(actionBy);
				action.setDeleteTime(new Timestamp(new Date().getTime()));
				
				getHibernateTemplate().saveOrUpdate(action);
			}
			
			getHibernateTemplate().saveOrUpdate(info);
		}else {
			return false;
		}
		return true;
	}
	

	@Override
	public void saveScriptInfo(ScriptInfo model) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.persist(model);
		session.flush();
		session.clear();
	}
}
