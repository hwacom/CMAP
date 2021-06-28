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
	public List<ModuleCircuitE1OpenList> findModuleE1OpenList(CircuitOpenListVO cVO) {
		StringBuffer sb = new StringBuffer();
        sb.append(" select mcbl ")
          .append(" from ModuleCircuitE1OpenList mcbl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwSID())) {
            sb.append(" and mcbl.srcSid = :srcSid ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryDstE1gwSID())) {
            sb.append(" and mcbl.dstSid = :dstSid ");
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwIP())) {
            sb.append(" and mcbl.srcE1gwIp = :srcE1gwIp ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryDstE1gwIP())) {
            sb.append(" and mcbl.dstE1gwIp = :dstE1gwIp ");
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcPort())) {
            sb.append(" and mcbl.srcPortNumber = :srcPortNumber ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryDstPort())) {
            sb.append(" and mcbl.dstPortNumber = :dstPortNumber ");
        }
        
        if (StringUtils.isNotBlank(cVO.getOrderColumn())) {
			sb.append(" order by ").append(cVO.getOrderColumn()).append(" ").append(cVO.getOrderDirection());
		} else {
			sb.append(" order by mcbl.createTime desc ");
		}
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        
        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwSID())) {
        	q.setParameter("srcSid", cVO.getQuerySrcE1gwSID());
        }
        if (StringUtils.isNotBlank(cVO.getQueryDstE1gwSID())) {
        	q.setParameter("dstSid", cVO.getQueryDstE1gwSID());
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwIP())) {
        	q.setParameter("srcE1gwIp", cVO.getQuerySrcE1gwIP());
        }
        if (StringUtils.isNotBlank(cVO.getQueryDstE1gwIP())) {
        	q.setParameter("dstE1gwIp", cVO.getQueryDstE1gwIP());
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcPort())) {
        	q.setParameter("srcPortNumber", cVO.getQuerySrcPort());
        }
        if (StringUtils.isNotBlank(cVO.getQueryDstPort())) {
        	q.setParameter("dstPortNumber", cVO.getQueryDstPort());
        }
        
        if (cVO.getStartNum() != null && cVO.getPageLength() != null) {
            q.setFirstResult(cVO.getStartNum());
            q.setMaxResults(cVO.getPageLength());
        }
        
        return (List<ModuleCircuitE1OpenList>)q.list();
	}

	@Override
	public List<Object[]> findModuleInfoAndList(CircuitVO cVO) {
		StringBuffer sb = new StringBuffer();
        sb.append(" select mcdi.NE_NAME, mcdi.E1GW_IP, mcdi.SR_PREFIX_SID, mcdi.LOCAL_AREA_CSR_IP, aa.PORT_NUMBER  ")
          .append(" from Module_Circuit_Diagram_Info mcdi ")
          .append(" 	left JOIN (SELECT mcds.E1GW_IP, mcds.PORT_NUMBER FROM cmap.module_circuit_diagram_setting mcds ")
          .append(" 		WHERE mcds.USED_FLAG = 'N' ")
          .append(" 				AND NOT EXISTS (SELECT 1 FROM cmap.module_circuit_e1open_list cel ")
          .append(" 						WHERE cel.SOURCE_SID=mcds.SR_PREFIX_SID AND cel.SOURCE_PORT_NUMBER=mcds.PORT_NUMBER)) aa ")
  		  .append(" 		on mcdi.E1GW_IP = aa.E1GW_IP  AND mcdi.delete_Flag = '"+Constants.DATA_MARK_NOT_DELETE+"' ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(cVO.getQueryCircleId())) {
            sb.append(" and mcdi.CIRCLE_ID = :circleId ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryStationEngName())) {
            sb.append(" and mcdi.STATION_ENG_NAME = :stationEngName ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryNeName())) {
            sb.append(" and mcdi.NE_NAME = :neName ");
        }
        if (cVO.getQueryE1gwIpList() != null && !cVO.getQueryE1gwIpList().isEmpty()) {
            sb.append(" and mcdi.E1GW_IP in (:e1gwIp) ");
        }
        
        sb.append(" order by mcdi.SR_PREFIX_SID ");
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());
        
        if (StringUtils.isNotBlank(cVO.getQueryCircleId())) {
        	q.setParameter("circleId", cVO.getQueryCircleId());
        }
        if (StringUtils.isNotBlank(cVO.getQueryStationEngName())) {
        	q.setParameter("stationEngName", cVO.getQueryStationEngName());
        }
        if (StringUtils.isNotBlank(cVO.getQueryNeName())) {
        	q.setParameter("neName", cVO.getQueryNeName());
        }
        if (cVO.getQueryE1gwIpList() != null && !cVO.getQueryE1gwIpList().isEmpty()) {
        	q.setParameterList("e1gwIp", cVO.getQueryE1gwIpList());
        }
        
        return (List<Object[]>)q.list();
	}

	@Override
	public List<ModuleCircuitDiagramInfo> findModuleInfoAndListByVO(CircuitVO cVO) {
		StringBuffer sb = new StringBuffer();
        sb.append(" select mcdi ")
          .append(" from ModuleCircuitDiagramInfo mcdi ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(cVO.getQueryCircleId())) {
            sb.append(" and mcdi.circleId = :circleId ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryStationEngName())) {
            sb.append(" and mcdi.stationEngName = :stationEngName ");
        }
        if (StringUtils.isNotBlank(cVO.getQueryNeName())) {
            sb.append(" and mcdi.neName = :neName ");
        }
        if (cVO.getQueryE1gwIpList() != null && !cVO.getQueryE1gwIpList().isEmpty()) {
            sb.append(" and mcdi.e1gwIp in (:e1gwIp) ");
        }
        if (cVO.getQuerySrPrefixSidList() != null && !cVO.getQuerySrPrefixSidList().isEmpty()) {
	        sb.append(" and mcdi.srPrefixSid in (:sidList) ");
	    }
        
        sb.append(" order by mcdi.srPrefixSid ");
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        
        if (StringUtils.isNotBlank(cVO.getQueryCircleId())) {
        	q.setParameter("circleId", cVO.getQueryCircleId());
        }
        if (StringUtils.isNotBlank(cVO.getQueryStationEngName())) {
        	q.setParameter("stationEngName", cVO.getQueryStationEngName());
        }
        if (StringUtils.isNotBlank(cVO.getQueryNeName())) {
        	q.setParameter("neName", cVO.getQueryNeName());
        }
        if (cVO.getQueryE1gwIpList() != null && !cVO.getQueryE1gwIpList().isEmpty()) {
        	q.setParameterList("e1gwIp", cVO.getQueryE1gwIpList());
        }
        if (cVO.getQuerySrPrefixSidList() != null && !cVO.getQuerySrPrefixSidList().isEmpty()) {
            q.setParameterList("sidList", cVO.getQuerySrPrefixSidList());
        }
        
        return (List<ModuleCircuitDiagramInfo>)q.list();
	}
	
	@Override
	public List<ModuleCircuitDiagramSetting> findModuleCircuitDiagramSetting(CircuitOpenListVO cVO) {
		StringBuffer sb = new StringBuffer();
        sb.append(" select mcds ")
          .append(" from ModuleCircuitDiagramSetting mcds ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwSID())) {
            sb.append(" and mcds.srPrefixSid = :e1gwSID ");
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwIP())) {
            sb.append(" and mcds.e1gwIp = :e1gwIp ");
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcPort())) {
            sb.append(" and mcds.port = :port ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        
        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwSID())) {
        	q.setParameter("e1gwSID", cVO.getQuerySrcE1gwSID());
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcE1gwIP())) {
        	q.setParameter("e1gwIp", cVO.getQuerySrcE1gwIP());
        }
        if (StringUtils.isNotBlank(cVO.getQuerySrcPort())) {
        	q.setParameter("port", cVO.getQuerySrcPort());
        }
        
        return (List<ModuleCircuitDiagramSetting>)q.list();
	}
	
	@Override
	public void saveOrUpdateCircuitData(List<Object> entityList) {
		for (Object entity : entityList) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
	}

	@Override
	public void deleteCircuitData(List<Object> entities) {
		getHibernateTemplate().deleteAll(entities);
	}

}
