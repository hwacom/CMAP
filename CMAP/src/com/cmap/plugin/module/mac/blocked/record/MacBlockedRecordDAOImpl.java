package com.cmap.plugin.module.mac.blocked.record;

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

@Repository("macRecordDAO")
@Transactional
public class MacBlockedRecordDAOImpl extends BaseDaoHibernate implements MacBlockedRecordDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleBlockedMacList(MacBlockedRecordVO mbrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mbml.list_Id) ")
          .append(" from Module_Blocked_Mac_List mbml ")
          .append(" left join Module_Ip_Data_Setting mids ")
          .append(" on ( mbml.group_id = mids.group_id ")
          .append("      and mbml.mac_address = mids.mac_addr ) ")
          .append("     ,Device_List dl ")
          .append(" where 1=1 ")
          .append(" and mbml.group_Id = dl.group_Id ")
          .append(" and mbml.device_Id = dl.device_Id ");

        if (StringUtils.isNotBlank(mbrVO.getQueryGroupId())) {
            sb.append(" and mbml.group_Id = :groupId ");
        } else {
            sb.append(" and mbml.group_Id in (:groupId) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryDeviceId())) {
            sb.append(" and mbml.device_Id = :deviceId ");
        } else {
            sb.append(" and mbml.device_Id in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryMacAddress())) {
            sb.append(" and mbml.mac_Address = :macAddress ");
        }
        if (mbrVO.getQueryStatusFlag() != null && !mbrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbml.status_Flag in (:statusFlag) ");
        }
        if (mbrVO.getQueryExcludeStatusFlag() != null && !mbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbml.status_Flag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbml.mac_Address like :searchValue ")
              .append("       or ")
              .append("       mbml.block_By like :searchValue ")
              .append("       or ")
              .append("       mbml.block_Reason like :searchValue ")
              .append("     ) ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        q.setParameter("groupId", StringUtils.isNotBlank(mbrVO.getQueryGroupId()) ? mbrVO.getQueryGroupId() : mbrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(mbrVO.getQueryDeviceId()) ? mbrVO.getQueryDeviceId() : mbrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(mbrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", mbrVO.getQueryMacAddress());
        }
        if (mbrVO.getQueryStatusFlag() != null && !mbrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", mbrVO.getQueryStatusFlag());
        }
        if (mbrVO.getQueryExcludeStatusFlag() != null && !mbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", mbrVO.getQueryExcludeStatusFlag());
        }
        if (StringUtils.isNotBlank(mbrVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(mbrVO.getSearchValue()).concat("%"));
        }

        return DataAccessUtils.longResult(q.list());
    }

    @Override
    public List<Object[]> findModuleBlockedMacList(MacBlockedRecordVO mbrVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select ")
          .append("   dl.group_id ")
          .append("  ,dl.group_name ")
          .append("  ,mbml.mac_address ")
          .append("  ,mbml.status_flag ")
          .append("  ,mbml.block_time ")
          .append("  ,mbml.block_by ")
          .append("  ,mbml.block_reason ")
          .append("  ,mbml.open_time ")
          .append("  ,mbml.open_by ")
          .append("  ,mbml.open_reason ")
          .append("  ,mbml.update_time ")
          .append("  ,mbml.update_by ")
          .append("  ,mbml.list_id ")
          .append("  ,mbml.device_id ")
          .append(" from Module_Blocked_Mac_List mbml ")
          .append(" left join Module_Ip_Data_Setting mids ")
          .append(" on ( mbml.group_id = mids.group_id ")
          .append("      and mbml.mac_address = mids.mac_addr ) ")
          .append("     ,Device_List dl ")
          .append(" where 1=1 ")
          .append(" and mbml.group_Id = dl.group_Id ")
          .append(" and mbml.device_Id = dl.device_Id ");

        if (StringUtils.isNotBlank(mbrVO.getQueryListId())) {
            sb.append(" and mbml.list_Id = :listId ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryGroupId())) {
            sb.append(" and mbml.group_Id = :groupId ");
        } else if (mbrVO.getQueryGroupIdList() != null && !mbrVO.getQueryGroupIdList().isEmpty()) {
            sb.append(" and mbml.group_Id in (:groupId) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryDeviceId())) {
            sb.append(" and mbml.device_Id = :deviceId ");
        } else if (mbrVO.getQueryDeviceIdList() != null && !mbrVO.getQueryDeviceIdList().isEmpty()) {
            sb.append(" and mbml.device_Id in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryMacAddress())) {
            sb.append(" and mbml.mac_Address = :macAddress ");
        }
        if (mbrVO.getQueryStatusFlag() != null && !mbrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbml.status_Flag in (:statusFlag) ");
        }
        if (mbrVO.getQueryExcludeStatusFlag() != null && !mbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbml.status_Flag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbml.mac_Address like :searchValue ")
              .append("       or ")
              .append("       mbml.block_By like :searchValue ")
              .append("       or ")
              .append("       mbml.block_Reason like :searchValue ")
              .append("     ) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getOrderColumn())) {
            sb.append(" order by ").append(mbrVO.getOrderColumn()).append(" ").append(mbrVO.getOrderDirection());

        } else {
            sb.append(" order by mbml.block_Time desc ");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(mbrVO.getQueryListId())) {
            q.setParameter("listId", mbrVO.getQueryListId());
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryGroupId())) {
            q.setParameter("groupId", mbrVO.getQueryGroupId());
        } else if (mbrVO.getQueryGroupIdList() != null && !mbrVO.getQueryGroupIdList().isEmpty()) {
            q.setParameter("groupId", mbrVO.getQueryGroupIdList());
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryDeviceId())) {
            q.setParameter("deviceId", mbrVO.getQueryDeviceId());
        } else if (mbrVO.getQueryDeviceIdList() != null && !mbrVO.getQueryDeviceIdList().isEmpty()) {
            q.setParameter("deviceId", mbrVO.getQueryDeviceIdList());
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", mbrVO.getQueryMacAddress());
        }
        if (mbrVO.getQueryStatusFlag() != null && !mbrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", mbrVO.getQueryStatusFlag());
        }
        if (mbrVO.getQueryExcludeStatusFlag() != null && !mbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", mbrVO.getQueryExcludeStatusFlag());
        }
        if (StringUtils.isNotBlank(mbrVO.getSearchValue())) {
            q.setParameter("searchValue", "%".concat(mbrVO.getSearchValue()).concat("%"));
        }
        if (startRow != null && pageLength != null) {
            q.setFirstResult(startRow);
            q.setMaxResults(pageLength);
        }

        return (List<Object[]>)q.list();
    }

    @Override
    public ModuleBlockedMacList findLastestModuleBlockedMacList(MacBlockedRecordVO mbrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbml ")
          .append(" from ModuleBlockedMacList mbml ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(mbrVO.getQueryGroupId())) {
            sb.append(" and mbml.groupId = :groupId ");
        } else {
            sb.append(" and mbml.groupId in (:groupId) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryDeviceId())) {
            sb.append(" and mbml.deviceId = :deviceId ");
        } else {
            sb.append(" and mbml.deviceId in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(mbrVO.getQueryMacAddress())) {
            sb.append(" and mbml.macAddress = :macAddress ");
        }
        if (mbrVO.getQueryStatusFlag() != null && !mbrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbml.statusFlag in (:statusFlag) ");
        }
        if (mbrVO.getQueryExcludeStatusFlag() != null && !mbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbml.statusFlag not in (:excludeStatusFlag) ");
        }
        sb.append(" order by mbml.updateTime desc ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("groupId", StringUtils.isNotBlank(mbrVO.getQueryGroupId()) ? mbrVO.getQueryGroupId() : mbrVO.getQueryGroupIdList());
        q.setParameter("deviceId", StringUtils.isNotBlank(mbrVO.getQueryDeviceId()) ? mbrVO.getQueryDeviceId() : mbrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(mbrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", mbrVO.getQueryMacAddress());
        }
        if (mbrVO.getQueryStatusFlag() != null && !mbrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", mbrVO.getQueryStatusFlag());
        }
        if (mbrVO.getQueryExcludeStatusFlag() != null && !mbrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", mbrVO.getQueryExcludeStatusFlag());
        }

        List<ModuleBlockedMacList> entities = (List<ModuleBlockedMacList>)q.list();
        return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
    }
}
