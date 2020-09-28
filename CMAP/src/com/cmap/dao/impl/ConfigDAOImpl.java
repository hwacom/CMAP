package com.cmap.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
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
import com.cmap.dao.ConfigDAO;
import com.cmap.dao.vo.ConfigVersionInfoDAOVO;
import com.cmap.model.ConfigContentSetting;
import com.cmap.model.ConfigVersionDiffLog;
import com.cmap.model.ConfigVersionInfo;

@Repository
@Transactional
public class ConfigDAOImpl extends BaseDaoHibernate implements ConfigDAO {
	@Log
    private static Logger log;
	
	@Override
	public long countConfigVersionInfoByDAOVO(ConfigVersionInfoDAOVO cviDAOVO) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) ")
		  .append("   from ConfigVersionInfo cvi")
		  .append("      left join DeviceList dl on cvi.deviceId = dl.deviceId ")
		  .append(" where 1 = 1 ")
		  .append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
			sb.append("	and cvi.configType = :configType ");
		}
		  
		sb.append(" and ((cvi.deviceId, cvi.createTime) IN ( ")
		  .append("     SELECT vi_1.deviceId, vi_1.createTime FROM ConfigVersionInfo vi_1");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
          sb.append(" where vi_1.deviceId = :deviceId1 ");
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
          sb.append(" where vi_1.deviceId in (:deviceId1) ");
        }
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin1())) {
			sb.append("	and vi_1.createTime >= DATE_FORMAT(:beginDate_1, '%Y-%m-%d') ");
		}
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd1())) {
			sb.append("	and vi_1.createTime < DATE_FORMAT(:endDate_1, '%Y-%m-%d') ");
		}
		sb.append(" )");
		
		//選擇兩組條件
  		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2()) || (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty())) {
  			sb.append(" or (cvi.deviceId, cvi.createTime) IN ( ")
			  .append("     SELECT vi_2.deviceId, vi_2.createTime FROM ConfigVersionInfo vi_2");
			
			if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2())) {
	          sb.append(" where vi_2.deviceId = :deviceId2 ");
	        } else if (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty()) {
	          sb.append(" where vi_2.deviceId in (:deviceId2) ");
	        }
			
			if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin2())) {
				sb.append("	and vi_2.createTime >= DATE_FORMAT(:beginDate_2, '%Y-%m-%d') ");
			}
			if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd2())) {
				sb.append("	and vi_2.createTime < DATE_FORMAT(:endDate_2, '%Y-%m-%d') ");
			}
			sb.append(" )");
		}
		
		sb.append(" )");
		
		if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       cvi.groupName like :searchValue ")
			  .append("       or ")
			  .append("       cvi.deviceName like :searchValue ")
			  .append("       or ")
			  .append("       cvi.deviceModel like :searchValue ")
			  .append("       or ")
			  .append("       cvi.configVersion like :searchValue ")
			  .append("     ) ");
		}
		
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
	    	q.setParameter("configType", cviDAOVO.getQueryConfigType());
		}
	    
        if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
            q.setParameter("deviceId1", cviDAOVO.getQueryDevice1());
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
            q.setParameterList("deviceId1", cviDAOVO.getQueryDevice1List());
        }

	    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin1())) {
	    	q.setParameter("beginDate_1", cviDAOVO.getQueryDateBegin1());
	    }
	    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd1())) {
	    	q.setParameter("endDate_1", cviDAOVO.getQueryDateEnd1());
	    }
	    
	    //選擇兩組條件
  		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2()) || (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty())) {
  			if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2())) {
  	            q.setParameter("deviceId2", cviDAOVO.getQueryDevice2());
  	        } else if (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty()) {
  	            q.setParameterList("deviceId2", cviDAOVO.getQueryDevice2List());
  	        }

  		    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin2())) {
  		    	q.setParameter("beginDate_2", cviDAOVO.getQueryDateBegin2());
  		    }
  		    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd2())) {
  		    	q.setParameter("endDate_2", cviDAOVO.getQueryDateEnd2());
  		    }
  		}
  		
	    if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(cviDAOVO.getSearchValue()).concat("%"));
	    }

	    return DataAccessUtils.longResult(q.list());
	}

	@Override
	public long countConfigVersionInfoByDAOVO4New(ConfigVersionInfoDAOVO cviDAOVO) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) ")
		  .append("   from ConfigVersionInfo cvi")
		  .append("      left join DeviceList dl on cvi.deviceId = dl.deviceId ")
		  .append(" where 1 = 1 ")
		  .append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
			sb.append("	and cvi.config_Type = :configType ");
		}
		  
		sb.append(" and (cvi.deviceId, cvi.createTime) IN ( ")
		  .append("     SELECT vi_1.deviceId, MAX(vi_1.createTime) FROM ConfigVersionInfo vi_1 ");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
          sb.append(" where vi_1.deviceId = :deviceId1 ");
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
          sb.append(" where vi_1.deviceId in (:deviceId1) ");
        }
		
		sb.append(" GROUP BY vi_1.deviceId)");
				
		if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
			sb.append(" and ( ")
			  .append("       cvi.groupName like :searchValue ")
			  .append("       or ")
			  .append("       cvi.deviceName like :searchValue ")
			  .append("       or ")
			  .append("       cvi.deviceModel like :searchValue ")
			  .append("       or ")
			  .append("       cvi.configVersion like :searchValue ")
			  .append("     ) ");
		}
		
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
	    	q.setParameter("configType", cviDAOVO.getQueryConfigType());
		}
	    
        if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
            q.setParameter("deviceId1", cviDAOVO.getQueryDevice1());
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
            q.setParameterList("deviceId1", cviDAOVO.getQueryDevice1List());
        }

	    if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(cviDAOVO.getSearchValue()).concat("%"));
	    }

	    return DataAccessUtils.longResult(q.list());
	}

	@Override
	public List<Object[]> findConfigVersionInfoByDAOVO(ConfigVersionInfoDAOVO cviDAOVO, Integer startRow, Integer pageLength) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select ")
		  .append(composeSelectStr(Constants.HQL_FIELD_NAME_FOR_VERSION, "cvi"))
		  .append(", ")
		  .append(composeSelectStr(Constants.HQL_FIELD_NAME_FOR_DEVICE, "dl"))
		  .append("   from ConfigVersionInfo cvi")
		  .append("      left join DeviceList dl on cvi.deviceId = dl.deviceId ")
		  .append(" where 1 = 1 ")
		  .append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
			sb.append("	and cvi.configType = :configType ");
		}
		  
		sb.append(" and ((cvi.deviceId, cvi.createTime) IN ( ")
		  .append("     SELECT vi_1.deviceId, vi_1.createTime FROM ConfigVersionInfo vi_1");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
	        sb.append(" where vi_1.deviceId = :deviceId1 ");
	    } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
	        sb.append(" where vi_1.deviceId in (:deviceId1) ");
	    }
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin1())) {
			sb.append("	and vi_1.createTime >= DATE_FORMAT(:beginDate_1, '%Y-%m-%d') ");
		}
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd1())) {
			sb.append("	and vi_1.createTime < DATE_FORMAT(:endDate_1, '%Y-%m-%d') ");
		}
		sb.append(" )");
		
		//選擇兩組條件
  		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2()) || (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty())) {
  			sb.append(" or (cvi.deviceId, cvi.createTime) IN ( ")
			  .append("     SELECT vi_2.deviceId, vi_2.createTime FROM ConfigVersionInfo vi_2");
			
			if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2())) {
	          sb.append(" where vi_2.deviceId = :deviceId2 ");
	        } else if (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty()) {
	          sb.append(" where vi_2.deviceId in (:deviceId2) ");
	        }
			
			if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin2())) {
				sb.append("	and vi_2.createTime >= DATE_FORMAT(:beginDate_2, '%Y-%m-%d') ");
			}
			if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd2())) {
				sb.append("	and vi_2.createTime < DATE_FORMAT(:endDate_2, '%Y-%m-%d') ");
			}
			sb.append(" )");
		}
		
		sb.append(" )");
		
		if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
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
			
		if (StringUtils.isNotBlank(cviDAOVO.getOrderColumn())) {
			sb.append(" order by cvi.").append(cviDAOVO.getOrderColumn()).append(" ").append(cviDAOVO.getOrderDirection());

		} else {
			sb.append(" order by cvi.createTime desc, cvi.groupName asc, cvi.deviceName asc ");
		}
			
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
	    	q.setParameter("configType", cviDAOVO.getQueryConfigType());
		}
	    
        if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
            q.setParameter("deviceId1", cviDAOVO.getQueryDevice1());
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
            q.setParameterList("deviceId1", cviDAOVO.getQueryDevice1List());
        }

	    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin1())) {
	    	q.setParameter("beginDate_1", cviDAOVO.getQueryDateBegin1());
	    }
	    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd1())) {
	    	q.setParameter("endDate_1", cviDAOVO.getQueryDateEnd1());
	    }
	    
	    //選擇兩組條件
  		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2()) || (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty())) {
  			if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice2())) {
  	            q.setParameter("deviceId2", cviDAOVO.getQueryDevice2());
  	        } else if (cviDAOVO.getQueryDevice2List() != null && !cviDAOVO.getQueryDevice2List().isEmpty()) {
  	            q.setParameterList("deviceId2", cviDAOVO.getQueryDevice2List());
  	        }

  		    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateBegin2())) {
  		    	q.setParameter("beginDate_2", cviDAOVO.getQueryDateBegin2());
  		    }
  		    if (StringUtils.isNotBlank(cviDAOVO.getQueryDateEnd2())) {
  		    	q.setParameter("endDate_2", cviDAOVO.getQueryDateEnd2());
  		    }
  		}
  		
	    if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(cviDAOVO.getSearchValue()).concat("%"));
	    }
	    
	    if (startRow != null && pageLength != null) {
	    	q.setFirstResult(startRow);
		    q.setMaxResults(pageLength);
	    }

	    List<Object[]> retList = (List<Object[]>)q.list();

		return transObjList2ModelList4Version(retList);
	}

	@Override
	public List<Object[]> findConfigVersionInfoByDAOVO4New(ConfigVersionInfoDAOVO cviDAOVO, Integer startRow,
			Integer pageLength) {
		StringBuffer sb = new StringBuffer();
		sb.append("select ")
		  .append(composeSelectStr(Constants.HQL_FIELD_NAME_FOR_VERSION, "cvi"))
		  .append(", ")
		  .append(composeSelectStr(Constants.HQL_FIELD_NAME_FOR_DEVICE, "dl"))
		  .append("   from ConfigVersionInfo cvi")
		  .append("      left join DeviceList dl on cvi.deviceId = dl.deviceId ")
		  .append(" where 1 = 1 ")
		  .append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
		  .append(" and dl.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
			sb.append("	and cvi.configType = :configType ");
		}
		  
		sb.append(" and (cvi.deviceId, cvi.createTime) IN ( ")
		  .append("     SELECT vi_1.deviceId, MAX(vi_1.createTime) FROM ConfigVersionInfo vi_1 ");
		
		if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
          sb.append(" where vi_1.deviceId = :deviceId1 ");
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
          sb.append(" where vi_1.deviceId in (:deviceId1) ");
        }
		
		sb.append(" GROUP BY vi_1.deviceId)");
				
		if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
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
		
		if (StringUtils.isNotBlank(cviDAOVO.getOrderColumn())) {
			sb.append(" order by cvi.").append(cviDAOVO.getOrderColumn()).append(" ").append(cviDAOVO.getOrderDirection());

		} else {
			sb.append(" order by cvi.createTime desc, cvi.groupName asc, cvi.deviceName asc ");
		}
		
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());

	    if (StringUtils.isNotBlank(cviDAOVO.getQueryConfigType())) {
	    	q.setParameter("configType", cviDAOVO.getQueryConfigType());
		}
	    
        if (StringUtils.isNotBlank(cviDAOVO.getQueryDevice1())) {
            q.setParameter("deviceId1", cviDAOVO.getQueryDevice1());
        } else if (cviDAOVO.getQueryDevice1List() != null && !cviDAOVO.getQueryDevice1List().isEmpty()) {
            q.setParameterList("deviceId1", cviDAOVO.getQueryDevice1List());
        }

	    if (StringUtils.isNotBlank(cviDAOVO.getSearchValue())) {
	    	q.setParameter("searchValue", "%".concat(cviDAOVO.getSearchValue()).concat("%"));
	    }

	    if (startRow != null && pageLength != null) {
	    	q.setFirstResult(startRow);
		    q.setMaxResults(pageLength);
	    }

	    List<Object[]> retList = (List<Object[]>)q.list();

		return transObjList2ModelList4Version(retList);
	}

	/*
	 * 刪除採化學刪除，僅調整刪除註記、不實際delete掉資料
	 * @see com.cmap.dao.ConfigVersionInfoDAO#deleteConfigVersionInfoByVersionIds(java.util.List, java.lang.String)
	 */
	@Override
	public Integer deleteConfigVersionInfoByVersionIds(List<String> versionIDs, String actionBy) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select cvi ")
		  .append(" from ConfigVersionInfo cvi ")
		  .append(" where 1=1 ")
		  .append(" and cvi.versionId in (:versionId) ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("versionId", versionIDs);

	    List<ConfigVersionInfo> modelList = (List<ConfigVersionInfo>)q.list();

	    if (modelList != null && !modelList.isEmpty()) {
	    	for (ConfigVersionInfo cvi : modelList) {
	    		cvi.setDeleteFlag(MARK_AS_DELETE);
	    		cvi.setUpdateBy(actionBy);
	    		cvi.setUpdateTime(new Timestamp(new Date().getTime()));
	    		cvi.setDeleteBy(actionBy);
	    		cvi.setDeleteTime(new Timestamp(new Date().getTime()));

	    		getHibernateTemplate().save(cvi);
	    	}
	    }

		return null;
	}

	@Override
	public List<ConfigVersionInfo> findConfigVersionInfoByVersionIDs(List<String> versionIDs) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select cvi ")
		  .append(" from ConfigVersionInfo cvi ")
		  .append(" where 1=1 ")
		  .append(" and cvi.versionId in (:versionId) ");

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    q.setParameter("versionId", versionIDs);

		return (List<ConfigVersionInfo>)q.list();
	}

	@Override
    public ConfigVersionInfo getConfigVersionInfoByUK(String groupId, String deviceId, String configVersion) {
	    StringBuffer sb = new StringBuffer();
        sb.append(" select cvi ")
          .append(" from ConfigVersionInfo cvi ")
          .append(" where 1=1 ")
          .append(" and cvi.groupId = :groupId ")
          .append(" and cvi.deviceId = :deviceId ")
          .append(" and cvi.configVersion = :configVersion ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("groupId", groupId);
        q.setParameter("deviceId", deviceId);
        q.setParameter("configVersion", configVersion);

        return (ConfigVersionInfo)q.uniqueResult();
    }

	@Override
    public ConfigVersionInfo getLastConfigVersionInfoByDeviceIdAndConfigType(String deviceId, String configType) {
	    StringBuffer sb = new StringBuffer();
        sb.append(" select cvi ")
          .append(" from ConfigVersionInfo cvi ")
          .append(" where 1=1 ")
          .append(" and cvi.deviceId = :deviceId ");
        
        if(StringUtils.isNotBlank(configType)) {
        	sb.append(" and cvi.configType = :configType ");
        }
        
        sb.append(" and cvi.deleteFlag = '").append(Constants.DATA_MARK_NOT_DELETE).append("' ")
          .append(" order by createTime desc");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("deviceId", deviceId);
        if(StringUtils.isNotBlank(configType)) {
        	q.setParameter("configType", configType);
        }

        List<ConfigVersionInfo> result = (List<ConfigVersionInfo>) q.list();
        return result == null || result.isEmpty() ? null : result.get(0);
    }
	
	@Override
	public void insertConfigVersionInfo(ConfigVersionInfo configVersionInfo) {
		getHibernateTemplate().save(configVersionInfo);
	}

	@Override
	public List<ConfigContentSetting> findConfigContentSetting(String settingType, String deviceModel, String deviceNameLike, String deviceListId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from ConfigContentSetting ccs ")
		  .append(" where 1=1 ");

		if (StringUtils.isNotBlank(settingType)) {
			sb.append(" and settingType = :settingType ");
		}
		if (StringUtils.isNotBlank(deviceModel)) {
			sb.append(" and deviceModel = :deviceModel ");
		}
		if (StringUtils.isNotBlank(deviceNameLike)) {
			sb.append(" and deviceNameLike like :deviceNameLike ");
		}
		if (StringUtils.isNotBlank(deviceListId)) {
			sb.append(" and deviceListId = :deviceListId ");
		}

		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    Query<?> q = session.createQuery(sb.toString());
	    if (StringUtils.isNotBlank(settingType)) {
			q.setParameter("settingType", settingType);
		}
		if (StringUtils.isNotBlank(deviceModel)) {
			q.setParameter("deviceModel", deviceModel);
		}
		if (StringUtils.isNotBlank(deviceNameLike)) {
			q.setParameter("deviceNameLike", "%"+deviceNameLike+"%");
		}
		if (StringUtils.isNotBlank(deviceListId)) {
			q.setParameter("deviceListId", deviceListId);
		}

		return (List<ConfigContentSetting>)q.list();
	}

    @Override
    public ConfigVersionDiffLog findConfigVersionDiffLogById(String diffLogId) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select cvdl ")
          .append(" from ConfigVersionDiffLog cvdl ")
          .append(" where 1=1 ")
          .append(" and cvdl.diffLogId = :diffLogId ");

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query<?> q = session.createQuery(sb.toString());
        q.setParameter("diffLogId", diffLogId);

        return (ConfigVersionDiffLog)q.uniqueResult();
    }

}
