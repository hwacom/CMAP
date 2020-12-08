package com.cmap.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.InventoryInfoDAO;
import com.cmap.model.InventoryInfo;
import com.cmap.service.vo.InventoryInfoVO;

@Repository("inventoryInfoDAO")
@Transactional
public class InventoryInfoDAOImpl extends BaseDaoHibernate implements InventoryInfoDAO {
	@Log
    private static Logger log;
	
	@Override
	public List<InventoryInfo> findInventoryInfoByDeviceId(List<String> ids){
		StringBuffer sb = new StringBuffer();
		sb.append(" from InventoryInfo ii ")
		  .append(" where 1=1 ");

		if (ids != null && !ids.isEmpty()) {
			sb.append(" and ii.deviceId in (:deviceId) ");
		}

	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (ids != null && !ids.isEmpty()) {
	    	q.setParameterList("deviceId", ids);
	    }

	    return (List<InventoryInfo>)q.list();
	}

	@Override
	public InventoryInfo findLastInventoryInfoByDeviceId(String deviceId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from InventoryInfo ii ")
		  .append(" where 1=1 ")
		  .append(" and ii.deviceId = :deviceId ");
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("deviceId", deviceId);
	    
	    return (InventoryInfo)q.uniqueResult();
	}
	
	@Override
	public List<InventoryInfo> findInventoryInfo(InventoryInfoVO vo){
		StringBuffer sb = new StringBuffer();
		sb.append(" from InventoryInfo ii ")
		  .append(" where 1=1 ");

		if (StringUtils.isNotBlank(vo.getQueryGroupName())) {
			sb.append(" and ii.groupName like :groupName ");
		} else if (vo.getQueryDeviceList() != null && !vo.getQueryDeviceList().isEmpty()) {
            sb.append(" and ii.deviceId in (:deviceIdList) ");
        }else if (StringUtils.isNotBlank(vo.getQueryDevice())) {
            sb.append(" and ii.deviceId = :deviceId ");
        }
		if (StringUtils.isNotBlank(vo.getQueryProbe())) {
			sb.append(" and ii.probe = :probe ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceName())) {
			sb.append(" and ii.deviceName like :deviceName ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceType())) {
			sb.append(" and ii.deviceType = :deviceType ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryBrand())) {
			sb.append(" and ii.brand = :brand ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryModel())) {
			sb.append(" and ii.model like :model ");
		}
		
		if (vo.isQueryModifyOnly()) {
			sb.append(" and ii.modifyFlag = 'Y' ");
		}
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
		
	    if (StringUtils.isNotBlank(vo.getQueryGroupName())) {
			q.setParameter("groupName", "%".concat(vo.getQueryGroupName()).concat("%"));
		} else if (vo.getQueryDeviceList() != null && !vo.getQueryDeviceList().isEmpty()) {
	    	q.setParameter("deviceIdList", vo.getQueryDeviceList());
        }else if (StringUtils.isNotBlank(vo.getQueryDevice())) {
        	q.setParameter("deviceId", vo.getQueryDevice());
        }
	    
	    if (StringUtils.isNotBlank(vo.getQueryProbe())) {
	    	q.setParameter("probe", vo.getQueryProbe());
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceName())) {
			q.setParameter("deviceName", "%".concat(vo.getQueryDeviceName()).concat("%"));
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceType())) {
			q.setParameter("deviceType", vo.getQueryDeviceType());
		}
		
		if (StringUtils.isNotBlank(vo.getQueryBrand())) {
			q.setParameter("brand", vo.getQueryBrand());
		}
		
		if (StringUtils.isNotBlank(vo.getQueryModel())) {
			q.setParameter("model", "%".concat(vo.getQueryModel()).concat("%"));
		}
		
	    return (List<InventoryInfo>)q.list();
	}
	
	@Override
	public String findMaxInventoryInfoDeviceId(){
		StringBuffer sb = new StringBuffer();
		sb.append(" select max(ii.deviceId) from InventoryInfo ii ")
		  .append(" where ii.deviceId like 'UA%' ");
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
				
	    return (String)q.getSingleResult();
	}
	
	@Override
	public void saveOrUpdateInventoryInfo(List<InventoryInfo> entityList) {
		for (InventoryInfo entity : entityList) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
	}
	
	@Override
	public void deleteInventoryInfo(List<InventoryInfo> entities) {
		getHibernateTemplate().deleteAll(entities);	
	}
}
