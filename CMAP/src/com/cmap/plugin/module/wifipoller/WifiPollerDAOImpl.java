package com.cmap.plugin.module.wifipoller;

import java.math.BigDecimal;
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
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;
import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.netflow.NetFlowVO;

@Repository("wifiPollerDAO")
@Transactional
public class WifiPollerDAOImpl extends BaseDaoHibernate implements WifiPollerDAO {
    @Log
    private static Logger log;

    @Autowired
    @Qualifier("secondSessionFactory")
    private SessionFactory secondSessionFactory;

    @Override
    public List<ModuleWifiTraceMst> findModuleWifiTraceMst(String clientMac, String startTime, String endTime, String clientIp, String apName, String ssid){
        StringBuffer sb = new StringBuffer();
        sb.append(" select mwtm ")
          .append(" from ModuleWifiTraceMst mwtm ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(clientMac)) {
            sb.append(" and mwtm.clientMac = :clientMac ");
        }
        if (StringUtils.isNotBlank(startTime)) {
            sb.append(" and mwtm.startTime = :startTime ");
        }
        if (StringUtils.isNotBlank(endTime)) {
            sb.append(" and mwtm.endTime = :endTime ");
        }
        if (StringUtils.isNotBlank(clientIp)) {
            sb.append(" and mwtm.clientIp = :clientIp ");
        }
        if (StringUtils.isNotBlank(apName)) {
            sb.append(" and mwtm.apName = :apName ");
        }
        if (StringUtils.isNotBlank(clientIp)) {
            sb.append(" and mwtm.ssid = :ssid ");
        }

        Session session = secondSessionFactory.getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(clientMac)) {
            q.setParameter("clientMac", clientMac);
        }
        if (StringUtils.isNotBlank(startTime)) {
            q.setParameter("startTime", startTime);
        }
        if (StringUtils.isNotBlank(endTime)) {
            q.setParameter("endTime", endTime);
        }
        if (StringUtils.isNotBlank(clientIp)) {
            q.setParameter("clientIp", clientIp);
        }
        if (StringUtils.isNotBlank(apName)) {
            q.setParameter("apName", apName);
        }
        if (StringUtils.isNotBlank(ssid)) {
            q.setParameter("ssid", ssid);
        }

        return (List<ModuleWifiTraceMst>)q.list();
    }

    @Override
    public ModuleWifiTraceMst findModuleWifiTraceMstByUK(String clientMac, String startTime) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mwtm ")
          .append(" from ModuleWifiTraceMst mwtm ")
          .append(" where 1=1 ")
          .append(" and mwtm.clientMac = :clientMac ")
          .append(" and mwtm.startTime = :startTime ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("clientMac", clientMac);
        q.setParameter("startTime", startTime);
        
        return (ModuleWifiTraceMst)q.uniqueResult();
    }

    @Override
	public List<WifiPollerVO> findModuleWifiTraceMst(WifiPollerVO searchVO, Integer startRow, Integer pageLength) {
    	// 回傳資料的VO容器
    	List<WifiPollerVO> retList = new ArrayList<>();
    	
        StringBuffer sb = new StringBuffer();
        sb.append(" select client_mac '1', start_time '2', end_time '3', client_ip '4', ap_name '5', ssid '6', total_traffic '7', upload_traffic '8', download_traffic '9' ")
        	.append(" from module_wifi_trace_mst mst")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            sb.append(" and mst.client_mac = :clientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            sb.append(" and mst.client_ip = :clientIp ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            sb.append(" and mst.ap_name = :apName ");
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            sb.append(" and mst.ssid = :ssid ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDate())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) && StringUtils.isNotBlank(searchVO.getQueryTimeEnd())) {
            sb.append(" and (mst.start_time >= :queryDateTimeBeginStr and mst.start_time < :queryDateTimeEndStr) ");
        }

       if (StringUtils.isNotBlank(searchVO.getOrderColumn())) {
           sb.append(" order by ").append(searchVO.getOrderColumn()).append(" ").append(searchVO.getOrderDirection());

       } else {
    	   sb.append(" order by mst.start_time, mst.client_ip desc ");
       }

        Session session = secondSessionFactory.getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //log.debug("xxxDebug:"+sb.toString());
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            q.setParameter("clientMac", searchVO.getQueryClientMac());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            q.setParameter("clientIp", searchVO.getQueryClientIp());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            q.setParameter("apName", searchVO.getQueryApName());
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            q.setParameter("ssid", searchVO.getQuerySsid());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDate())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) && StringUtils.isNotBlank(searchVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", searchVO.getQueryDate().concat(" ").concat(searchVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", searchVO.getQueryDate().concat(" ").concat(searchVO.getQueryTimeEnd()));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }
        // createNative查回時是Object[]格式需要轉型hibernate會對應DB field type
        List<Object[]> dataList = (List<Object[]>)q.list();
        if (dataList != null && !dataList.isEmpty()) {
        	WifiPollerVO retVO = null;
        	for (Object[] data : dataList) {
        		retVO = new WifiPollerVO();
        		
        		String clientMac = data[0].toString();
        		
        		String startTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[1]);
        		// endTime is nullable 預設null顯示空字串
        		String endTime = "";
        		if(data[2]!=null)
        			endTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[2]);
        		String clientIp = data[3].toString();
        		String apName = data[4].toString();
        		String ssid = data[5].toString();
        		String totalTraffic = data[6].toString();
                String uploadTraffic = data[7].toString();
                String downloadTraffic = data[8].toString();
        		
                retVO.setClientMac(clientMac);
                retVO.setStartTime(startTime);
                retVO.setEndTime(endTime);
                retVO.setClientIp(clientIp);
                retVO.setApName(apName);
                retVO.setSsid(ssid);
                retVO.setTotalTraffic(totalTraffic);
                retVO.setUploadTraffic(uploadTraffic);
                retVO.setDownloadTraffic(downloadTraffic);
                
        		retList.add(retVO);
        	}
        }
        return retList;
	}
    
	@Override
	 public long countWifiMstDataFromDB(WifiPollerVO searchVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(data_id) ")
        	.append(" from module_wifi_trace_mst mst ")
        	.append(" where 1=1 ");
        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            sb.append(" and mst.client_mac = :clientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            sb.append(" and mst.client_ip = :clientIp ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            sb.append(" and mst.ap_name = :apName ");
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            sb.append(" and mst.ssid = :ssid ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDate())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) && StringUtils.isNotBlank(searchVO.getQueryTimeEnd())) {
            sb.append(" and (mst.start_time >= :queryDateTimeBeginStr and mst.start_time < :queryDateTimeEndStr) ");
        }

       sb.append(" order by mst.start_time, mst.client_ip desc ");

        Session session = secondSessionFactory.getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            q.setParameter("clientMac", searchVO.getQueryClientMac());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            q.setParameter("clientIp", searchVO.getQueryClientIp());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            q.setParameter("apName", searchVO.getQueryApName());
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            q.setParameter("ssid", searchVO.getQuerySsid());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDate())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) && StringUtils.isNotBlank(searchVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", searchVO.getQueryDate().concat(" ").concat(searchVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", searchVO.getQueryDate().concat(" ").concat(searchVO.getQueryTimeEnd()));
        }

        return DataAccessUtils.longResult(q.list());
	}
}
