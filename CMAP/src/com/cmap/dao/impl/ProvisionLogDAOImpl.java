package com.cmap.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.ProvisionLogDAO;
import com.cmap.dao.vo.ProvisionLogDAOVO;
import com.cmap.model.ProvisionAccessLog;
import com.cmap.model.ProvisionLogConfigBackupError;
import com.cmap.model.ProvisionLogDetail;
import com.cmap.model.ProvisionLogDevice;
import com.cmap.model.ProvisionLogMaster;
import com.cmap.model.ProvisionLogRetry;
import com.cmap.model.ProvisionLogStep;
import com.cmap.service.vo.VersionServiceVO;

@Repository("provisionLogDAOImpl")
@Transactional
public class ProvisionLogDAOImpl extends BaseDaoHibernate implements ProvisionLogDAO {
	@Log
    private static Logger log;
	
	@Override
	public long countProvisionLogByDAOVO(ProvisionLogDAOVO daovo) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(distinct pld.log_detail_id) ")
		  .append(" from Provision_Log_Master plm ")
		  .append("     ,Provision_Log_Detail pld ")
		  .append("     ,Provision_Log_Step pls ")
		  .append("     ,Provision_Log_Device pldc ")
		  .append("     ,Script_Info si ")
		  .append("     ,Device_List dl ");

		sb.append(" where 1=1 ")
		  .append(" and plm.log_master_id = pld.log_master_id ")
		  .append(" and pld.log_detail_id = pls.log_detail_id ")
		  .append(" and pls.log_step_id = pldc.log_step_id ")
		  .append(" and pls.script_code = si.script_code ")
		  .append(" and pldc.device_list_id = dl.device_list_id ");

		if (StringUtils.isNotBlank(daovo.getQueryGroupId())) {
			sb.append(" and dl.group_Id = :groupId ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryDeviceId())) {
			sb.append(" and dl.device_Id = :deviceId ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeStart())) {
			sb.append(" and plm.begin_time >= DATE_FORMAT(:beginDate, '%Y-%m-%d') ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeEnd())) {
			sb.append(" and plm.begin_time < DATE_ADD(:endDate, INTERVAL 1 DAY) ");
		}

		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       plm.create_by like :searchValue ")
			  .append("       or ")
			  .append("       dl.group_name like :searchValue ")
			  .append("       or ")
			  .append("       dl.device_name like :searchValue ")
			  .append("       or ")
			  .append("       dl.device_model like :searchValue ")
			  .append("       or ")
			  .append("       si.script_name like :searchValue ")
			  .append("       or ")
			  .append("       plm.reason like :searchValue ")
			  .append("     ) ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createNativeQuery(sb.toString());

		if (StringUtils.isNotBlank(daovo.getQueryGroupId())) {
			q.setParameter("groupId", daovo.getQueryGroupId());
		}
		if (StringUtils.isNotBlank(daovo.getQueryDeviceId())) {
			q.setParameter("deviceId", daovo.getQueryDeviceId());
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeStart())) {
			q.setParameter("beginDate", daovo.getQueryBeginTimeStart());
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeEnd())) {
			q.setParameter("endDate", daovo.getQueryBeginTimeEnd());
		}
		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(daovo.getSearchValue()).concat("%"));
	    }

