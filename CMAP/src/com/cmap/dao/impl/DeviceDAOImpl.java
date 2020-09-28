package com.cmap.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.dao.vo.DeviceDAOVO;
import com.cmap.model.DeviceDetailInfo;
import com.cmap.model.DeviceDetailMapping;
import com.cmap.model.DeviceList;
import com.cmap.model.DeviceLoginInfo;

@Repository("deviceDAO")
@Transactional
public class DeviceDAOImpl extends BaseDaoHibernate implements DeviceDAO {
	@Log
    private static Logger log;
	
	@Override
	public DeviceList findDeviceListByDeviceListId(String deviceListId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from DeviceList dl ")
		  .append(" where 1=1 ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deviceListId = :deviceListId ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("deviceListId", deviceListId);

		return (DeviceList)q.uniqueResult();
	}

	@Override
    public DeviceList findDeviceListByDeviceIp(String deviceIp) {
	    StringBuffer sb = new StringBuffer();
        sb.append(" from DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
          .append(" and dl.deviceIp = :deviceIp ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("deviceIp", deviceIp);

        List<DeviceList> returnList = (List<DeviceList>)q.list();
        return returnList.isEmpty() ? null : returnList.get(0);
    }

	@Override
	public DeviceList findDeviceListByGroupAndDeviceId(String groupId, String deviceId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from DeviceList dl ")
		  .append(" where 1=1 ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (StringUtils.isNotBlank(deviceId)) {
			sb.append(" and dl.deviceId = :deviceId ");
		}
		if (StringUtils.isNotBlank(groupId)) {
			sb.append(" and dl.groupId = :groupId ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(deviceId)) {
	    	q.setParameter("deviceId", deviceId);
		}
	    if (StringUtils.isNotBlank(groupId)) {
	    	q.setParameter("groupId", groupId);
		}

		List<DeviceList> returnList = (List<DeviceList>)q.list();
		return returnList.isEmpty() ? null : returnList.get(0);
	}

	@Override
	public void saveOrUpdateDeviceListByModel(List<DeviceList> entityList) {
		for (DeviceList entity : entityList) {
			getHibernateTemplate().saveOrUpdate(entity);
		}
	}

	@Override
	public List<DeviceList> findDistinctDeviceListByGroupIdsOrDeviceIds(List<String> groupIds, List<String> deviceIds) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select distinct dl ")
		  .append(" from DeviceList dl ")
		  .append(" where 1=1 ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and ( ");

		if (groupIds != null && !groupIds.isEmpty()) {
			sb.append("   dl.groupId in (:groupIds) ");
		}
		if ((groupIds != null && !groupIds.isEmpty()) && (deviceIds != null && !deviceIds.isEmpty())) {
			sb.append("   or ");
		}
		if (deviceIds != null && !deviceIds.isEmpty()) {
			sb.append("   dl.deviceId in (:deviceIds) ");
		}
		sb.append(" ) ")
		  .append(" order by ");

		if (groupIds != null && !groupIds.isEmpty()) {
			sb.append(" dl.groupId asc ");
		}
		if ((groupIds != null && !groupIds.isEmpty()) && (deviceIds != null && !deviceIds.isEmpty())) {
			sb.append(" , ");
		}
		if (deviceIds != null && !deviceIds.isEmpty()) {
			sb.append(" dl.deviceId asc ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (groupIds != null && !groupIds.isEmpty()) {
	    	q.setParameter("groupIds", groupIds);
	    }
	    if (deviceIds != null && !deviceIds.isEmpty()) {
	    	q.setParameter("deviceIds", deviceIds);
	    }

		return (List<DeviceList>)q.list();
	}

	@Override
	public List<Object[]> getGroupIdAndNameByGroupIds(List<String> groupIds) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select distinct dl.groupId, dl.groupName ")
		  .append(" from DeviceList dl ")
		  .append(" where 1=1 ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (groupIds != null && !groupIds.isEmpty()) {
			sb.append(" and dl.groupId in (:groupIds) ");
		}

		sb.append(" order by dl.groupId asc ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (groupIds != null && !groupIds.isEmpty()) {
	    	q.setParameter("groupIds", groupIds);
	    }

		return (List<Object[]>)q.list();
	}

	@Override
	public List<Object[]> getDeviceIdAndNameByDeviceIds(List<String> deviceIds) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select distinct dl.deviceId, dl.deviceName ")
		  .append(" from DeviceList dl ")
		  .append(" where 1=1 ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (deviceIds != null && !deviceIds.isEmpty()) {
			sb.append(" and dl.deviceId in (:deviceIds) ");
		}

		sb.append(" order by dl.deviceId asc ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (deviceIds != null && !deviceIds.isEmpty()) {
	    	q.setParameter("deviceIds", deviceIds);
	    }

		return (List<Object[]>)q.list();
	}

	@Override
	public List<DeviceDetailInfo> findDeviceDetailInfo(String deviceListId, String groupId, String deviceId, String infoName) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from DeviceDetailInfo ddi ")
		  .append(" where 1=1 ")
		  .append(" and ddi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (StringUtils.isNotBlank(deviceListId)) {
			sb.append(" and ddi.deviceListId = :deviceListId ");
		}
		if (StringUtils.isNotBlank(groupId)) {
			sb.append(" and ddi.groupId = :groupId ");
		}
		if (StringUtils.isNotBlank(deviceId)) {
			sb.append(" and ddi.deviceId = :deviceId ");
		}
		if (StringUtils.isNotBlank(infoName)) {
			sb.append(" and ddi.infoName = :infoName ");
		}

		sb.append(" order by ddi.infoName asc, ddi.infoOrder asc ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(deviceListId)) {
			q.setParameter("deviceListId", deviceListId);
		}
		if (StringUtils.isNotBlank(groupId)) {
			q.setParameter("groupId", groupId);
		}
		if (StringUtils.isNotBlank(deviceId)) {
			q.setParameter("deviceId", deviceId);
		}
		if (StringUtils.isNotBlank(infoName)) {
			q.setParameter("infoName", infoName);
		}

		return (List<DeviceDetailInfo>)q.list();
	}

	@Override
	public List<DeviceDetailMapping> findDeviceDetailMapping(String targetInfoName) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from DeviceDetailMapping ddm ")
		  .append(" where 1=1 ")
		  .append(" and ddm.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");

		if (StringUtils.isNotBlank(targetInfoName)) {
			sb.append(" and ddm.targetInfoName = :targetInfoName ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(targetInfoName)) {
			q.setParameter("targetInfoName", targetInfoName);
		}

		return (List<DeviceDetailMapping>)q.list();
	}

	@Override
	public boolean deleteDeviceDetailInfoByInfoName(
			String deviceListId, String groupId, String deviceId, String infoName, Timestamp deleteTime, String deleteBy) throws Exception {
		if (StringUtils.isBlank(deviceListId) && StringUtils.isBlank(groupId) && StringUtils.isBlank(deviceId)) {
			throw new Exception("欲刪除設備明細資料(Device_Detail_Info)傳入參數檢核失敗 >> deviceListId, groupId, deviceId 不得皆為空");
		}

		StringBuffer sb = new StringBuffer();
		sb.append(" delete DeviceDetailInfo ddi ")
		  .append(" where 1=1 ");

		if (StringUtils.isNotBlank(deviceListId)) {
			sb.append(" and ddi.deviceListId = :deviceListId ");
		}
		if (StringUtils.isNotBlank(groupId)) {
			sb.append(" and ddi.groupId = :groupId ");
		}
		if (StringUtils.isNotBlank(deviceId)) {
			sb.append(" and ddi.deviceId = :deviceId ");
		}
		if (StringUtils.isNotBlank(infoName)) {
			sb.append(" and ddi.infoName = :infoName ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(deviceListId)) {
	    	q.setParameter("deviceListId", deviceListId);
	    }
	    if (StringUtils.isNotBlank(groupId)) {
	    	q.setParameter("groupId", groupId);
	    }
	    if (StringUtils.isNotBlank(deviceId)) {
	    	q.setParameter("deviceId", deviceId);
	    }
	    if (StringUtils.isNotBlank(infoName)) {
	    	q.setParameter("infoName", infoName);
	    }

	    q.executeUpdate();

		return true;
	}

	@Override
	public DeviceLoginInfo findDeviceLoginInfo(String deviceListId, String groupId, String deviceId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from DeviceLoginInfo dli ")
		  .append(" where 1=1 ");

		if (StringUtils.isNotBlank(deviceListId)) {
			sb.append(" and dli.deviceListId = :deviceListId ");
		}
		if (StringUtils.isNotBlank(groupId)) {
			sb.append(" and dli.groupId = :groupId ");
		}
		if (StringUtils.isNotBlank(deviceId)) {
			sb.append(" and dli.deviceId = :deviceId ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(deviceListId)) {
	    	q.setParameter("deviceListId", deviceListId);
		}
	    if (StringUtils.isNotBlank(groupId)) {
	    	q.setParameter("groupId", groupId);
		}
	    if (StringUtils.isNotBlank(deviceId)) {
	    	q.setParameter("deviceId", deviceId);
		}

		List<DeviceLoginInfo> returnList = (List<DeviceLoginInfo>)q.list();
		return returnList.isEmpty() ? null : returnList.get(0);
	}

    @Override
    public List<DeviceList> findDeviceListByDAOVO(DeviceDAOVO dlDAOVO) {
        StringBuffer sb = new StringBuffer();
        sb.append(" from DeviceList dl ")
          .append(" where 1=1 ")
          .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");
        if (StringUtils.isNotBlank(dlDAOVO.getGroupId())) {
            sb.append(" and dl.groupId = :groupId ");
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceId())) {
            sb.append(" and dl.deviceId = :deviceId ");
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceIp())) {
            sb.append(" and dl.deviceIp = :deviceIp ");
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceModel())) {
            sb.append(" and dl.deviceModel = :deviceModel ");
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceLayer())) {
            sb.append(" and dl.deviceLayer = :deviceLayer ");
        }
        if (dlDAOVO.getDeviceLayerList() != null && !dlDAOVO.getDeviceLayerList().isEmpty()) {
            sb.append(" and dl.deviceLayer in (:deviceLayerList)");
        }

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        if (StringUtils.isNotBlank(dlDAOVO.getGroupId())) {
            q.setParameter("groupId", dlDAOVO.getGroupId());
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceId())) {
            q.setParameter("deviceId", dlDAOVO.getDeviceId());
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceIp())) {
            q.setParameter("deviceIp", dlDAOVO.getDeviceIp());
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceModel())) {
            q.setParameter("deviceModel", dlDAOVO.getDeviceModel());
        }
        if (StringUtils.isNotBlank(dlDAOVO.getDeviceLayer())) {
            q.setParameter("deviceLayer", dlDAOVO.getDeviceLayer());
        }
        if (dlDAOVO.getDeviceLayerList() != null && !dlDAOVO.getDeviceLayerList().isEmpty()) {
            q.setParameterList("deviceLayerList", dlDAOVO.getDeviceLayerList());
        }
        return (List<DeviceList>)q.list();
    }
    

	@Override
	public long countDeviceListAndLastestVersionByDAOVO(DeviceDAOVO dlDAOVO) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*)")
		  .append("   from DeviceList dl ")
		  .append("      left join ConfigVersionInfo cvi on cvi.deviceId = dl.deviceId")
		  .append(" where 1 = 1 ")
		  .append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and (cvi.deviceId, cvi.createTime) IN ( ")
		  .append("     SELECT vi_1.deviceId, MAX(vi_1.createTime) FROM ConfigVersionInfo vi_1 GROUP BY vi_1.deviceId)");
		
		if (StringUtils.isNotBlank(dlDAOVO.getQueryDevice())) {
          sb.append(" and dl.deviceId = :deviceId ");
        } else if (dlDAOVO.getQueryDeviceList() != null && !dlDAOVO.getQueryDeviceList().isEmpty()) {
          sb.append(" and dl.deviceId in (:deviceId) ");
        }
		
		if (StringUtils.isNotBlank(dlDAOVO.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       dl.groupName like :searchValue ")
			  .append("       or ")
			  .append("       dl.deviceName like :searchValue ")
			  .append("       or ")
			  .append("       dl.deviceModel like :searchValue ")
			  .append("       or ")
			  .append("       cvi.configVersion like :searchValue ")
			  .append("     ) ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(dlDAOVO.getQueryDevice())) {
            q.setParameter("deviceId", dlDAOVO.getQueryDevice());
        } else if (dlDAOVO.getQueryDeviceList() != null && !dlDAOVO.getQueryDeviceList().isEmpty()) {
            q.setParameterList("deviceId", dlDAOVO.getQueryDeviceList());
        }

	    if (StringUtils.isNotBlank(dlDAOVO.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(dlDAOVO.getSearchValue()).concat("%"));
	    }

	    return DataAccessUtils.longResult(q.list());
	}

	@Override
	public List<Object[]> findDeviceListAndLastestVersionByDAOVO(DeviceDAOVO dlDAOVO, Integer startRow, Integer pageLength) {
		StringBuffer sb = new StringBuffer();
		sb.append("select" + composeSelectStr(Constants.HQL_FIELD_NAME_FOR_DEVICE_2, "dl"))
		  .append(", ")
		  .append(composeSelectStr(Constants.HQL_FIELD_NAME_FOR_VERSION_2, "cvi"))
		  .append("   from DeviceList dl ")
		  .append("      left join ConfigVersionInfo cvi on cvi.deviceId = dl.deviceId")
		  .append(" where 1 = 1 ")
		  .append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and (cvi.deviceId, cvi.createTime) IN ( ")
		  .append("     SELECT vi_1.deviceId, MAX(vi_1.createTime) FROM ConfigVersionInfo vi_1 GROUP BY vi_1.deviceId)");
		
		if (StringUtils.isNotBlank(dlDAOVO.getQueryDevice())) {
          sb.append(" and dl.deviceId = :deviceId ");
        } else if (dlDAOVO.getQueryDeviceList() != null && !dlDAOVO.getQueryDeviceList().isEmpty()) {
          sb.append(" and dl.deviceId in (:deviceId) ");
        }
		
		if (StringUtils.isNotBlank(dlDAOVO.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       dl.groupName like :searchValue ")
			  .append("       or ")
			  .append("       dl.deviceName like :searchValue ")
			  .append("       or ")
			  .append("       dl.deviceModel like :searchValue ")
			  .append("       or ")
			  .append("       cvi.configVersion like :searchValue ")
			  .append("     ) ");
		}

		if (StringUtils.isNotBlank(dlDAOVO.getOrderColumn())) {
			sb.append(" order by ").append(dlDAOVO.getOrderColumn()).append(" ").append(dlDAOVO.getOrderDirection());

		} else {
			sb.append(" order by dl.groupName asc, dl.deviceName asc ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

        if (StringUtils.isNotBlank(dlDAOVO.getQueryDevice())) {
            q.setParameter("deviceId", dlDAOVO.getQueryDevice());
        } else if (dlDAOVO.getQueryDeviceList() != null && !dlDAOVO.getQueryDeviceList().isEmpty()) {
            q.setParameterList("deviceId", dlDAOVO.getQueryDeviceList());
        }

	    if (StringUtils.isNotBlank(dlDAOVO.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(dlDAOVO.getSearchValue()).concat("%"));
	    }

	    if (startRow != null && pageLength != null) {
	    	q.setFirstResult(startRow);
		    q.setMaxResults(pageLength);
	    }

	    List<Object[]> retList = (List<Object[]>)q.list();

		return transObjList2ModelList4Device(retList);
	}
}
