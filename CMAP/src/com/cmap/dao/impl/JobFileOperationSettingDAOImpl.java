package com.cmap.dao.impl;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.annotation.Log;
import com.cmap.dao.JobFileOperationSettingDAO;
import com.cmap.model.JobFileOperationSetting;

@Repository("jobFileOperationSettingDAOImpl")
@Transactional
public class JobFileOperationSettingDAOImpl extends BaseDaoHibernate implements JobFileOperationSettingDAO {
	@Log
    private static Logger log;
	
	@Override
	public JobFileOperationSetting findJobFileOperationSettingById(String settingId) {
		return getHibernateTemplate().getSessionFactory().getCurrentSession().get(JobFileOperationSetting.class, settingId);
	}

}