		return DataAccessUtils.longResult(q.list());
	}

	@Override
	public List<Object[]> findProvisionLogByDAOVO(ProvisionLogDAOVO daovo, Integer startRow, Integer pageLength) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select distinct")
		  .append("   plm.log_master_id, pld.log_detail_id, pls.log_step_id, pldc.log_device_id, ") //index: 0~3
		  .append("   pld.begin_time, ")		//index: 4
		  .append("   pld.create_by, ")			//index: 5
		  .append("   dl.group_name, ")			//index: 6
		  .append("   dl.device_name, ")		//index: 7
		  .append("   dl.device_model, ")		//index: 8
		  .append("   si.script_name, ")		//index: 9
		  .append("   if(pld.create_by='"+Constants.SYSLOG_BK_TRIGGER_NAME+"', CONCAT('Configured by [', pld.user_name, '] on ', pld.user_ip), plm.reason), ")	//index: 10
		  .append("   pls.result, ")			//index: 11
		  .append("   pls.process_log ")		//index: 12
		  .append(" from Provision_Log_Master plm ")
		  .append("     ,Provision_Log_Detail pld ")
		  .append("     ,Provision_Log_Step pls ")
		  .append("     ,Provision_Log_Device pldc ")
		  .append("     ,Script_Info si ")
		  .append("     ,Device_List dl ");
		sb.append(" where 1=1 ")
		  .append(" and plm.log_master_id = pld.log_master_id ")
		  .append(" and pld.log_detail_id = pls.log_detail_id ")
		  .append(" and pls.log_step_id = pldc.log_step_id ")
		  .append(" and pls.script_code = si.script_code ")
		  .append(" and pldc.device_list_id = dl.device_list_id ");

		if (StringUtils.isNotBlank(daovo.getQueryGroupId())) {
			sb.append(" and dl.group_Id = :groupId ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryDeviceId())) {
			sb.append(" and dl.device_Id = :deviceId ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeStart())) {
			sb.append(" and plm.begin_time >= DATE_FORMAT(:beginDate, '%Y-%m-%d') ");
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeEnd())) {
			sb.append(" and plm.begin_time < DATE_ADD(:endDate, INTERVAL 1 DAY) ");
		}

		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       plm.create_by like :searchValue ")
			  .append("       or ")
			  .append("       dl.group_name like :searchValue ")
			  .append("       or ")
			  .append("       dl.device_name like :searchValue ")
			  .append("       or ")
			  .append("       dl.device_model like :searchValue ")
			  .append("       or ")
			  .append("       si.script_name like :searchValue ")
			  .append("       or ")
			  .append("       plm.reason like :searchValue ")
			  .append("     ) ");
		}

		if (StringUtils.isNotBlank(daovo.getOrderColumn())) {
			sb.append(" order by ").append(daovo.getOrderColumn()).append(" ").append(daovo.getOrderDirection());

		} else {
			sb.append(" order by plm.begin_time desc, dl.group_name asc, dl.device_name asc ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createNativeQuery(sb.toString());

		if (StringUtils.isNotBlank(daovo.getQueryGroupId())) {
			q.setParameter("groupId", daovo.getQueryGroupId());
		}
		if (StringUtils.isNotBlank(daovo.getQueryDeviceId())) {
			q.setParameter("deviceId", daovo.getQueryDeviceId());
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeStart())) {
			q.setParameter("beginDate", daovo.getQueryBeginTimeStart());
		}
		if (StringUtils.isNotBlank(daovo.getQueryBeginTimeEnd())) {
			q.setParameter("endDate", daovo.getQueryBeginTimeEnd());
		}
		if (StringUtils.isNotBlank(daovo.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(daovo.getSearchValue()).concat("%"));
	    }
		if (startRow != null && pageLength != null) {
	    	q.setFirstResult(startRow);
		    q.setMaxResults(pageLength);
	    }

		return (List<Object[]>)q.list();
	}

	@Override
	public void insertProvisionLog(ProvisionLogMaster master, List<ProvisionLogDetail> details, List<ProvisionLogStep> steps,
			List<ProvisionLogDevice> devices, List<ProvisionLogRetry> retrys, List<ProvisionLogConfigBackupError> errors) {

		if (master != null) {
			getHibernateTemplate().save(master);
		}

		if (details != null) {
			for (ProvisionLogDetail detail : details) {
				getHibernateTemplate().save(detail);
			}
		}

		if (steps != null) {
			for (ProvisionLogStep step : steps) {
				getHibernateTemplate().save(step);
			}
		}

		if (devices != null) {
			for (ProvisionLogDevice device : devices) {
				getHibernateTemplate().save(device);
			}
		}

		if (retrys != null) {
			for (ProvisionLogRetry retry : retrys) {
				getHibernateTemplate().save(retry);
			}
		}
		
		if (errors != null) {
			for (ProvisionLogConfigBackupError error : errors) {
				getHibernateTemplate().save(error);
			}
		}
	}

	@Override
	public ProvisionAccessLog findProvisionAccessLogById(String logId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ProvisionAccessLog ")
		  .append(" where 1=1 ")
		  .append(" and logId = :logId ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());
		q.setParameter("logId", logId);

		List<ProvisionAccessLog> entities = (List<ProvisionAccessLog>)q.list();
		return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
	}

	@Override
	public ProvisionLogStep findProvisionLogStepById(String logStepId) {
		return getHibernateTemplate().getSessionFactory().getCurrentSession().get(ProvisionLogStep.class, logStepId);
	}
	
	@Override
	public List<ProvisionLogConfigBackupError> findProvisionLogConfigBackupErrorByDAOVO(VersionServiceVO vsVO) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ProvisionLogConfigBackupError ")
		  .append(" where 1=1 ");
		
		if (StringUtils.isNotBlank(vsVO.getQueryDevice())) {
          sb.append(" and deviceId = :deviceId ");
        } else if (vsVO.getQueryDeviceList() != null && !vsVO.getQueryDeviceList().isEmpty()) {
          sb.append(" and deviceId in (:deviceId) ");
        }
	
		if (StringUtils.isNotBlank(vsVO.getQueryDateBegin1())) {
			sb.append("	and createTime >= DATE_FORMAT(:beginDate_1, '%Y-%m-%d') ");
		}
		if (StringUtils.isNotBlank(vsVO.getQueryDateEnd1())) {
			sb.append("	and createTime < DATE_FORMAT(:endDate_1, '%Y-%m-%d %H:%i:%s') ");
		}


		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query<?> q = session.createQuery(sb.toString());

		if (StringUtils.isNotBlank(vsVO.getQueryDevice())) {
            q.setParameter("deviceId", vsVO.getQueryDevice());
        } else if (vsVO.getQueryDeviceList() != null && !vsVO.getQueryDeviceList().isEmpty()) {
            q.setParameterList("deviceId", vsVO.getQueryDeviceList());
        }
		if (StringUtils.isNotBlank(vsVO.getQueryDateBegin1())) {
	    	q.setParameter("beginDate_1", vsVO.getQueryDateBegin1());
	    }
	    if (StringUtils.isNotBlank(vsVO.getQueryDateEnd1())) {
	    	q.setParameter("endDate_1", vsVO.getQueryDateEnd1());
	    }
	    
	    List<ProvisionLogConfigBackupError> result = (List<ProvisionLogConfigBackupError>)q.list();
	    
	    if(result == null || result.size() == 0) {
	    	log.error("query empty with SQL = " + sb.toString());
	    }
		return result;
	}
}
