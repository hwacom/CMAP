package com.cmap.plugin.module.inventory.info;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;
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
		  .append(" where ii.deleteFlag = 'N' ");

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
		  .append(" and ii.deviceId = :deviceId ")
		  .append(" and ii.deleteFlag = 'N'  ");
		
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("deviceId", deviceId);
	    
	    return (InventoryInfo)q.setMaxResults(1).uniqueResult();
	}
	
	@Override
	public List<InventoryInfo> findInventoryInfo(InventoryInfoVO vo){
		StringBuffer sb = new StringBuffer();
		sb.append(" from InventoryInfo ii ")
		  .append(" where ii.deleteFlag = 'N' ");

		if (StringUtils.isNotBlank(vo.getQueryGroupName())) {
			sb.append(" and ii.groupName like :groupName ");
		} else if (vo.getQueryDeviceList() != null && !vo.getQueryDeviceList().isEmpty()) {
            sb.append(" and ii.deviceId in (:deviceIdList) ");
        }else if (StringUtils.isNotBlank(vo.getQueryDevice())) {
            sb.append(" and ii.deviceId = :deviceId ");
        }
		if (StringUtils.isNotBlank(vo.getQueryProbe())) {
			sb.append(" and ii.probe like :probe ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceName())) {
			sb.append(" and ii.deviceName like :deviceName ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceType())) {
			sb.append(" and ii.deviceType = :deviceType ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryIP())) {
			sb.append(" and ii.deviceIp like :IP ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryModel())) {
			sb.append(" and ii.model like :model ");
		}
		
		if (vo.isQueryDiffOnly()) {
			sb.append(" and DIFFERENCE_COMPARISON <> '' and DIFFERENCE_COMPARISON is not null ");
		}
		
		if (StringUtils.isNotBlank(vo.getOrderColumn())) {
			sb.append(" order by ").append(vo.getOrderColumn());
			if (StringUtils.isNotBlank(vo.getOrderDirection())) {
				sb.append(" ").append(vo.getOrderDirection());
			}
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
	    	q.setParameter("probe", "%".concat(vo.getQueryProbe()).concat("%"));
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceName())) {
			q.setParameter("deviceName", "%".concat(vo.getQueryDeviceName()).concat("%"));
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceType())) {
			q.setParameter("deviceType", vo.getQueryDeviceType());
		}
		
		if (StringUtils.isNotBlank(vo.getQueryIP())) {
			q.setParameter("IP", "%".concat(vo.getQueryIP()).concat("%"));
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
		  .append(" where ii.deviceId like 'UA%' ")
		  .append(" and ii.deleteFlag = 'N' ");
		
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
	
	@Override
	public void saveOrUpdateInventory(List<Object> entityList) {
		for (Object entity : entityList) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
	}
	
    @Override
    public Object[] findInvDetailDataByDeviceId(String deviceId, String deviceType) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select * ")
          .append(" from module_inventory_info_" + deviceType + "_detail dtb ")
          .append(" where dtb.device_Id = :deviceId ")
          .append(" and dtb.delete_Flag = 'N' ");
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());
        
        q.setParameter("deviceId", deviceId);
        
        return (Object[])q.setMaxResults(1).uniqueResult();
    }
    
    @Override
    public Object findInvDetailDataByDeviceIdType(String deviceId, String deviceType) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from InventoryInfo" + deviceType + "Detail dtb ")
          .append(" where dtb.deviceId = :deviceId ")
          .append(" and  dtb.deleteFlag = 'N' ");
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        
        q.setParameter("deviceId", deviceId);
        
        return (Object)q.setMaxResults(1).uniqueResult();
    }
    
    @Override
    public List<Object[]> findInvInfoAndDetailDataByDeviceIdType(InventoryInfoVO vo) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from InventoryInfo ii left join InventoryInfo"+vo.getQueryDeviceType()+"Detail det on ii.deviceId = det.deviceId ")
        	.append(" where ii.deleteFlag = 'N' ");
        
        if (StringUtils.isNotBlank(vo.getQueryGroupName())) {
			sb.append(" and ii.groupName like :groupName ");
		} else if (vo.getQueryDeviceList() != null && !vo.getQueryDeviceList().isEmpty()) {
            sb.append(" and ii.deviceId in (:deviceIdList) ");
        }else if (StringUtils.isNotBlank(vo.getQueryDevice())) {
            sb.append(" and ii.deviceId = :deviceId ");
        }
		if (StringUtils.isNotBlank(vo.getQueryProbe())) {
			sb.append(" and ii.probe like :probe ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceName())) {
			sb.append(" and ii.deviceName like :deviceName ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceType())) {
			sb.append(" and ii.deviceType = :deviceType ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryIP())) {
			sb.append(" and ii.deviceIp like :ip ");
		}
		
		if (StringUtils.isNotBlank(vo.getQueryModel())) {
			sb.append(" and ii.model like :model ");
		}
		
		if (vo.isQueryDiffOnly()) {
			sb.append(" and DIFFERENCE_COMPARISON <> '' and DIFFERENCE_COMPARISON is not null ");
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
	    	q.setParameter("probe", "%".concat(vo.getQueryProbe()).concat("%"));
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceName())) {
			q.setParameter("deviceName", "%".concat(vo.getQueryDeviceName()).concat("%"));
		}
		
		if (StringUtils.isNotBlank(vo.getQueryDeviceType())) {
			q.setParameter("deviceType", vo.getQueryDeviceType());
		}
		
		if (StringUtils.isNotBlank(vo.getQueryIP())) {
			q.setParameter("ip", "%".concat(vo.getQueryIP()).concat("%"));
		}
		
		if (StringUtils.isNotBlank(vo.getQueryModel())) {
			q.setParameter("model", "%".concat(vo.getQueryModel()).concat("%"));
		}
        
		if (vo.getStartNum() != null && vo.getPageLength() != null) {
            q.setFirstResult(vo.getStartNum());
            q.setMaxResults(vo.getPageLength());
        }
		
        return (List<Object[]>)q.list();
    }
}
