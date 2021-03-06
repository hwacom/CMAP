package com.cmap.plugin.module.mobilepoller;

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

@Repository("mobilePollerDAO")
@Transactional
public class MobilePollerDAOImpl extends BaseDaoHibernate implements MobilePollerDAO {
    @Log
    private static Logger log;

    @Override
    public List<ModuleMobileTraceMst> findModuleMobileTraceMst(String clientSUPI, String startTime, String endTime, String clientNumber, String apName, String ssid){
        StringBuffer sb = new StringBuffer();
        sb.append(" select mwtm ")
          .append(" from ModuleMobileTraceMst mwtm ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(clientSUPI)) {
            sb.append(" and mwtm.clientSUPI = :clientSUPI ");
        }
        if (StringUtils.isNotBlank(clientNumber)) {
            sb.append(" and mwtm.clientNumber = :clientNumber ");
        }
        if (StringUtils.isNotBlank(apName)) {
            sb.append(" and mwtm.apName = :apName ");
        }
        if (StringUtils.isNotBlank(clientNumber)) {
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

        if (StringUtils.isNotBlank(clientSUPI)) {
            q.setParameter("clientSUPI", clientSUPI);
        }
        if (StringUtils.isNotBlank(startTime)) {
            q.setParameter("startTime", startTime);
        }
        if (StringUtils.isNotBlank(endTime)) {
            q.setParameter("endTime", endTime);
        }
        if (StringUtils.isNotBlank(clientNumber)) {
            q.setParameter("clientNumber", clientNumber);
        }
        if (StringUtils.isNotBlank(apName)) {
            q.setParameter("apName", apName);
        }
        if (StringUtils.isNotBlank(ssid)) {
            q.setParameter("ssid", ssid);
        }

        return (List<ModuleMobileTraceMst>)q.list();
    }

    @Override
    public ModuleMobileTraceMst findModuleMobileTraceMstByUK(String clientSUPI, String startTime) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mwtm ")
          .append(" from ModuleMobileTraceMst mwtm ")
          .append(" where 1=1 ")
          .append(" and mwtm.clientSUPI = :clientSUPI ")
          .append(" and mwtm.startTime = :startTime ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("clientSUPI", clientSUPI);
        q.setParameter("startTime", startTime);
        
        return (ModuleMobileTraceMst)q.uniqueResult();
    }

    @Override
	public List<MobilePollerVO> findModuleMobileTraceMst(MobilePollerVO searchVO, Integer startRow, Integer pageLength) {
    	// 回傳資料的VO容器
    	List<MobilePollerVO> retList = new ArrayList<>();
    	
        StringBuffer sb = new StringBuffer();
        sb.append(" select group_name '1', client_mac '2', start_time '3', end_time '4', client_ip '5', cell_name '6', ssid '7', total_traffic '8', upload_traffic '9', download_traffic '10' ")
        	.append(" from module_mobile_trace_mst mmtm")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            sb.append(" and mmtm.group_id = :queryGroupId ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientSUPI())) {
            sb.append(" and mmtm.client_mac = :queryClientSUPI ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientNumber())) {
            sb.append(" and mmtm.client_ip = :queryClientNumber ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryCellName())) {
            sb.append(" and mmtm.cell_name = :queryCellName ");
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            sb.append(" and mmtm.ssid = :querySsid ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            sb.append(" and mmtm.start_time >= :queryDateTimeBeginStr ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&&StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            sb.append(" and mmtm.start_time <= :queryDateTimeEndStr ");
        }
        
       if (StringUtils.isNotBlank(searchVO.getOrderColumn())) {
           sb.append(" order by ").append(searchVO.getOrderColumn()).append(" ").append(searchVO.getOrderDirection());

       } else {
    	   sb.append(" order by mmtm.start_time, mmtm.client_ip desc ");
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
        if (StringUtils.isNotBlank(searchVO.getQueryClientSUPI())) {
            q.setParameter("queryClientSUPI", searchVO.getQueryClientSUPI());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientNumber())) {
            q.setParameter("queryClientNumber", searchVO.getQueryClientNumber());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryCellName())) {
            q.setParameter("queryCellName", searchVO.getQueryCellName());
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
        	MobilePollerVO retVO = null;
        	for (Object[] data : dataList) {
        		retVO = new MobilePollerVO();
        		
        		String groupName = data[0].toString();
        		String clientSUPI = data[1].toString();
        		
        		String startTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[2]);
        		// endTime is nullable 預設null顯示空字串
        		String endTime = "";
        		if(data[3]!=null)
        			endTime = Constants.FORMAT_YYYYMMDD_HH24MISS.format(data[3]);
        		String clientNumber = data[4].toString();
        		String apName = data[5].toString();
        		String ssid = data[6].toString();
        		String totalTraffic = data[7].toString();
                String uploadTraffic = data[8].toString();
                String downloadTraffic = data[9].toString();
        		
                retVO.setGroupName(groupName);
                retVO.setClientSUPI(clientSUPI);
                retVO.setStartTime(startTime);
                retVO.setEndTime(endTime);
                retVO.setClientNumber(clientNumber);
                retVO.setCellName(apName);
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
	 public long countMobileMstDataFromDB(MobilePollerVO searchVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(data_id) ")
        	.append(" from module_mobile_trace_mst mmtm ")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            sb.append(" and mmtm.group_id = :queryGroupId ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientSUPI())) {
            sb.append(" and mmtm.client_mac = :queryClientSUPI ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientNumber())) {
            sb.append(" and mmtm.client_ip = :queryClientNumber ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryCellName())) {
            sb.append(" and mmtm.cell_name = :queryCellName ");
        }
        if (StringUtils.isNotBlank(searchVO.getQuerySsid())) {
            sb.append(" and mmtm.ssid = :querySsid ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateBegin())&&StringUtils.isNotBlank(searchVO.getQueryTimeBegin()) ) {
            sb.append(" and mmtm.start_time >= :queryDateTimeBeginStr ");
        }
        if (StringUtils.isNotBlank(searchVO.getQueryDateEnd())&&StringUtils.isNotBlank(searchVO.getQueryTimeEnd()) ) {
            sb.append(" and mmtm.start_time <= :queryDateTimeEndStr ");
        }

       sb.append(" order by mmtm.start_time, mmtm.client_ip desc ");

       Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(searchVO.getQueryGroupId())) {
            q.setParameter("queryGroupId", searchVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientSUPI())) {
            q.setParameter("queryClientSUPI", searchVO.getQueryClientSUPI());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryClientNumber())) {
            q.setParameter("queryClientNumber", searchVO.getQueryClientNumber());
        }
        if (StringUtils.isNotBlank(searchVO.getQueryCellName())) {
            q.setParameter("queryCellName", searchVO.getQueryCellName());
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
	public List<MobilePollerDetailVO> findModuleMobileTraceDetail(MobilePollerVO searchVO) {
    	// 回傳資料的VO容器
    	List<MobilePollerDetailVO> retList = new ArrayList<MobilePollerDetailVO>();
    	
        StringBuffer sb = new StringBuffer();
        sb.append(" select detail.client_mac, detail.polling_time, detail.upload_traffic, detail.download_traffic, detail.total_traffic, detail.rssi, detail.noise, detail.snr ")
        	.append(" from module_wifi_trace_detail detail")
        	.append(" where 1=1 ");

        if (StringUtils.isNotBlank(searchVO.getQueryClientSUPI()) ) {
        	sb.append(" and detail.client_mac = :queryClientSUPI ");
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

		 if (StringUtils.isNotBlank(searchVO.getQueryClientSUPI()) ) {
			q.setParameter("queryClientSUPI", searchVO.getQueryClientSUPI());
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
        		MobilePollerDetailVO retVO = new MobilePollerDetailVO();
				
        		String clientSUPI = data[0].toString();
        		String pollingTime = data[1].toString(); //前端moment.js有限定dateString格式要符合,不要亂改啊!!!!
        		String uploadTraffic = data[2].toString();
				String downloadTraffic = data[3].toString();
				String totalTraffic = data[4].toString();
				String rssi = data[5].toString();
				String noise = data[6].toString();
				String snr = data[7].toString();
				
				retVO.setClientSUPI(clientSUPI);
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
