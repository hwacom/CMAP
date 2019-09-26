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
        sb.append(" select count(mbil.list_Id) ")
          .append(" from Module_Blocked_Ip_List mbil ")
          .append(" left join Module_Ip_Data_Setting mids ")
          .append(" on ( mbil.group_id = mids.group_id ")
          .append("      and mbil.ip_address = mids.ip_addr ) ")
          .append("     ,Device_List dl ")
          .append(" where 1=1 ")
          .append(" and mbil.group_Id = dl.group_Id ")
          .append(" and mbil.device_Id = dl.device_Id ");

        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            sb.append(" and mbil.group_Id = :groupId ");
        } else {
            sb.append(" and mbil.group_Id in (:groupId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbil.device_Id = :deviceId ");
        } else {
            sb.append(" and mbil.device_Id in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbil.ip_Address = :ipAddress ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbil.status_Flag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbil.status_Flag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbil.ip_Address like :searchValue ")
              .append("       or ")
              .append("       mbil.block_By like :searchValue ")
              .append("       or ")
              .append("       mbil.block_Reason like :searchValue ")
              .append("       or ")
              .append("       mids.ip_Desc like :searchValue ")
              .append("     ) ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        q.setParameter("groupId", StringUtils.isNotBlank(ibrVO.getQueryGroupId()) ? ibrVO.getQueryGroupId() : ibrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(ibrVO.getQueryDeviceId()) ? ibrVO.getQueryDeviceId() : ibrVO.getQueryDeviceIdList());

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
            q.setParameter("searchValue", "%".concat(ibrVO.getSearchValue()).concat("%"));
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findModuleBlockedIpList(IpBlockedRecordVO ibrVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select ")
          .append("   dl.group_id ")
          .append("  ,dl.group_name ")
          .append("  ,mbil.ip_address ")
          .append("  ,mids.ip_desc ")
          .append("  ,mbil.status_flag ")
          .append("  ,mbil.block_time ")
          .append("  ,mbil.block_by ")
          .append("  ,mbil.block_reason ")
          .append("  ,mbil.open_time ")
          .append("  ,mbil.open_by ")
          .append("  ,mbil.open_reason ")
          .append("  ,mbil.update_time ")
          .append("  ,mbil.update_by ")
          .append("  ,mbil.list_id ")
          .append("  ,mbil.device_id ")
          .append(" from Module_Blocked_Ip_List mbil ")
          .append(" left join Module_Ip_Data_Setting mids ")
          .append(" on ( mbil.group_id = mids.group_id ")
          .append("      and mbil.ip_address = mids.ip_addr ) ")
          .append("     ,Device_List dl ")
          .append(" where 1=1 ")
          .append(" and mbil.group_Id = dl.group_Id ")
          .append(" and mbil.device_Id = dl.device_Id ");

        if (StringUtils.isNotBlank(ibrVO.getQueryListId())) {
            sb.append(" and mbil.list_Id = :listId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            sb.append(" and mbil.group_Id = :groupId ");
        } else if (ibrVO.getQueryGroupIdList() != null && !ibrVO.getQueryGroupIdList().isEmpty()) {
            sb.append(" and mbil.group_Id in (:groupId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbil.device_Id = :deviceId ");
        } else if (ibrVO.getQueryDeviceIdList() != null && !ibrVO.getQueryDeviceIdList().isEmpty()) {
            sb.append(" and mbil.device_Id in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbil.ip_Address = :ipAddress ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbil.status_Flag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbil.status_Flag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbil.ip_Address like :searchValue ")
              .append("       or ")
              .append("       mbil.block_By like :searchValue ")
              .append("       or ")
              .append("       mbil.block_Reason like :searchValue ")
              .append("       or ")
              .append("       mids.ip_Desc like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getOrderColumn())) {
            sb.append(" order by ").append(ibrVO.getOrderColumn()).append(" ").append(ibrVO.getOrderDirection());

        } else {
            sb.append(" order by mbil.block_Time desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(ibrVO.getQueryListId())) {
            q.setParameter("listId", ibrVO.getQueryListId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryGroupId())) {
            q.setParameter("groupId", ibrVO.getQueryGroupId());
        } else if (ibrVO.getQueryGroupIdList() != null && !ibrVO.getQueryGroupIdList().isEmpty()) {
            q.setParameter("groupId", ibrVO.getQueryGroupIdList());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceId());
        } else if (ibrVO.getQueryDeviceIdList() != null && !ibrVO.getQueryDeviceIdList().isEmpty()) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceIdList());
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
            q.setParameter("searchValue", "%".concat(ibrVO.getSearchValue()).concat("%"));
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
        } else {
            sb.append(" and mbil.groupId in (:groupId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbil.deviceId = :deviceId ");
        } else {
            sb.append(" and mbil.deviceId in (:deviceId) ");
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

        q.setParameter("groupId", StringUtils.isNotBlank(ibrVO.getQueryGroupId()) ? ibrVO.getQueryGroupId() : ibrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(ibrVO.getQueryDeviceId()) ? ibrVO.getQueryDeviceId() : ibrVO.getQueryDeviceIdList());

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
