package com.cmap.plugin.module.wifipoller;

import java.util.ArrayList;
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
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("wifiPollerDAO")
@Transactional
public class WifiPollerDAOImpl extends BaseDaoHibernate implements WifiPollerDAO {
    @Log
    private static Logger log;

    @Override
    public List<ModuleWifiTraceMst> findModuleWifiTraceMst(String clientMac, String startTime, String endTime, String clientIp, String apName, String ssid){
        StringBuffer sb = new StringBuffer();
        sb.append(" select mwtm ")
          .append(" from ModuleWifiTraceMst mwtm ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(clientMac)) {
            sb.append(" and mwtm.clientMac = :clientMac ");
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
        if (StringUtils.isNotBlank(startTime)) {
            sb.append(" and mwtm.startTime = :startTime ");
        }
        if (StringUtils.isNotBlank(endTime)) {
            sb.append(" and mwtm.endTime = :endTime ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

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
        sb.append(" select group_name '1', client_mac '2', start_time '3', end_time '4', client_ip '5', ap_name '6', ssid '7', total_traffic '8', upload_traffic '9', download_traffic '10' ")
        	.append(" from module_wifi_trace_mst mst")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            sb.append(" and mst.group_id = :queryGroupId ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            sb.append(" and mst.client_mac = :queryClientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            sb.append(" and mst.client_ip = :queryClientIp ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            sb.append(" and mst.ap_name = :queryApName ");
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            sb.append(" and mst.ssid = :querySsid ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            sb.append(" and mst.start_time >= :queryDateTimeBeginStr ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&&StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            sb.append(" and mst.start_time <= :queryDateTimeEndStr ");
        }
        
       if (StringUtils.isNotBlank(searchVO.getOrderColumn())) {
           sb.append(" order by ").append(searchVO.getOrderColumn()).append(" ").append(searchVO.getOrderDirection());

       } else {
    	   sb.append(" order by mst.start_time, mst.client_ip desc ");
       }

       Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //log.debug("xxxDebug:"+sb.toString());
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            q.setParameter("queryGroupId", searchVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            q.setParameter("queryClientMac", searchVO.getQueryClientMac());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            q.setParameter("queryClientIp", searchVO.getQueryClientIp());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            q.setParameter("queryApName", searchVO.getQueryApName());
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            q.setParameter("querySsid", searchVO.getQuerySsid());
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
        	WifiPollerVO retVO = null;
        	for (Object[] data : dataList) {
        		retVO = new WifiPollerVO();
        		
        		String groupName = data[0].toString();
        		String clientMac = data[1].toString();
        		
        		String startTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[2]);
        		// endTime is nullable 預設null顯示空字串
        		String endTime = "";
        		if(data[3]!=null)
        			endTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[3]);
        		String clientIp = data[4].toString();
        		String apName = data[5].toString();
        		String ssid = data[6].toString();
        		String totalTraffic = data[7].toString();
                String uploadTraffic = data[8].toString();
                String downloadTraffic = data[9].toString();
        		
                retVO.setGroupName(groupName);
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

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            sb.append(" and mst.group_id = :queryGroupId ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            sb.append(" and mst.client_mac = :queryClientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            sb.append(" and mst.client_ip = :queryClientIp ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            sb.append(" and mst.ap_name = :queryApName ");
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            sb.append(" and mst.ssid = :querySsid ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            sb.append(" and mst.start_time >= :queryDateTimeBeginStr ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&&StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            sb.append(" and mst.start_time <= :queryDateTimeEndStr ");
        }

       sb.append(" order by mst.start_time, mst.client_ip desc ");

       Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            q.setParameter("queryGroupId", searchVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientMac())) {
            q.setParameter("queryClientMac", searchVO.getQueryClientMac());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientIp())) {
            q.setParameter("queryClientIp", searchVO.getQueryClientIp());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryApName())) {
            q.setParameter("queryApName", searchVO.getQueryApName());
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            q.setParameter("querySsid", searchVO.getQuerySsid());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            q.setParameter("queryDateTimeBeginStr", searchVO.getQueryDateBegin().concat(" ").concat(searchVO.getQueryTimeBegin()));
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&& StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            q.setParameter("queryDateTimeEndStr", searchVO.getQueryDateEnd().concat(" ").concat(searchVO.getQueryTimeEnd()));
        }

        return DataAccessUtils.longResult(q.list());
	}
	
	@Override
	public List<WifiPollerDetailVO> findModuleWifiTraceDetail(WifiPollerVO searchVO) {
    	// 回傳資料的VO容器
    	List<WifiPollerDetailVO> retList = new ArrayList<WifiPollerDetailVO>();
    	
        StringBuffer sb = new StringBuffer();
        sb.append(" select detail.client_mac, detail.polling_time, detail.upload_traffic, detail.download_traffic, detail.total_traffic, detail.rssi, detail.noise, detail.snr ")
        	.append(" from module_wifi_trace_detail detail")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryClientMac()) ) {
        	sb.append(" and detail.client_mac = :queryClientMac ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryStartTime()) ) {
        	sb.append(" and detail.polling_time >= :queryStartTime ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryEndTime()) ) {
        	sb.append(" and detail.polling_time <= :queryEndTime ");
        }
        sb.append(" order by detail.polling_time");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
        	session.beginTransaction();
        }

		Query<?> q = session.createNativeQuery(sb.toString());

		 if (StringUtils.isNotBlank(searchVO.getQueryClientMac()) ) {
			q.setParameter("queryClientMac", searchVO.getQueryClientMac());
		}
		 if (StringUtils.isNotBlank(searchVO.getQueryStartTime()) ) {
			q.setParameter("queryStartTime", searchVO.getQueryStartTime());
		}
		 if (StringUtils.isNotBlank(searchVO.getQueryEndTime()) ) {
			q.setParameter("queryEndTime", searchVO.getQueryEndTime());
		}

		// createNative查回時是Object[]格式需要轉型hibernate會對應DB field type
        List<Object[]> dataList = (List<Object[]>)q.list();
        if (dataList != null && !dataList.isEmpty()) {
        	for (Object[] data : dataList) {
        		WifiPollerDetailVO retVO = new WifiPollerDetailVO();
				
        		String clientMac = data[0].toString();
        		String pollingTime = data[1].toString(); //前端moment.js有限定dateString格式要符合,不要亂改啊!!!!
        		String uploadTraffic = data[2].toString();
				String downloadTraffic = data[3].toString();
				String totalTraffic = data[4].toString();
				String rssi = data[5].toString();
				String noise = data[6].toString();
				String snr = data[7].toString();
				
				retVO.setClientMac(clientMac);
				retVO.setPollingTime(pollingTime);
				retVO.setUploadTraffic(uploadTraffic);
				retVO.setDownloadTraffic(downloadTraffic);
				retVO.setTotalTraffic(totalTraffic);
				retVO.setRssi(rssi);
				retVO.setNoise(noise);
				retVO.setSnr(snr);
				
				retList.add(retVO);
        	}
		}
		return retList;
	}
}
