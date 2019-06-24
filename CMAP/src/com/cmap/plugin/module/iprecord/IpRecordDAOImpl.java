package com.cmap.plugin.module.iprecord;

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

@Repository("ipRecordDAO")
@Transactional
public class IpRecordDAOImpl extends BaseDaoHibernate implements IpRecordDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleBlockedIpList(IpRecordVO irVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mbil.listId) ")
          .append(" from ModuleBlockedIpList mbil ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and mbil.groupId = dl.groupId ")
          .append(" and mbil.deviceId = dl.deviceId ");

        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            sb.append(" and mbil.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            sb.append(" and mbil.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryIpAddress())) {
            sb.append(" and mbil.ipAddress = :ipAddress ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryStatusFlag())) {
            sb.append(" and mbil.statusFlag = :statusFlag ");
        }
        if (StringUtils.isNotBlank(irVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.ipAddress like :searchValue ")
              .append("       or ")
              .append("       mbil.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbil.blockReason like :searchValue ")
              .append("     ) ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            q.setParameter("groupId", irVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            q.setParameter("deviceId", irVO.getQueryDeviceId());
        }
        if (StringUtils.isNotBlank(irVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", irVO.getQueryIpAddress());
        }
        if (StringUtils.isNotBlank(irVO.getQueryStatusFlag())) {
            q.setParameter("statusFlag", irVO.getQueryStatusFlag());
        }
        if (StringUtils.isNotBlank(irVO.getSearchValue())) {
            q.setParameter("searchValue", irVO.getQueryGroupId());
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<ModuleBlockedIpList> findModuleBlockedIpList(IpRecordVO irVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbil ")
          .append(" from ModuleBlockedIpList mbil ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and mbil.groupId = dl.groupId ")
          .append(" and mbil.deviceId = dl.deviceId ");

        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            sb.append(" and mbil.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            sb.append(" and mbil.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryIpAddress())) {
            sb.append(" and mbil.ipAddress = :ipAddress ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryStatusFlag())) {
            sb.append(" and mbil.statusFlag = :statusFlag ");
        }
        if (StringUtils.isNotBlank(irVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.ipAddress like :searchValue ")
              .append("       or ")
              .append("       mbil.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbil.blockReason like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(irVO.getOrderColumn())) {
            sb.append(" order by mbil.").append(irVO.getOrderColumn()).append(" ").append(irVO.getOrderDirection());

        } else {
            sb.append(" order by mbil.blockTime desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            q.setParameter("groupId", irVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            q.setParameter("deviceId", irVO.getQueryDeviceId());
        }
        if (StringUtils.isNotBlank(irVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", irVO.getQueryIpAddress());
        }
        if (StringUtils.isNotBlank(irVO.getQueryStatusFlag())) {
            q.setParameter("statusFlag", irVO.getQueryStatusFlag());
        }
        if (StringUtils.isNotBlank(irVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(irVO.getQueryGroupId()).concat("%"));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }

        return (List<ModuleBlockedIpList>)q.list();
    }
}
