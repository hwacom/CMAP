package com.cmap.plugin.module.tickets;

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

@Repository("ticketListDAO")
@Transactional
public class TicketListDAOImpl extends BaseDaoHibernate implements TicketListDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleTicketList(TicketListVO tlVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mtl.list_Id) ")
          .append(" from module_ticket_list mtl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(tlVO.getQueryOwnerType())) {
			sb.append(" and mtl.owner_type = :ownerType ");
		}
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
			sb.append(" and mtl.owner = :owner ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mtl.status = :status ");
		}
		
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mtl.update_time >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mtl.update_time < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
		
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwnerType())) {
            q.setParameter("ownerType", tlVO.getQueryOwnerType());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
            q.setParameter("owner", tlVO.getQueryOwner());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findModuleTicketList(TicketListVO tlVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select  UPDATE_TIME '0', PRIORITY '1', LIST_ID '2', SUBJECT '3', OWNER '4', STATUS '5', REMARK '6', CREATE_TIME '7', CREATE_BY '8', UPDATE_BY '9', OWNER_TYPE '10', MAIL_FLAG '11', EXEC_FLAG '12' ")
          .append(" from module_ticket_list mtl ")
          .append(" where 1=1 ");
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwnerType())) {
			sb.append(" and mtl.owner_type = :ownerType ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
			sb.append(" and mtl.owner = :owner ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mtl.status = :status ");
		}
		
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mtl.update_time >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mtl.update_time < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
		if (StringUtils.isNotBlank(tlVO.getOrderColumn())) {
			sb.append(" order by ").append(tlVO.getOrderColumn()).append(" ").append(tlVO.getOrderDirection());
		} else {
			sb.append(" order by mtl.UPDATE_TIME, mtl.OWNER desc ");
		}
		
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwnerType())) {
            q.setParameter("ownerType", tlVO.getQueryOwnerType());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
            q.setParameter("owner", tlVO.getQueryOwner());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
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
    public List<ModuleTicketList> findModuleTicketList(TicketListVO tlVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mtl ")
          .append(" from ModuleTicketList mtl ")
          .append(" where 1=1 ");
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
			sb.append(" and mtl.owner = :owner ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mtl.status = :status ");
		}
		
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mtl.updateTime >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mtl.updateTime < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
            q.setParameter("owner", tlVO.getQueryOwner());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }

        return (List<ModuleTicketList>)q.list();
    }
    
    @Override
    public ModuleTicketList findModuleTicketListByVO(TicketListVO tlVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mtl ")
          .append(" from ModuleTicketList mtl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(tlVO.getQueryListId())) {
			sb.append(" and mtl.listId = :listId ");
		}
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
			sb.append(" and mtl.owner = :owner ");
		}
        
		if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
			sb.append(" and mtl.status = :status ");
		}
		
		// 範圍查詢會中止Index左前綴結合需放在最後面接合,否則會影響查詢效能
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
        	sb.append(" and (mtl.updateTime >= DATE_FORMAT(:queryDateTimeBeginStr, '%Y-%m-%d %H:%i')  and mtl.updateTime < DATE_FORMAT(:queryDateTimeEndStr, '%Y-%m-%d %H:%i')) ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(tlVO.getQueryListId())) {
            q.setParameter("listId", tlVO.getQueryListId());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryOwner())) {
            q.setParameter("owner", tlVO.getQueryOwner());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryStatus())) {
            q.setParameter("status", tlVO.getQueryStatus());
        }
        
        if (StringUtils.isNotBlank(tlVO.getQueryDateBegin()) && StringUtils.isNotBlank(tlVO.getQueryDateEnd()) && StringUtils.isNotBlank(tlVO.getQueryTimeBegin()) && StringUtils.isNotBlank(tlVO.getQueryTimeEnd())) {
            q.setParameter("queryDateTimeBeginStr", tlVO.getQueryDateBegin().concat(" ").concat(tlVO.getQueryTimeBegin()));
            q.setParameter("queryDateTimeEndStr", tlVO.getQueryDateEnd().concat(" ").concat(tlVO.getQueryTimeEnd()));
        }
        
        List<ModuleTicketList> result = (List<ModuleTicketList>) q.list();
        
        return result != null && result.size() > 0 ? result.get(0) : null;
    }
    
    @Override
    public ModuleTicketList findModuleTicketListByPK(Long listId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mtl ")
          .append(" from ModuleTicketList mtl ")
          .append(" where 1=1 ");

        if (listId != null) {
			sb.append(" and mtl.listId = :listId ");
		}
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        if (listId != null) {
            q.setParameter("listId", listId);
        }
        
        ModuleTicketList result = (ModuleTicketList) q.uniqueResult();
        
        return result != null ? result : null;
    }
    
    @Override
    public List<ModuleTicketDetail> findModuleTicketDetailByListId(Long listId) {
    	StringBuffer sb = new StringBuffer();
        sb.append(" select mtd ")
          .append(" from ModuleTicketDetail mtd ")
          .append(" where 1=1 ")
		  .append(" and mtd.listId = :listId ")
          .append(" order by mtd.createTime desc ");
          
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("listId", listId);
        
        return (List<ModuleTicketDetail>) q.list();
    }
    
    @Override
	public void saveOrUpdateTicketList(Object entity) {
    	getHibernateTemplate().saveOrUpdate(entity);
	}
	
	@Override
	public void deleteTicketList(List<Object> entities) {
		getHibernateTemplate().deleteAll(entities);	
	}

}
