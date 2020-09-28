package com.cmap.plugin.module.circuit;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("CircuitDAO")
@Transactional
public class CircuitDAOImpl extends BaseDaoHibernate implements CircuitDAO {
    @Log
    private static Logger log;

	@Override
	public List<Object[]> findModuleBlockedList(CircuitVO cVO, Integer startRow, Integer pageLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModuleCircuitDiagramInfo> findModuleInfo(CircuitVO cVO) {
		StringBuffer sb = new StringBuffer();
        sb.append(" select mcdi ")
          .append(" from ModuleCircuitDiagramInfo mcdi ")
          .append(" where 1=1 ")
          .append(" and mcdi.deleteFlag = '"+Constants.DATA_MARK_NOT_DELETE+"' ");

        if (StringUtils.isNotBlank(cVO.getQueryCircleId())) {
            sb.append(" and mcdi.circleId = :circleId ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryType())) {
            sb.append(" and mcdi.type = :type ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryName())) {
            sb.append(" and mcdi.name = :name ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        
        if (StringUtils.isNotBlank(cVO.getQueryCircleId())) {
        	q.setParameter("circleId", cVO.getQueryCircleId());
        }
        if (StringUtils.isNotBlank(cVO.getQueryType())) {
        	q.setParameter("type", cVO.getQueryType());
        }
        if (StringUtils.isNotBlank(cVO.getQueryName())) {
        	q.setParameter("name", cVO.getQueryName());
        }
        
        return (List<ModuleCircuitDiagramInfo>)q.list();
	}

	@Override
    public void deleteSetting(String e1Ip) {
		StringBuffer sb = new StringBuffer();
        sb.append(" delete ModuleCircuitDiagramSetting ")
          .append(" where 1=1 ")
          .append(" and e1Ip = :e1Ip ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("e1Ip", e1Ip);
        
        q.executeUpdate();
    }
	
	@Override
    public void saveOrUpdateSetting(List<ModuleCircuitDiagramSetting> entities) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        for(ModuleCircuitDiagramSetting entity : entities) {
        	session.saveOrUpdate(entity);
        }        
        session.close();
    }
	
	@Override
	public List<ModuleCircuitDiagramSetting> findModuleSettingByIp(String e1Ip) {
		StringBuffer sb = new StringBuffer();
        sb.append(" select mcds ")
          .append(" from ModuleCircuitDiagramSetting mcds ")
          .append(" where 1=1 ")
          .append(" and mcds.deleteFlag = '"+Constants.DATA_MARK_NOT_DELETE+"' ");

        if (StringUtils.isNotBlank(e1Ip)) {
            sb.append(" and mcds.e1Ip = :e1Ip ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        
        if (StringUtils.isNotBlank(e1Ip)) {
        	q.setParameter("e1Ip", e1Ip);
        }
        
        return (List<ModuleCircuitDiagramSetting>)q.list();
	}
}
