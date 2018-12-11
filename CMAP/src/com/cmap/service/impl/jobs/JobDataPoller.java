package com.cmap.service.impl.jobs;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.service.BaseJobService;
import com.cmap.service.DataPollerService;
import com.cmap.service.vo.DataPollerServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;

@DisallowConcurrentExecution
public class JobDataPoller extends BaseJobImpl implements BaseJobService {
	private static Logger log = LoggerFactory.getLogger(JobBackupConfig.class);

	private DataPollerService dataPollerService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final String JOB_ID = UUID.randomUUID().toString();
		Timestamp startTime = new Timestamp((new Date()).getTime());
		DataPollerServiceVO dpsVO = new DataPollerServiceVO();

		try {
			JobDataMap jMap = context.getJobDetail().getJobDataMap();
			final String settingId = jMap.getString(Constants.QUARTZ_PARA_DATA_POLLER_SETTING_ID);

			dataPollerService = (DataPollerService)ApplicationContextUtil.getBean("dataPollerService");
			dpsVO = dataPollerService.excutePolling(settingId);

		} catch (Exception e) {
			log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

		} finally {
			Timestamp endTime = new Timestamp((new Date()).getTime());

			super.insertSysJobLog(JOB_ID, context, dpsVO.getJobExcuteResult(), dpsVO.getJobExcuteResultRecords(), startTime, endTime, dpsVO.getJobExcuteRemark());
		}
	}
}
