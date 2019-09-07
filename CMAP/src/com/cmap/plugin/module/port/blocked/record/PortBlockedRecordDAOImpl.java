package com.cmap.plugin.module.port.blocked.record;

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

@Repository("portRecordDAO")
@Transactional
public class PortBlockedRecordDAOImpl extends BaseDaoHibernate implements PortBlockedRecordDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleBlockedPortList(PortBlockedRecordVO irVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mbpl.listId) ")
          .append(" from ModuleBlockedPortList mbpl ")
          .append("     ,DeviceList dl ")
          .append("     ,DevicePortInfo dpi ")
          .append(" where 1=1 ")
          .append(" and mbpl.groupId = dl.groupId ")
          .append(" and mbpl.deviceId = dl.deviceId ")
          .append(" and dl.deviceModel = dpi.deviceModel ")
          .append(" and mbpl.portId = dpi.portId ");

        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            sb.append(" and mbpl.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            sb.append(" and mbpl.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryPortId())) {
            sb.append(" and mbpl.portId = :portId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryStatusFlag())) {
            sb.append(" and mbpl.statusFlag = :statusFlag ");
        }
        if (StringUtils.isNotBlank(irVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.ipAddress like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockReason like :searchValue ")
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
        if (StringUtils.isNotBlank(irVO.getQueryPortId())) {
            q.setParameter("portId", irVO.getQueryPortId());
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
    public List<Object[]> findModuleBlockedPortList(PortBlockedRecordVO irVO, Integer startRow,
            Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbpl, dl, dpi ")
          .append(" from ModuleBlockedPortList mbpl ")
          .append("     ,DeviceList dl ")
          .append("     ,DevicePortInfo dpi ")
          .append(" where 1=1 ")
          .append(" and mbpl.groupId = dl.groupId ")
          .append(" and mbpl.deviceId = dl.deviceId ")
          .append(" and dl.deviceModel = dpi.deviceModel ")
          .append(" and mbpl.portId = dpi.portId ");

        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            sb.append(" and mbpl.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            sb.append(" and mbpl.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryPortId())) {
            sb.append(" and mbpl.portId = :portId ");
        }
        if (StringUtils.isNotBlank(irVO.getQueryStatusFlag())) {
            sb.append(" and mbpl.statusFlag = :statusFlag ");
        }
        if (StringUtils.isNotBlank(irVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.ipAddress like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockReason like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(irVO.getOrderColumn())) {
            sb.append(" order by ").append(irVO.getOrderColumn()).append(" ").append(irVO.getOrderDirection());

        } else {
            sb.append(" order by mbpl.blockTime desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            q.setParameter("groupId", irVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(irVO.getQueryDeviceId())) {
            q.setParameter("deviceId", irVO.getQueryDeviceId());
        }
        if (StringUtils.isNotBlank(irVO.getQueryPortId())) {
            q.setParameter("portId", irVO.getQueryPortId());
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

        return (List<Object[]>)q.list();
    }
}
