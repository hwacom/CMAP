package com.cmap.plugin.module.blocked.record;

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

@Repository("blockedRecordDAO")
@Transactional
public class BlockedRecordDAOImpl extends BaseDaoHibernate implements BlockedRecordDAO {
    @Log
    private static Logger log;

    @Override
    public long countModuleBlockedList(BlockedRecordVO ibrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select count(mbl.list_Id) ")
          .append(" from module_blocked_list mbl ")
          .append(" left join Module_Ip_Data_Setting mids ")
          .append(" on ( mbl.group_id = mids.group_id ")
          .append("      and mbl.ip_address = mids.ip_addr ) ")
          .append("     ,Device_List dl ")
          .append(" where 1=1 ")
          .append(" and mbl.device_Id = dl.device_Id ");

        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbl.device_Id = :deviceId ");
        } else {
            sb.append(" and mbl.device_Id in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            sb.append(" and mbl.block_type = :blockType ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbl.ip_Address = :ipAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            sb.append(" and mbl.mac_Address = :macAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            sb.append(" and mbl.port_id = :portId ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbl.status_Flag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbl.status_Flag not in (:excludeStatusFlag) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
            sb.append(" and ( ")
              .append("       mbl.ip_Address like :searchValue ")
              .append("       or ")
              .append("       mbl.mac_Address like :searchValue ")
              .append("       or ")
              .append("       mbl.port_id like :searchValue ")
              .append("       or ")
              .append("       mbl.block_By like :searchValue ")
              .append("       or ")
              .append("       mbl.block_Reason like :searchValue ")
              .append("       or ")
              .append("       mids.ip_Desc like :searchValue ")
              .append("     ) ");
        }
		
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        q.setParameter("deviceId", StringUtils.isNotBlank(ibrVO.getQueryDeviceId()) ? ibrVO.getQueryDeviceId() : ibrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            q.setParameter("blockType", ibrVO.getQueryBlockType());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", ibrVO.getQueryMacAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            q.setParameter("portId", ibrVO.getQueryPortId());
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
    public List<Object[]> findModuleBlockedList(BlockedRecordVO ibrVO, Integer startRow, Integer pageLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select ")
          .append("   dl.group_id ")
          .append("  ,dl.group_name ")
          .append("  ,mbl.device_id ")
          .append("  ,mbl.block_type ")
          .append("  ,mbl.ip_address ")
          .append("  ,mids.ip_desc ")
          .append("  ,mbl.mac_address ")
          .append("  ,mbl.port_id ")
          .append("  ,mbl.global_Value ")
          .append("  ,mbl.status_flag ")
          .append("  ,mbl.script_code ")
          .append("  ,mbl.script_name ")
          .append("  ,mbl.undo_script_code ")
          .append("  ,mbl.block_time ")
          .append("  ,mbl.block_by ")
          .append("  ,mbl.block_reason ")
          .append("  ,mbl.open_time ")
          .append("  ,mbl.open_by ")
          .append("  ,mbl.open_reason ")
          .append("  ,mbl.update_time ")
          .append("  ,mbl.update_by ")
          .append("  ,mbl.list_id ")
          .append("  ,dl.device_name ")
          .append(" from module_blocked_list mbl ")
          .append(" left join Module_Ip_Data_Setting mids ")
          .append(" on ( mbl.group_id = mids.group_id ")
          .append("      and mbl.ip_address = mids.ip_addr ) ")
          .append("     ,Device_List dl ")
          .append(" where 1=1 ")
          .append(" and mbl.device_Id = dl.device_Id ");

        if (StringUtils.isNotBlank(ibrVO.getQueryListId())) {
            sb.append(" and mbl.list_Id = :listId ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbl.device_Id = :deviceId ");
        } else if (ibrVO.getQueryDeviceIdList() != null && !ibrVO.getQueryDeviceIdList().isEmpty()) {
            sb.append(" and mbl.device_Id in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            sb.append(" and mbl.block_type = :blockType ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbl.ip_Address = :ipAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            sb.append(" and mbl.mac_Address = :macAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            sb.append(" and mbl.port_id = :portId ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbl.status_Flag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbl.status_Flag not in (:excludeStatusFlag) ");
        }
        
        if (StringUtils.isNotBlank(ibrVO.getSearchValue())) {
        	sb.append(" and ( ")
	            .append("       mbl.ip_Address like :searchValue ")
	            .append("       or ")
	            .append("       mbl.mac_Address like :searchValue ")
	            .append("       or ")
	            .append("       mbl.port_id like :searchValue ")
	            .append("       or ")
	            .append("       mbl.block_By like :searchValue ")
	            .append("       or ")
	            .append("       mbl.block_Reason like :searchValue ")
	            .append("       or ")
	            .append("       mids.ip_Desc like :searchValue ")
	            .append("     ) ");
        }
        
		if (StringUtils.isNotBlank(ibrVO.getOrderColumn())) {
			sb.append(" order by ").append(ibrVO.getOrderColumn()).append(" ").append(ibrVO.getOrderDirection());
		} else {
			sb.append(" order by mbl.block_type, mbl.block_time desc ");
		}
		
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createNativeQuery(sb.toString());

        if (StringUtils.isNotBlank(ibrVO.getQueryListId())) {
            q.setParameter("listId", ibrVO.getQueryListId());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceId());
        } else if (ibrVO.getQueryDeviceIdList() != null && !ibrVO.getQueryDeviceIdList().isEmpty()) {
            q.setParameter("deviceId", ibrVO.getQueryDeviceIdList());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            q.setParameter("blockType", ibrVO.getQueryBlockType());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", ibrVO.getQueryMacAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            q.setParameter("portId", ibrVO.getQueryPortId());
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
    public List<ModuleBlockedList> findModuleBlockedList(BlockedRecordVO ibrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbl ")
          .append(" from ModuleBlockedList mbl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbl.deviceId = :deviceId ");
        } else {
            sb.append(" and mbl.deviceId in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            sb.append(" and mbl.blockType = :blockType ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbl.ipAddress = :ipAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            sb.append(" and mbl.macAddress = :macAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            sb.append(" and mbl.port = :portId ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbl.statusFlag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbl.statusFlag not in (:excludeStatusFlag) ");
        }
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("deviceId", StringUtils.isNotBlank(ibrVO.getQueryDeviceId()) ? ibrVO.getQueryDeviceId() : ibrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            q.setParameter("blockType", ibrVO.getQueryBlockType());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", ibrVO.getQueryMacAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            q.setParameter("portId", ibrVO.getQueryPortId());
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", ibrVO.getQueryStatusFlag());
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", ibrVO.getQueryExcludeStatusFlag());
        }

        return (List<ModuleBlockedList>)q.list();
    }
    
    @Override
    public ModuleBlockedList findLastestModuleBlockedList(BlockedRecordVO ibrVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select mbl ")
          .append(" from ModuleBlockedList mbl ")
          .append(" where 1=1 ");

        if (StringUtils.isNotBlank(ibrVO.getQueryDeviceId())) {
            sb.append(" and mbl.deviceId = :deviceId ");
        } else {
            sb.append(" and mbl.deviceId in (:deviceId) ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            sb.append(" and mbl.blockType = :blockType ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            sb.append(" and mbl.ipAddress = :ipAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            sb.append(" and mbl.macAddress = :macAddress ");
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            sb.append(" and mbl.port = :portId ");
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            sb.append(" and mbl.statusFlag in (:statusFlag) ");
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            sb.append(" and mbl.statusFlag not in (:excludeStatusFlag) ");
        }
        sb.append(" order by mbl.updateTime desc ");
        
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());

        q.setParameter("deviceId", StringUtils.isNotBlank(ibrVO.getQueryDeviceId()) ? ibrVO.getQueryDeviceId() : ibrVO.getQueryDeviceIdList());

        if (StringUtils.isNotBlank(ibrVO.getQueryBlockType())) {
            q.setParameter("blockType", ibrVO.getQueryBlockType());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryIpAddress())) {
            q.setParameter("ipAddress", ibrVO.getQueryIpAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryMacAddress())) {
            q.setParameter("macAddress", ibrVO.getQueryMacAddress());
        }
        if (StringUtils.isNotBlank(ibrVO.getQueryPortId())) {
            q.setParameter("portId", ibrVO.getQueryPortId());
        }
        if (ibrVO.getQueryStatusFlag() != null && !ibrVO.getQueryStatusFlag().isEmpty()) {
            q.setParameterList("statusFlag", ibrVO.getQueryStatusFlag());
        }
        if (ibrVO.getQueryExcludeStatusFlag() != null && !ibrVO.getQueryExcludeStatusFlag().isEmpty()) {
            q.setParameterList("excludeStatusFlag", ibrVO.getQueryExcludeStatusFlag());
        }

        List<ModuleBlockedList> entities = (List<ModuleBlockedList>)q.list();
        return (entities != null && !entities.isEmpty()) ? entities.get(0) : null;
    }
}
