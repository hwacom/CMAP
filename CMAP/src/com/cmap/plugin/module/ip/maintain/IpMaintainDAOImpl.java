package com.cmap.plugin.module.ip.maintain;

import java.util.List;
import javax.annotation.Resource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;
import com.nimbusds.oauth2.sdk.util.StringUtils;

@Repository("ipMaintainDAO")
@Transactional
public class IpMaintainDAOImpl extends BaseDaoHibernate implements IpMaintainDAO {
    @Log
    private static Logger log;

    @Resource(name = "primarySessionFactory")
    private SessionFactory primarySessionFactory;

    @Resource(name = "secondSessionFactory")
    private SessionFactory secondSessionFactory;

    @Override
    public long countModuleIpDataSetting(IpMaintainServiceVO imsVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ")
          .append("   count(distinct mids.settingId) ")
          .append(" FROM ModuleIpDataSetting mids ")
          .append("     ,DeviceList dl ")
          .append(" WHERE 1=1 ")
          .append(" AND mids.groupId = dl.groupId ");

        if (StringUtils.isNotBlank(imsVO.getQueryGroup())) {
            sb.append(" AND mids.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(imsVO.getQueryIp())) {
            sb.append(" AND mids.ipAddr = :ipAddr ");
        }
        if (StringUtils.isNotBlank(imsVO.getQueryMac())) {
            sb.append(" AND mids.macAddr = :macAddr ");
        }
        if (StringUtils.isNotBlank(imsVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.groupName like :searchValue ")
              .append("       or ")
              .append("       mids.ipAddr like :searchValue ")
              .append("       or ")
              .append("       mids.macAddr like :searchValue ")
              .append("       or ")
              .append("       mids.ipDesc like :searchValue ")
              .append("     ) ");
        }

        Session session = primarySessionFactory.openSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(imsVO.getQueryGroup())) {
                q.setParameter("groupId", imsVO.getQueryGroup());
            }
            if (StringUtils.isNotBlank(imsVO.getQueryIp())) {
                q.setParameter("ipAddr", imsVO.getQueryIp());
            }
            if (StringUtils.isNotBlank(imsVO.getQueryMac())) {
                q.setParameter("macAddr", imsVO.getQueryMac());
            }
            if (StringUtils.isNotBlank(imsVO.getSearchValue())) {
                q.setParameter("searchValue", "%".concat(imsVO.getSearchValue()).concat("%"));
            }
            return DataAccessUtils.longResult(q.list());

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public List<Object[]> findModuleIpDataSetting(
            IpMaintainServiceVO imsVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT distinct mids, dl.groupName ")
          .append(" FROM ModuleIpDataSetting mids ")
          .append("     ,DeviceList dl ")
          .append(" WHERE 1=1 ")
          .append(" AND mids.groupId = dl.groupId ");

        if (StringUtils.isNotBlank(imsVO.getQueryGroup())) {
            sb.append(" AND mids.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(imsVO.getQueryIp())) {
            sb.append(" AND mids.ipAddr = :ipAddr ");
        }
        if (StringUtils.isNotBlank(imsVO.getQueryMac())) {
            sb.append(" AND mids.macAddr = :macAddr ");
        }
        if (StringUtils.isNotBlank(imsVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.groupName like :searchValue ")
              .append("       or ")
              .append("       mids.ipAddr like :searchValue ")
              .append("       or ")
              .append("       mids.macAddr like :searchValue ")
              .append("       or ")
              .append("       mids.ipDesc like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(imsVO.getOrderColumn())) {
            sb.append(" order by ").append(imsVO.getOrderColumn()).append(" ").append(imsVO.getOrderDirection());
        } else {
            sb.append(" order by mids.updateTime desc ");
        }

        Session session = primarySessionFactory.openSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(imsVO.getQueryGroup())) {
                q.setParameter("groupId", imsVO.getQueryGroup());
            }
            if (StringUtils.isNotBlank(imsVO.getQueryIp())) {
                q.setParameter("ipAddr", imsVO.getQueryIp());
            }
            if (StringUtils.isNotBlank(imsVO.getQueryMac())) {
                q.setParameter("macAddr", imsVO.getQueryMac());
            }
            if (StringUtils.isNotBlank(imsVO.getSearchValue())) {
                q.setParameter("searchValue", "%".concat(imsVO.getSearchValue()).concat("%"));
            }
            if (startRow != null && pageLength != null) {
                q.setFirstResult(startRow);
                q.setMaxResults(pageLength);
            }
            return (List<Object[]>)q.list();

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public List<Object[]> findModuleIpDataSettingFromSecondaryDB(IpMaintainServiceVO imsVO,
            Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT distinct mids, dl.groupName ")
          .append(" FROM ModuleIpDataSetting mids ")
          .append("     ,DeviceList dl ")
          .append(" WHERE 1=1 ")
          .append(" AND mids.groupId = dl.groupId ");

        if (StringUtils.isNotBlank(imsVO.getQueryGroup())) {
            sb.append(" AND mids.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(imsVO.getQueryIp())) {
            sb.append(" AND mids.ipAddr = :ipAddr ");
        }
        if (StringUtils.isNotBlank(imsVO.getQueryMac())) {
            sb.append(" AND mids.macAddr = :macAddr ");
        }
        if (StringUtils.isNotBlank(imsVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.groupName like :searchValue ")
              .append("       or ")
              .append("       mids.ipAddr like :searchValue ")
              .append("       or ")
              .append("       mids.macAddr like :searchValue ")
              .append("       or ")
              .append("       mids.ipDesc like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(imsVO.getOrderColumn())) {
            sb.append(" order by ").append(imsVO.getOrderColumn()).append(" ").append(imsVO.getOrderDirection());
        } else {
            sb.append(" order by mids.updateTime desc ");
        }

        Session session = secondSessionFactory.getCurrentSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(imsVO.getQueryGroup())) {
                q.setParameter("groupId", imsVO.getQueryGroup());
            }
            if (StringUtils.isNotBlank(imsVO.getQueryIp())) {
                q.setParameter("ipAddr", imsVO.getQueryIp());
            }
            if (StringUtils.isNotBlank(imsVO.getQueryMac())) {
                q.setParameter("macAddr", imsVO.getQueryMac());
            }
            if (StringUtils.isNotBlank(imsVO.getSearchValue())) {
                q.setParameter("searchValue", "%".concat(imsVO.getSearchValue()).concat("%"));
            }
            if (startRow != null && pageLength != null) {
                q.setFirstResult(startRow);
                q.setMaxResults(pageLength);
            }
            return (List<Object[]>)q.list();

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public ModuleIpDataSetting findModuleIpDataSettingById(String settingId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM ModuleIpDataSetting mids ")
          .append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(settingId)) {
            sb.append(" AND mids.settingId = :settingId ");
        }

        Session session = primarySessionFactory.openSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(settingId)) {
                q.setParameter("settingId", settingId);
            }
            return (ModuleIpDataSetting)q.uniqueResult();

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public ModuleIpDataSetting findModuleIpDataSettingByIdFromSecondaryDB(String settingId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM ModuleIpDataSetting mids ")
          .append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(settingId)) {
            sb.append(" AND mids.settingId = :settingId ");
        }

        Session session = secondSessionFactory.openSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(settingId)) {
                q.setParameter("settingId", settingId);
            }
            return (ModuleIpDataSetting)q.uniqueResult();

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public ModuleIpDataSetting findModuleIpDataSettingByUk(String groupId, String ipAddr) {
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM ModuleIpDataSetting mids ")
          .append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(groupId)) {
            sb.append(" AND mids.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(ipAddr)) {
            sb.append(" AND mids.ipAddr = :ipAddr ");
        }

        Session session = primarySessionFactory.openSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(groupId)) {
                q.setParameter("groupId", groupId);
            }
            if (StringUtils.isNotBlank(ipAddr)) {
                q.setParameter("ipAddr", ipAddr);
            }
            return (ModuleIpDataSetting)q.uniqueResult();

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public ModuleIpDataSetting findModuleIpDataSettingByUkFromSecondaryDB(String groupId, String ipAddr) {
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM ModuleIpDataSetting mids ")
          .append(" WHERE 1=1 ");

        if (StringUtils.isNotBlank(groupId)) {
            sb.append(" AND mids.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(ipAddr)) {
            sb.append(" AND mids.ipAddr = :ipAddr ");
        }

        Session session = secondSessionFactory.openSession();

        if (session.getTransaction().getStatus() == TransactionStatus.NOT_ACTIVE) {
            session.beginTransaction();
        }
        //Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        try {
            Query<?> q = session.createQuery(sb.toString());

            if (StringUtils.isNotBlank(groupId)) {
                q.setParameter("groupId", groupId);
            }
            if (StringUtils.isNotBlank(ipAddr)) {
                q.setParameter("ipAddr", ipAddr);
            }
            return (ModuleIpDataSetting)q.uniqueResult();

        } catch (Exception e) {
            throw e;

        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }
}
