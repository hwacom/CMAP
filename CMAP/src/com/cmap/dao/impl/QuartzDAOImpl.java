package com.cmap.dao.impl;

import java.util.List;

import javax.persistence.TransactionRequiredException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.QuartzDAO;
import com.cmap.dao.vo.QuartzDAOVO;

@Repository("quartzDAOImpl")
@Transactional
public class QuartzDAOImpl extends BaseDaoHibernate implements QuartzDAO {
	@Log
    private static Logger log;
	
	@Autowired
    @Qualifier("quartzSessionFactory")
    private SessionFactory quartzSessionFactory;
	
	Session session = null;
    Transaction tx = null;
    
	private void checkSession() {
        try {
            session = quartzSessionFactory.openSession();
        } catch (HibernateException e) {
            session = quartzSessionFactory.openSession();
        } finally {
            if (session != null) {
                if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
                    tx = session.beginTransaction();
                } else {
                    tx = session.getTransaction();
                }
            }
        }
	}
	
	@Override
	public long countQuartzDataByDAOVO(QuartzDAOVO daoVO) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(*) ")
		  .append(" from QrtzTriggers qt, QrtzCronTriggers qct ")
		  .append(" where 1=1 ")
		  .append(" and qt.schedName = qct.schedName ")
		  .append(" and qt.triggerName = qct.triggerName ")
		  .append(" and qt.triggerGroup = qct.triggerGroup ");
		
		try {
			checkSession();
			
			Query<?> q = session.createQuery(sb.toString());
			
			return DataAccessUtils.longResult(q.list());
        } catch (TransactionRequiredException tre) {
            log.error(tre.toString());

        } catch (Exception e) {
            log.error(e.toString(), e);

            if (tx != null) {
                tx.rollback();
                session.close();
            }

        } finally {
            if (session != null) {
                session.close();
            }
        }
        
		return 0;
	}
	
	@Override
	public List<Object[]> findQuartzDataByDAOVO(QuartzDAOVO daoVO) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(" select qt, qct, qjd")
		  .append(" from QrtzTriggers qt, QrtzCronTriggers qct, QrtzJobDetails qjd ")
		  .append(" where 1=1 ")
		  .append(" and qt.schedName = qct.schedName ")
		  .append(" and qt.triggerName = qct.triggerName ")
		  .append(" and qt.triggerGroup = qct.triggerGroup ")
		  .append(" and qt.schedName = qjd.schedName ")
		  .append(" and qt.jobName = qjd.jobName ")
		  .append(" and qt.jobGroup = qjd.jobGroup ");
		
		if (daoVO != null && StringUtils.isNotBlank(daoVO.getJobKeyName())) {
			sb.append(" and qt.jobName = :jobName ");
		}
		if (daoVO != null && StringUtils.isNotBlank(daoVO.getJobKeyGroup())) {
			sb.append(" and qt.jobGroup = :jobGroup ");
		}
		if (daoVO != null && StringUtils.isNotBlank(daoVO.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       qt.jobName like :searchValue ")
			  .append("       or ")
			  .append("       qt.jobGroup like :searchValue ")
			  .append("       or ")
			  .append("       qt.priority like :searchValue ")
			  .append("       or ")
			  .append("       qt.triggerState like :searchValue ")
			  .append("       or ")
			  .append("       qct.cronExpression like :searchValue ")
			  .append("       or ")
			  .append("       qct.timeZoneId like :searchValue ")
			  .append("       or ")
			  .append("       qjd.jobClassName like :searchValue ")
			  .append("       or ")
			  .append("       qjd.description like :searchValue ")
			  .append("     ) ");
		}
		if (StringUtils.isNotBlank(daoVO.getOrderColumn())) {
			sb.append(" order by ").append(daoVO.getOrderColumn()).append(" ").append(daoVO.getOrderDirection());
			
		} else {
			sb.append(" order by qt.prevFireTime desc ");
		}
		
		try {
			checkSession();
			
			Query<?> q  = session.createQuery(sb.toString());
		    
		    if (daoVO != null && StringUtils.isNotBlank(daoVO.getJobKeyName())) {
				q.setParameter("jobName", daoVO.getJobKeyName());
			}
			if (daoVO != null && StringUtils.isNotBlank(daoVO.getJobKeyGroup())) {
				q.setParameter("jobGroup", daoVO.getJobKeyGroup());
			}
			if (daoVO != null && StringUtils.isNotBlank(daoVO.getSearchValue())) {
				q.setParameter("searchValue", "%".concat(daoVO.getSearchValue()).concat("%"));
			}
			
			return (List<Object[]>)q.list();
			
        } catch (TransactionRequiredException tre) {
            log.error(tre.toString());

        } catch (Exception e) {
            log.error(e.toString(), e);

            if (tx != null) {
                tx.rollback();
                session.close();
            }

        } finally {
            if (session != null) {
                session.close();
            }
        }
	    
		return null;
	}
}
