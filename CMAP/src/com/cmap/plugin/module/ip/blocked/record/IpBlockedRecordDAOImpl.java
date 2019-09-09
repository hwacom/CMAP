package com.cmap.plugin.module.ip.blocked.record;

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
public class IpBlockedRecordDAOImpl extends BaseDaoHibernate implements IpBlockedRecordDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleBlockedIpList(IpBlockedRecordVO ibrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mbil.listId) ")
          .append(" from ModuleBlockedIpList mbil ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and mbil.groupId = dl.groupId ")
          .append(" and mbil.deviceId = dl.deviceId ");

        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            sb.append(" and mbil.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbil.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbil.ipAddress = :ipAddress ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbil.statusFlag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbil.statusFlag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
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
        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            q.setParameter("groupId", ibrVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", ibrVO.getQueryStatusFlag());
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", ibrVO.getQueryExcludeStatusFlag());
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
            q.setParameter("searchValue", ibrVO.getQueryGroupId());
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findModuleBlockedIpList(IpBlockedRecordVO ibrVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbil, dl ")
          .append(" from ModuleBlockedIpList mbil ")
          .append("     ,DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and mbil.groupId = dl.groupId ")
          .append(" and mbil.deviceId = dl.deviceId ");

        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            sb.append(" and mbil.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbil.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbil.ipAddress = :ipAddress ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbil.statusFlag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbil.statusFlag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       dl.ipAddress like :searchValue ")
              .append("       or ")
              .append("       mbil.blockBy like :searchValue ")
              .append("       or ")
              .append("       mbil.blockReason like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getOrderColumn())) {
            sb.append(" order by ").append(ibrVO.getOrderColumn()).append(" ").append(ibrVO.getOrderDirection());

        } else {
            sb.append(" order by mbil.blockTime desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            q.setParameter("groupId", ibrVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", ibrVO.getQueryStatusFlag());
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", ibrVO.getQueryExcludeStatusFlag());
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(ibrVO.getQueryGroupId()).concat("%"));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }

        return (List<Object[]>)q.list();
    }

    @Override
    public ModuleBlockedIpList findLastestModuleBlockedIpList(IpBlockedRecordVO ibrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbil ")
          .append(" from ModuleBlockedIpList mbil ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            sb.append(" and mbil.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbil.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbil.ipAddress = :ipAddress ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbil.statusFlag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbil.statusFlag not in (:excludeStatusFlag) ");
        }
        sb.append(" order by mbil.updateTime desc ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            q.setParameter("groupId", ibrVO.getQueryGroupId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", ibrVO.getQueryStatusFlag());
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", ibrVO.getQueryExcludeStatusFlag());
        }

        List<ModuleBlockedIpList> entities = (List<ModuleBlockedIpList>)q.list();
        return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
    }
}
