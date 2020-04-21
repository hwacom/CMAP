package com.cmap.plugin.module.iptracepoller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("ipTracePollerDAO")
@Transactional
public class IpTracePollerDAOImpl extends BaseDaoHibernate implements IpTracePollerDAO {
    @Log
    private static Logger log;

    @Override
    public ModuleIpTrace findModuleIpTraceByUK(String clientIp, String startTime) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mit ")
          .append(" from ModuleIpTrace mit ")
          .append(" where 1=1 ")
          .append(" and mit.clientMac = :clientMac ")
          .append(" and mit.startTime = :startTime ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("clientMac", clientIp);
        q.setParameter("startTime", startTime);
        
        return (ModuleIpTrace)q.uniqueResult();
    }

    @Override
    public List<IpTracePollerVO> findModuleIpTrace(IpTracePollerVO searchVO, Integer startRow, Integer pageLength) {
    	// 回傳資料的VO容器
    	List<IpTracePollerVO> retList = new ArrayList<>();
    	
        StringBuffer sb = new StringBuffer();
        sb.append(" select client_ip '1',start_time '2',end_time '3',client_mac '4',group_name '5',device_name '6',port_name '7' ")
        	.append(" from module_ip_trace mit")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            sb.append(" and mit.client_mac = :queryClientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            sb.append(" and mit.client_ip = :queryClientIp ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            sb.append(" and mit.group_id = :queryGroupId ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDevice())) {
            sb.append(" and mit.device_id = :queryDevice ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            sb.append(" and mit.start_time >= :queryDateTimeBeginStr ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&&StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            sb.append(" and mit.start_time <= :queryDateTimeEndStr ");
        }

       if (StringUtils.isNotBlank(searchVO.getOrderColumn())) {
           sb.append(" order by ").append(searchVO.getOrderColumn()).append(" ").append(searchVO.getOrderDirection());

       } else {
    	   sb.append(" order by mit.start_time, mit.client_ip desc ");
       }

       Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //log.debug("xxxDebug:"+sb.toString());
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            q.setParameter("queryClientMac", searchVO.getQueryClientMac());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            q.setParameter("queryClientIp", searchVO.getQueryClientIp());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            q.setParameter("queryGroupId", searchVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDevice())) {
            q.setParameter("queryDevice", searchVO.getQueryDevice());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            q.setParameter("queryDateTimeBeginStr", searchVO.getQueryDateBegin().concat(" ").concat(searchVO.getQueryTimeBegin()));
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&& StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            q.setParameter("queryDateTimeEndStr", searchVO.getQueryDateEnd().concat(" ").concat(searchVO.getQueryTimeEnd()));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }
        // createNative查回時是Object[]格式需要轉型hibernate會對應DB field type
        List<Object[]> dataList = (List<Object[]>)q.list();
        if (dataList != null && !dataList.isEmpty()) {
        	IpTracePollerVO retVO = null;
        	for (Object[] data : dataList) {
        		retVO = new IpTracePollerVO();
        		
        		String clientIp = data[0].toString();
        		String startTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[1]);
        		// endTime is nullable 預設null顯示空字串
        		String endTime = "";
        		if(data[2]!=null)
        			endTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[2]);
        		String clientMac = data[3].toString();
        		String groupName = data[4].toString();
        		String deviceName = data[5].toString();
        		String portName = data[6].toString();
        		
                retVO.setClientIp(clientIp);
                retVO.setStartTime(startTime);
                retVO.setEndTime(endTime);
                retVO.setClientMac(clientMac);
                retVO.setGroupName(groupName);
                retVO.setDeviceName(deviceName);
                retVO.setPortName(portName);
                
        		retList.add(retVO);
        	}
        }
        return retList;
	}
    
	@Override
	 public long countIpTraceDataFromDB(IpTracePollerVO searchVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(data_id) ")
        	.append(" from module_ip_trace mit ")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            sb.append(" and mit.client_mac = :queryClientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            sb.append(" and mit.client_ip = :queryClientIp ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            sb.append(" and mit.group_id = :queryGroupId ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDevice())) {
            sb.append(" and mit.device_id = :queryDevice ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            sb.append(" and mit.start_time >= :queryDateTimeBeginStr ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&&StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            sb.append(" and mit.start_time <= :queryDateTimeEndStr ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            q.setParameter("queryClientMac", searchVO.getQueryClientMac());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            q.setParameter("queryClientIp", searchVO.getQueryClientIp());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            q.setParameter("queryGroupId", searchVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDevice())) {
            q.setParameter("queryDevice", searchVO.getQueryDevice());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            q.setParameter("queryDateTimeBeginStr", searchVO.getQueryDateBegin().concat(" ").concat(searchVO.getQueryTimeBegin()));
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&& StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            q.setParameter("queryDateTimeEndStr", searchVO.getQueryDateEnd().concat(" ").concat(searchVO.getQueryTimeEnd()));
        }

        return DataAccessUtils.longResult(q.list());
	}
}
