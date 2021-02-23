package com.cmap.plugin.module.netflow.statistics;

import java.text.ParseException;
import java.util.Date;
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

@Repository("netFlowStatisticsDAO")
@Transactional
public class NetFlowStatisticsDAOImpl extends BaseDaoHibernate implements NetFlowStatisticsDAO {
    @Log
    private static Logger log;

    @Override
    public List<ModuleIpTrafficStatistics> findModuleIpStatistics(String groupId, String statDate, String ipAddress) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mits ")
          .append(" from ModuleIpTrafficStatistics mits ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(groupId)) {
            sb.append(" and mits.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(statDate)) {
            sb.append(" and mits.statDate = :statDate ");
        }
        if (StringUtils.isNotBlank(ipAddress)) {
            sb.append(" and mits.ipAddress = :ipAddress ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(groupId)) {
            q.setParameter("groupId", groupId);
        }
        if (StringUtils.isNotBlank(statDate)) {
            q.setParameter("statDate", statDate);
        }
        if (StringUtils.isNotBlank(ipAddress)) {
            q.setParameter("ipAddress", ipAddress);
        }

        return (List<ModuleIpTrafficStatistics>)q.list();
    }

    @Override
    public ModuleIpTrafficStatistics findModuleIpStatisticsByUK(String groupId, Date statDate, String ipAddress) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mits ")
          .append(" from ModuleIpTrafficStatistics mits ")
          .append(" where 1=1 ")
          .append(" and mits.groupId = :groupId ")
          .append(" and mits.statDate = :statDate ")
          .append(" and mits.ipAddress = :ipAddress ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("groupId", groupId);
        q.setParameter("statDate", statDate);
        q.setParameter("ipAddress", ipAddress);

        return (ModuleIpTrafficStatistics)q.uniqueResult();
    }

    @Override
    public void insertModuleIpStatistics(List<ModuleIpTrafficStatistics> entities) {
        for (ModuleIpTrafficStatistics entity : entities) {
            try {
                getHibernateTemplate().getSessionFactory().getCurrentSession().save(entity);

            } catch (Exception e) {
                /*
                 * Y191114, Ken
                 * 其中更新失敗的不處理，繼續處理其他筆資料
                 */
                log.error(e.toString(), e);
            }
        }
        //insertEntities(BaseDAO.TARGET_PRIMARY_DB, entities);
    }

    @Override
    public void updateModuleIpStatistics(List<ModuleIpTrafficStatistics> entities) {
        for (ModuleIpTrafficStatistics entity : entities) {
            try {
                getHibernateTemplate().getSessionFactory().getCurrentSession().update(entity);

            } catch (Exception e) {
                /*
                 * Y191114, Ken
                 * 其中更新失敗的不處理，繼續處理其他筆資料
                 */
                log.error(e.toString(), e);
            }
        }
        /*
         * Y191114, Ken
         * 原方法會發生Transaction lock timeout問題
         * 推測應為先前流程[Query此筆資料]與此處[Update資料]調用的session不同
         * 導致[update-session]在wait[query-session]釋放lock而timeout
         * 暫時改為不調用共用method
         */
    	//updateEntities(BaseDAO.TARGET_PRIMARY_DB, entities);
    }

    @Override
    public long countModuleIpStatisticsRanking(NetFlowStatisticsVO nfsVO) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(" select count(0) ")
              .append(" from ( ")
              .append("   select 1 ")
              .append("   from Module_Ip_Traffic_Statistics mits ")
              .append("   left join Module_Ip_Data_Setting mids ")
              .append("   on ( mits.group_id = mids.group_id ")
              .append("        and mits.ip_address = mids.ip_addr ) ")
              .append("   where 1=1 ");

            if (StringUtils.isNotBlank(nfsVO.getQueryGroupId())) {
                sb.append(" and mits.group_Id = :groupId ");
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateBegin())) {
                sb.append(" and mits.stat_Date >= :queryDateBegin ");
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateEnd())) {
                sb.append(" and mits.stat_Date <= :queryDateEnd ");
            }
            if (StringUtils.isNotBlank(nfsVO.getSearchValue())) {
                sb.append(" and ( mits.ip_Address like :searchValue ")
                  .append("       or ")
                  .append("       mids.ip_desc like :searchValue ")
                  .append(" ) ");
            }
            sb.append("   group by mits.group_id, mits.ip_address ")
              .append(" ) subQuery ");

            Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

            if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
                session.beginTransaction();
            }

            Query<?> q = session.createNativeQuery(sb.toString());
            if (StringUtils.isNotBlank(nfsVO.getQueryGroupId())) {
                q.setParameter("groupId", nfsVO.getQueryGroupId());
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateBegin())) {
                q.setParameter("queryDateBegin", Constants.FORMAT_YYYY_MM_DD.parse(nfsVO.getQueryDateBegin()));
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateEnd())) {
                q.setParameter("queryDateEnd", Constants.FORMAT_YYYY_MM_DD.parse(nfsVO.getQueryDateEnd()));
            }
            if (StringUtils.isNotBlank(nfsVO.getSearchValue())) {
                q.setParameter("searchValue", "%".concat(nfsVO.getSearchValue()).concat("%"));
            }
            return DataAccessUtils.longResult(q.list());

        } catch (ParseException pe) {
            log.error(pe.toString(), pe);
            return 0;
        }
    }

    @Override
    public List<Object[]> findModuleIpStatisticsRanking(NetFlowStatisticsVO nfsVO, Integer startRow, Integer pageLength) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(" select mits1.ip_address ")
              .append("       ,mits1.group_id ")
              .append("       ,(sum(mits1.total_traffic) / mits2.sum_total * 100) as percent ")
              .append("       ,sum(mits1.total_traffic) as ttl_traffic")
              .append("       ,sum(mits1.upload_traffic) as ttl_upload_traffic ")
              .append("       ,sum(mits1.download_traffic) as ttl_download_traffic ")
              .append("       ,sum(mits1.session_num) as ttl_session_num ")
              .append("       ,mits2.sum_total as ttl_totalTraffic ")
              .append("       ,mits2.sum_upload as ttl_uploadTraffic ")
              .append("       ,mits2.sum_download as ttl_downloadTraffic ")
              .append("       ,mits2.sum_session as ttl_session ")
              .append("       ,mids.ip_desc ")
              .append(" from Module_Ip_Traffic_Statistics as mits1 ")
              .append("      left join Module_Ip_Data_Setting mids ")
              .append("      on ( mits1.group_id = mids.group_id ")
              .append("           and mits1.ip_address = mids.ip_addr ) ")
              .append("     ,(select sum(mits.total_traffic) as sum_total ")
              .append("             ,sum(mits.upload_traffic) as sum_upload ")
              .append("             ,sum(mits.download_traffic) as sum_download ")
              .append("             ,sum(mits.session_num) as sum_session ")
              .append("       from Module_Ip_Traffic_Statistics as mits ")
              .append("       where 1=1 ");

              if (StringUtils.isNotBlank(nfsVO.getQueryGroupId())) {
                  sb.append(" and mits.group_id = :groupId ");
              }
              if (StringUtils.isNotBlank(nfsVO.getQueryDateBegin())) {
                  sb.append(" and mits.stat_date >= :queryDateBegin ");
              }
              if (StringUtils.isNotBlank(nfsVO.getQueryDateEnd())) {
                  sb.append(" and mits.stat_date <= :queryDateEnd ");
              }
              if (StringUtils.isNotBlank(nfsVO.getSearchValue())) {
                  sb.append(" and mits.ip_address like :searchValue ");
              }
            sb.append("      ) as mits2 ")
              .append(" where 1=1 ");

            if (StringUtils.isNotBlank(nfsVO.getQueryGroupId())) {
                sb.append(" and mits1.group_id = :groupId ");
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateBegin())) {
                sb.append(" and mits1.stat_date >= :queryDateBegin ");
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateEnd())) {
                sb.append(" and mits1.stat_date <= :queryDateEnd ");
            }
            if (StringUtils.isNotBlank(nfsVO.getSearchValue())) {
                sb.append(" and ( mits1.ip_address like :searchValue ")
                  .append("       or ")
                  .append("       mids.ip_desc like :searchValue ")
                  .append(" ) ");
            }
            sb.append(" group by mits1.ip_address, mits1.group_id, mids.ip_desc ");

            if (StringUtils.isNotBlank(nfsVO.getOrderColumn())) {
                sb.append(" order by ").append(nfsVO.getOrderColumn()).append(" ").append(nfsVO.getOrderDirection());

            } else {
                sb.append(" order by mits1.total_traffic desc ");
            }

            Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

            if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
                session.beginTransaction();
            }

            Query<?> q = session.createNativeQuery(sb.toString());
            if (StringUtils.isNotBlank(nfsVO.getQueryGroupId())) {
                q.setParameter("groupId", nfsVO.getQueryGroupId());
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateBegin())) {
                q.setParameter("queryDateBegin", Constants.FORMAT_YYYY_MM_DD.parse(nfsVO.getQueryDateBegin()));
            }
            if (StringUtils.isNotBlank(nfsVO.getQueryDateEnd())) {
                q.setParameter("queryDateEnd", Constants.FORMAT_YYYY_MM_DD.parse(nfsVO.getQueryDateEnd()));
            }
            if (StringUtils.isNotBlank(nfsVO.getSearchValue())) {
                q.setParameter("searchValue", "%".concat(nfsVO.getSearchValue()).concat("%"));
            }
            if (startRow != null && pageLength != null) {
                q.setFirstResult(startRow);
                q.setMaxResults(pageLength);
            }
            return (List<Object[]>)q.list();

        } catch (ParseException pe) {
            log.error(pe.toString(), pe);
            return null;
        }
    }
    
    @Override
	public List<Object[]> findModuleIpStatisticsRankingOverLimit(String nowDateStr, String limitSize,
			String limitColumn, List<String> deviceModel) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(" select mits.ip_address ")
              .append("       ,mits.group_id ")
              .append("       ,dev.device_id ")
              .append("       ,mits.total_traffic ")
              .append("       ,mits.upload_traffic ")
              .append("       ,mits.download_traffic ")
              .append(" from Module_Ip_Traffic_Statistics as mits ");

            if (deviceModel != null && deviceModel.size() != 0) {
				sb.append("		left join device_list dev ")
				  .append("		on mits.group_id=dev.group_id and dev.device_model in ( :deviceModel )");
			}
			sb.append(" where 1=1 ");
            sb.append(" and mits.stat_date = :nowDateStr ");
            sb.append(" and NOT EXISTS ( ");
            sb.append("       SELECT mbil.ip_address");
            sb.append("       FROM Module_Blocked_Ip_List mbil ");
            sb.append("       WHERE mbil.ip_address=mits.ip_address");
            sb.append("       and mbil.status_flag='B')");
            
			switch (StringUtils.lowerCase(limitColumn)) {

			case "total_traffic":
			case "total":
				sb.append(" and mits.total_traffic >= :limitSize ");
				break;
				
			case "upload_traffic":
			case "upload":
				sb.append(" and mits.upload_traffic >= :limitSize ");
				break;
				
			case "download_traffic":
			case "download":
				sb.append(" and mits.download_traffic >= :limitSize ");
				break;
				
			default:
				sb.append(" and ( mits.total_traffic >= :limitSize ");
				sb.append(" or  mits.upload_traffic >= :limitSize ");
				sb.append(" or  mits.download_traffic >= :limitSize )");
				break;
			}
            
			 Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

            if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
                session.beginTransaction();
            }

            Query<?> q = session.createNativeQuery(sb.toString());
            if (StringUtils.isNotBlank(nowDateStr)) {
                q.setParameter("nowDateStr", nowDateStr);
            }
            if (StringUtils.isNotBlank(limitSize)) {
                q.setParameter("limitSize", limitSize);
            }
            if (deviceModel != null && deviceModel.size() != 0) {
                q.setParameter("deviceModel", deviceModel);
            }
            return (List<Object[]>)q.list();

        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }
    }
}
