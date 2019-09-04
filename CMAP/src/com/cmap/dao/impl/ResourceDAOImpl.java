package com.cmap.dao.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.dao.ResourceDAO;
import com.cmap.model.ResourceInfo;

@Repository
@Transactional
public class ResourceDAOImpl extends BaseDaoHibernate implements ResourceDAO {

    @Override
    public ResourceInfo getResourceInfoById(String id) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from ResourceInfo ri ")
          .append(" where 1=1 ")
          .append(" and ri.id = :id ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("id", id);

        return (ResourceInfo)q.uniqueResult();
    }

}
