package com.cmap.plugin.module.ip.mapping;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.annotation.Log;
import com.cmap.dao.impl.BaseDaoHibernate;
import com.cmap.model.MibOidMapping;

@Repository("ipMappingDAO")
@Transactional
public class IpMappingDAOImpl extends BaseDaoHibernate implements IpMappingDAO {
    @Log
    private static Logger log;

    @Autowired
    @Qualifier("secondSessionFactory")
    private SessionFactory secondSessionFactory;

    @Override
    public List<ModuleMacTableExcludePort> findModuleMacTableExcludePort(String groupId, String deviceId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from ModuleMacTableExcludePort mmtep ")
          .append(" where 1=1 ")
          .append(" and mmtep.groupId = :groupId ")
          .append(" and mmtep.deviceId = :deviceId ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("groupId", groupId);
        q.setParameter("deviceId", deviceId);
        return (List<ModuleMacTableExcludePort>)q.list();
    }

    @Override
    public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from MibOidMapping mom ")
          .append(" where 1=1 ")
          .append(" and mom.oidName in (:oidNames) ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameterList("oidNames", oidNames);
        return (List<MibOidMapping>)q.list();
    }

	@Override
	public List<MibOidMapping> findMibOidMappingOfTableEntryByNameLike(String tableOidName) {
		StringBuffer sb = new StringBuffer();
        sb.append(" from MibOidMapping mom ")
          .append(" where 1=1 ")
          .append(" and mom.oidName like :tableOidName ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("tableOidName", tableOidName + ".%");
        return (List<MibOidMapping>)q.list();
	}

	@Override
	public List<Object[]> findEachIpAddressLastestModuleIpMacPortMapping(String groupId) {
		/*
		 * SELECT 
				m1.group_id
			   ,m1.ip_address
			   ,m1.mac_address
			   ,m1.port_id
			FROM `module_ip_mac_port_mapping` m1
			    ,(SELECT
			      	mm.group_id	
			       ,mm.ip_address
			       ,max(create_time) create_time
			      FROM `module_ip_mac_port_mapping` mm
			      WHERE 1=1
			      and mm.group_id = '15931'
			      group BY
			      	mm.group_id	
			       ,mm.ip_address
			     ) m2
			WHERE 1=1
			AND m1.group_id = m2.group_id
			AND m1.ip_address = m2.ip_address
			AND m1.create_time = m2.create_time
			AND m1.group_id = '15931'
		 */
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ")
		  .append("   m1.group_id, m1.device_id, m1.ip_address, m1.mac_address, m1.port_id ")
		  .append(" FROM Module_Ip_Mac_Port_Mapping m1 ")
		  .append("     ,(SELECT ")
		  .append("         mm.group_id, mm.ip_address, max(create_time) create_time ")
		  .append("       FROM Module_Ip_Mac_Port_Mapping mm ")
		  .append("       WHERE 1=1 ")
		  .append("       AND mm.group_id = :groupId ")
		  .append("       GROUP BY ")
		  .append("         mm.group_id, mm.ip_address ")
		  .append("      ) m2 ")
		  .append(" WHERE 1=1 ")
          .append(" AND m1.group_id = m2.group_id ")
          .append(" AND m1.ip_address = m2.ip_address ")
          .append(" AND m1.create_time = m2.create_time ")
          .append(" AND m1.group_id = :groupId ");

      Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
      Query<?> q = session.createNativeQuery(sb.toString());
      q.setParameter("groupId", groupId);
      return (List<Object[]>)q.list();
	}
}
