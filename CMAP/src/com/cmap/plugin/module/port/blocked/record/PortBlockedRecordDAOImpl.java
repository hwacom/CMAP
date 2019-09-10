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
    public long countModuleBlockedPortList(PortBlockedRecordVO pbrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mbpl.listId) ")
          .append(" from ModuleBlockedPortList mbpl ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and mbpl.groupId = dl.groupId ")
          .append(" and mbpl.deviceId = dl.deviceId ");

        if (StringUtils.isNotBlank(pbrVO.getQueryGroupId())) {
            sb.append(" and mbpl.groupId = :groupId ");
        } else {
            sb.append(" and mbpl.groupId in (:groupId) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getQueryDeviceId())) {
            sb.append(" and mbpl.deviceId = :deviceId ");
        } else {
            sb.append(" and mbpl.deviceId in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getQueryPortId())) {
            sb.append(" and mbpl.portId = :portId ");
        }
        if (pbrVO.getQueryStatusFlag() != null && !pbrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbpl.statusFlag in (:statusFlag) ");
        }
        if (pbrVO.getQueryExcludeStatusFlag() != null && !pbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbpl.statusFlag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbpl.portid like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockReason like :searchValue ")
              .append("     ) ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("groupId", StringUtils.isNotBlank(pbrVO.getQueryGroupId()) ? pbrVO.getQueryGroupId() : pbrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(pbrVO.getQueryDeviceId()) ? pbrVO.getQueryDeviceId() : pbrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(pbrVO.getQueryPortId())) {
            q.setParameter("portId", pbrVO.getQueryPortId());
        }
        if (pbrVO.getQueryStatusFlag() != null && !pbrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", pbrVO.getQueryStatusFlag());
        }
        if (pbrVO.getQueryExcludeStatusFlag() != null && !pbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", pbrVO.getQueryExcludeStatusFlag());
        }
        if (StringUtils.isNotBlank(pbrVO.getSearchValue())) {
            q.setParameter("searchValue", pbrVO.getQueryGroupId());
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findModuleBlockedPortList(PortBlockedRecordVO pbrVO, Integer startRow,
            Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbpl, dl ")
          .append(" from ModuleBlockedPortList mbpl ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and mbpl.groupId = dl.groupId ")
          .append(" and mbpl.deviceId = dl.deviceId ");

        if (StringUtils.isNotBlank(pbrVO.getQueryGroupId())) {
            sb.append(" and mbpl.groupId = :groupId ");
        } else {
            sb.append(" and mbpl.groupId in (:groupId) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getQueryDeviceId())) {
            sb.append(" and mbpl.deviceId = :deviceId ");
        } else {
            sb.append(" and mbpl.deviceId in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getQueryPortId())) {
            sb.append(" and mbpl.portId = :portId ");
        }
        if (pbrVO.getQueryStatusFlag() != null && !pbrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbpl.statusFlag in (:statusFlag) ");
        }
        if (pbrVO.getQueryExcludeStatusFlag() != null && !pbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbpl.statusFlag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbpl.portid like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbpl.blockReason like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getOrderColumn())) {
            sb.append(" order by ").append(pbrVO.getOrderColumn()).append(" ").append(pbrVO.getOrderDirection());

        } else {
            sb.append(" order by mbpl.blockTime desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("groupId", StringUtils.isNotBlank(pbrVO.getQueryGroupId()) ? pbrVO.getQueryGroupId() : pbrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(pbrVO.getQueryDeviceId()) ? pbrVO.getQueryDeviceId() : pbrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(pbrVO.getQueryPortId())) {
            q.setParameter("portId", pbrVO.getQueryPortId());
        }
        if (pbrVO.getQueryStatusFlag() != null && !pbrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", pbrVO.getQueryStatusFlag());
        }
        if (pbrVO.getQueryExcludeStatusFlag() != null && !pbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", pbrVO.getQueryExcludeStatusFlag());
        }
        if (StringUtils.isNotBlank(pbrVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(pbrVO.getQueryGroupId()).concat("%"));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }

        return (List<Object[]>)q.list();
    }

    @Override
    public ModuleBlockedPortList findLastestModuleBlockedPortList(PortBlockedRecordVO pbrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbpl ")
          .append(" from ModuleBlockedPortList mbpl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(pbrVO.getQueryGroupId())) {
            sb.append(" and mbpl.groupId = :groupId ");
        } else {
            sb.append(" and mbpl.groupId in (:groupId) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getQueryDeviceId())) {
            sb.append(" and mbpl.deviceId = :deviceId ");
        } else {
            sb.append(" and mbpl.deviceId in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(pbrVO.getQueryPortId())) {
            sb.append(" and mbpl.portId = :portId ");
        }
        if (pbrVO.getQueryStatusFlag() != null && !pbrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbpl.statusFlag in (:statusFlag) ");
        }
        if (pbrVO.getQueryExcludeStatusFlag() != null && !pbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbpl.statusFlag not in (:excludeStatusFlag) ");
        }
        sb.append(" order by mbpl.updateTime desc ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("groupId", StringUtils.isNotBlank(pbrVO.getQueryGroupId()) ? pbrVO.getQueryGroupId() : pbrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(pbrVO.getQueryDeviceId()) ? pbrVO.getQueryDeviceId() : pbrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(pbrVO.getQueryPortId())) {
            q.setParameter("portId", pbrVO.getQueryPortId());
        }
        if (pbrVO.getQueryStatusFlag() != null && !pbrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", pbrVO.getQueryStatusFlag());
        }
        if (pbrVO.getQueryExcludeStatusFlag() != null && !pbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", pbrVO.getQueryExcludeStatusFlag());
        }

        List<ModuleBlockedPortList> entities = (List<ModuleBlockedPortList>)q.list();
        return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
    }
}
