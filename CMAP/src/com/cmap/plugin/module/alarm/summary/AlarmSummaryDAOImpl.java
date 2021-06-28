package com.cmap.plugin.module.alarm.summary;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;

@Repository("alarmSummaryDAO")
@Transactional
public class AlarmSummaryDAOImpl extends BaseDaoHibernate implements AlarmSummaryDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleAlarmSummary(AlarmSummaryVO tlVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mas.alarm_id) ")
          .append(" from module_alarm_summary mas ")
          .append(" where 1=1 ");


        if (StringUtils.isNotBlank(tlVO.getQuerySensorType())) {
			sb.append(" and mas.SENSOR_TYPE = :sensorType ");
		}
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mas.alarm_status = :status ");
		}
		
        if (!tlVO.getQueryDataStatus().isEmpty() && tlVO.getQueryDataStatus() != null) {
			sb.append(" and mas.ALARM_DATA_STATUS in (:dataStatus) ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
			sb.append(" and mas.message = :message ");
		}
				
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mas.ALARM_TIME >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mas.ALARM_TIME < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
		if (StringUtils.isNotBlank(tlVO.getOrderColumn())) {
			sb.append(" order by ").append(tlVO.getOrderColumn()).append(" ").append(tlVO.getOrderDirection());
		} else {
			sb.append(" order by mas.ALARM_TIME desc ");
		}
		
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());
        
        if (StringUtils.isNotBlank(tlVO.getQuerySensorType())) {
            q.setParameter("sensorType", tlVO.getQuerySensorType());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (!tlVO.getQueryDataStatus().isEmpty() && tlVO.getQueryDataStatus() != null) {
            q.setParameterList("dataStatus", tlVO.getQueryDataStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
            q.setParameter("message", tlVO.getQueryMessage());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }
        
        if (tlVO.getStartNum() != null && tlVO.getPageLength() != null) {
            q.setFirstResult(tlVO.getStartNum());
            q.setMaxResults(tlVO.getPageLength());
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findModuleAlarmSummary(AlarmSummaryVO tlVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select SENSOR_NAME '0', SENSOR_TYPE '1', GROUP_NAME '2', DEVICE_NAME '3', ALARM_STATUS '4', ALARM_TIME '5', CLOSE_TIME '6', LAST_VALUE '7', MESSAGE '8', PRIORITY '9', REMARK '10', ALARM_ID '11', GROUP_ID '12', DEVICE_ID '13', SENSOR_ID '14', ALARM_DATA_STATUS '15', update_time '16', update_by '17' ")
          .append(" from module_alarm_summary mas ")
          .append(" where 1=1 ");
        
        if (StringUtils.isNotBlank(tlVO.getQuerySensorType())) {
			sb.append(" and mas.SENSOR_TYPE = :sensorType ");
		}
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mas.alarm_status = :status ");
		}
		
        if (!tlVO.getQueryDataStatus().isEmpty() && tlVO.getQueryDataStatus() != null) {
			sb.append(" and mas.ALARM_DATA_STATUS in (:dataStatus) ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
			sb.append(" and mas.message = :message ");
		}
				
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mas.ALARM_TIME >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mas.ALARM_TIME < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
		if (StringUtils.isNotBlank(tlVO.getOrderColumn())) {
			sb.append(" order by ").append(tlVO.getOrderColumn()).append(" ").append(tlVO.getOrderDirection());
		} else {
			sb.append(" order by mas.ALARM_TIME desc ");
		}
		
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());
        
        if (StringUtils.isNotBlank(tlVO.getQuerySensorType())) {
            q.setParameter("sensorType", tlVO.getQuerySensorType());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (!tlVO.getQueryDataStatus().isEmpty() && tlVO.getQueryDataStatus() != null) {
            q.setParameterList("dataStatus", tlVO.getQueryDataStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
            q.setParameter("message", tlVO.getQueryMessage());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }
        
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }
        
        return (List<Object[]>)q.list();
    }

    @Override
    public List<ModuleAlarmSummary> findModuleAlarmSummary(AlarmSummaryVO tlVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mas ")
          .append(" from ModuleAlarmSummary mas ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mas.alarmDataStatus = :status ");
		}
		
		if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
			sb.append(" and mas.message = :message ");
		}
				
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mas.alarmTime >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mas.alarmTime < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
            q.setParameter("message", tlVO.getQueryMessage());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }

        return (List<ModuleAlarmSummary>)q.list();
    }
    
    @Override
    public ModuleAlarmSummary findModuleAlarmSummaryByVO(AlarmSummaryVO tlVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mas ")
          .append(" from ModuleAlarmSummary mas ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mas.alarmDataStatus = :status ");
		}
		
		if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
			sb.append(" and mas.message = :message ");
		}
				
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mas.alarmTime >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mas.alarmTime < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryMessage())) {
            q.setParameter("message", tlVO.getQueryMessage());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }
        
        return (ModuleAlarmSummary) q.setMaxResults(1).uniqueResult();
    }
    
    @Override
    public ModuleAlarmSummary findModuleAlarmSummaryByPK(Long alarmId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mas ")
          .append(" from ModuleAlarmSummary mas ")
          .append(" where 1=1 ");

        if (alarmId != null) {
			sb.append(" and mas.alarmId = :alarmId ");
		}
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (alarmId != null) {
            q.setParameter("alarmId", alarmId);
        }
        
        return (ModuleAlarmSummary) q.setMaxResults(1).uniqueResult();
    }
    
    @Override
    public List<ModuleAlarmSummaryLog> findModuleAlarmSummaryLogByAlarmId(Long alarmId) {
    	StringBuffer sb = new StringBuffer();
        sb.append(" select alog ")
          .append(" from ModuleAlarmSummaryLog alog ")
          .append(" where 1=1 ")
		  .append(" and alog.alarmId = :alarmId ")
          .append(" order by alog.createTime desc ");
          
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("alarmId", alarmId);
        
        return (List<ModuleAlarmSummaryLog>) q.list();
    }
    
    @Override
	public void saveOrUpdateAlarmSummary(List<Object> entityList) {
		for (Object entity : entityList) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
	}
	
	@Override
	public void deleteAlarmSummary(List<Object> entities) {
		getHibernateTemplate().deleteAll(entities);	
	}

}
