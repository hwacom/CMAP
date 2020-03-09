package com.cmap.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.PrtgDAO;
import com.cmap.model.DeviceList;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.PrtgUserRightSetting;
import com.nimbusds.oauth2.sdk.util.StringUtils;

@Repository("prtgDAO")
@Transactional
public class PrtgDAOImpl extends BaseDaoHibernate implements PrtgDAO {
	@Log
    private static Logger log;
	
	@Override
	public PrtgAccountMapping findPrtgAccountMappingBySourceId(String sourceId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from PrtgAccountMapping pam ")
		  .append(" where 1=1 ")
		  .append(" and pam.sourceId = :sourceId ");

	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("sourceId", sourceId);

	    List<PrtgAccountMapping> entities = (List<PrtgAccountMapping>)q.list();
	    if (entities == null || (entities != null && entities.isEmpty())) {
	    	return null;
	    } else {
	    	return entities.get(0);
	    }
	}

    @Override
    public List<PrtgAccountMapping> findPrtgAccountMappingByAccount(String prtgAccount) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from PrtgAccountMapping pam ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(prtgAccount)) {
            sb.append(" and pam.prtgAccount = :prtgAccount ");
        }
        sb.append(" order by pam.prtgAccount ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(prtgAccount)) {
            q.setParameter("prtgAccount", prtgAccount);
        }
        return (List<PrtgAccountMapping>)q.list();
    }

    @Override
    public List<PrtgUserRightSetting> findPrtgUserRightSetting(String prtgAccount,
            String settingType) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from PrtgUserRightSetting purs ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(prtgAccount)) {
            sb.append(" and purs.prtgAccount = :prtgAccount ");
        }
        if (StringUtils.isNotBlank(settingType)) {
            sb.append(" and purs.settingType = :settingType ");
        }
        sb.append(" order by purs.settingType desc ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(prtgAccount)) {
            q.setParameter("prtgAccount", prtgAccount);
        }
        if (StringUtils.isNotBlank(settingType)) {
            q.setParameter("settingType", settingType);
        }
        return (List<PrtgUserRightSetting>)q.list();
    }

    @Override
    public List<DeviceList> findPrtgUserRightGroupAndDeviceFullInfo(String prtgAccount) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select distinct dl ")
          .append(" from PrtgUserRightSetting purs ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and purs.parentNode = dl.groupId ")
          .append(" and purs.settingValue = dl.deviceId ")
          .append(" and purs.settingType = '").append(Constants.PRTG_RIGHT_SETTING_TYPE_OF_DEVICE).append("' ");

        if (StringUtils.isNotBlank(prtgAccount)) {
            sb.append(" and purs.prtgAccount = :prtgAccount ");
        }
        sb.append(" order by dl.groupId ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(prtgAccount)) {
            q.setParameter("prtgAccount", prtgAccount);
        }
        return (List<DeviceList>)q.list();
    }

    @Override
    public List<Object[]> findPrtgUserRightGroupList(String prtgAccount) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select distinct ")
          .append("     dl.groupId ")
          .append("    ,dl.groupName ")
          .append(" from PrtgUserRightSetting purs ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and purs.settingValue = dl.groupId ")
          .append(" and purs.settingType = '").append(Constants.PRTG_RIGHT_SETTING_TYPE_OF_GROUP).append("' ");

        if (StringUtils.isNotBlank(prtgAccount)) {
            sb.append(" and purs.prtgAccount = :prtgAccount ");
        }
        sb.append(" order by dl.groupId ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(prtgAccount)) {
            q.setParameter("prtgAccount", prtgAccount);
        }
        return (List<Object[]>)q.list();
    }

    @Override
    public List<Object[]> findPrtgUserRightDeviceList(String prtgAccount, String groupId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select distinct ")
          .append("     dl.deviceId ")
          .append("    ,dl.deviceName ")
          .append(" from PrtgUserRightSetting purs ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and purs.parentNode = dl.groupId ")
          .append(" and purs.settingValue = dl.deviceId ")
          .append(" and purs.settingType = '").append(Constants.PRTG_RIGHT_SETTING_TYPE_OF_DEVICE).append("' ");

        if (StringUtils.isNotBlank(prtgAccount)) {
            sb.append(" and purs.prtgAccount = :prtgAccount ");
        }
        if (StringUtils.isNotBlank(groupId)) {
            sb.append(" and dl.groupId = :groupId ");
        }
        sb.append(" order by dl.deviceId ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(prtgAccount)) {
            q.setParameter("prtgAccount", prtgAccount);
        }
        if (StringUtils.isNotBlank(groupId)) {
            q.setParameter("groupId", groupId);
        }
        return (List<Object[]>)q.list();
    }
    
    @Override
    public List<PrtgUserRightSetting> findPrtgUserRightSettingBySettingValueAndType(String settingValue, String settingType) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from PrtgUserRightSetting purs ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(settingValue)) {
            sb.append(" and purs.settingValue = :settingValue ");
        }
        if (StringUtils.isNotBlank(settingType)) {
            sb.append(" and purs.settingType = :settingType ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(settingValue)) {
            q.setParameter("settingValue", settingValue);
        }
        if (StringUtils.isNotBlank(settingType)) {
            q.setParameter("settingType", settingType);
        }
        return (List<PrtgUserRightSetting>)q.list();
    }
}
