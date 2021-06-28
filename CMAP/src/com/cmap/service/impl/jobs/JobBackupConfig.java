package com.cmap.service.impl.jobs;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.service.BaseJobService;
import com.cmap.service.VersionService;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisallowConcurrentExecution
public class JobBackupConfig extends BaseJobImpl implements BaseJobService {
	private static Logger log = LoggerFactory.getLogger(JobBackupConfig.class);

	private VersionService versionService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final String JOB_ID = UUID.randomUUID().toString();
		Timestamp startTime = new Timestamp((new Date()).getTime());
		VersionServiceVO vsVO = new VersionServiceVO();

		List<String> deviceIds = new ArrayList<>();
		boolean actionFlag = true;
		
		try {
			actionFlag = checkDistributionSetting(context);
			
			if(actionFlag) {
				JobDataMap jMap = context.getJobDetail().getJobDataMap();
				final String deviceId = jMap.getString(Constants.QUARTZ_PARA_DEVICE_ID);
				final String configType = jMap.getString(Constants.QUARTZ_PARA_CONFIG_TYPE);

				log.debug("for debug action start!!");
				ObjectMapper mapper = new ObjectMapper();
				deviceIds = mapper.readValue(deviceId, new TypeReference<List<String>>(){});

				if (deviceIds != null && !deviceIds.isEmpty()) {
					versionService = (VersionService)ApplicationContextUtil.getBean("versionService");
					vsVO = versionService.backupConfig(configType, deviceIds, true, null);
				}
			}			

		} catch (Exception e) {
			log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

		} finally {
			if(actionFlag) {
				log.debug("for debug action log start!!");
				Timestamp endTime = new Timestamp((new Date()).getTime());

				super.insertSysJobLog(JOB_ID, context, vsVO.getJobExcuteResult(), vsVO.getJobExcuteResultRecords(), startTime, endTime, vsVO.getJobExcuteRemark());
			}			
		}
	}
}
