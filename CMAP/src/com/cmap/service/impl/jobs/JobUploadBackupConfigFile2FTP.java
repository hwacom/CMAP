package com.cmap.service.impl.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.configuration.hibernate.ConnectionFactory;
import com.cmap.service.BaseJobService;
import com.cmap.service.StepService;
import com.cmap.service.VersionService;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.JobServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;

@DisallowConcurrentExecution
public class JobUploadBackupConfigFile2FTP extends BaseJobImpl implements BaseJobService {
	private static Logger log = LoggerFactory.getLogger(JobUploadBackupConfigFile2FTP.class);

	private VersionService versionService;

	private StepService stepService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jMap = context.getJobDetail().getJobDataMap();
		boolean actionFlag = true;
		
		try {
			if(StringUtils.equalsIgnoreCase(Env.DISTRIBUTED_FLAG, Constants.DATA_Y)) {
				String disGroupId = jMap.getString(Constants.QUARTZ_PARA_DISTRIBUTED_GROUP_ID);
				
				Properties prop = new Properties();
				final String propFileName = "application.properties";
				InputStream inputStream = ConnectionFactory.class.getClassLoader().getResourceAsStream(propFileName);
				prop.load(inputStream);
				
				if(!StringUtils.equalsAnyIgnoreCase(prop.getProperty("distributed.group.id"), disGroupId)){
					actionFlag = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(actionFlag) {
			final String JOB_ID = UUID.randomUUID().toString();
			Result result = Result.SUCCESS;
			Timestamp startTime = new Timestamp((new Date()).getTime());
			JobServiceVO jsVO = new JobServiceVO();

			try {
				if (versionService == null) {
					versionService = (VersionService)ApplicationContextUtil.getBean("versionService");
				}

				/*
				 * 查找當天最新備份資料
				 */
				final String currentDateStr = Constants.FORMAT_YYYY_MM_DD.format(new Date());
				VersionServiceVO vsVO = new VersionServiceVO();
				vsVO.setOrderColumn("group_Name");
				vsVO.setOrderDirection("asc");
				vsVO.setQueryDateBegin1(currentDateStr);
				vsVO.setQueryDateEnd1(currentDateStr);
				vsVO.setJobTrigger(true);

				final boolean newestOnly = (Env.UPLOAD_NEWEST_BACKUP_FILE_ONLY != null && Env.UPLOAD_NEWEST_BACKUP_FILE_ONLY.equals("Y"))
						? true : false;
				vsVO.setQueryNewChkbox(newestOnly);

				List<VersionServiceVO> todayVersionInfo = versionService.findVersionInfo(vsVO, null, null);

				if (todayVersionInfo != null && !todayVersionInfo.isEmpty()) {
					stepService = (StepService)ApplicationContextUtil.getBean("stepService");

					ConfigInfoVO ciVO = new ConfigInfoVO();
					ciVO.settFtpIP(Env.TFTP_HOST_IP);
					ciVO.settFtpPort(Env.TFTP_HOST_PORT);
					ciVO.setFtpIP(Env.FTP_HOST_IP);
					ciVO.setFtpPort(Env.FTP_HOST_PORT);
					ciVO.setFtpAccount(Env.FTP_LOGIN_ACCOUNT);
					ciVO.setFtpPassword(Env.FTP_LOGIN_PASSWORD);

					List<VersionServiceVO> batchVOList = new ArrayList<>();

					StepServiceVO ssVO;
					BigDecimal totalCount = new BigDecimal(0);
					for (int i=1; i<=todayVersionInfo.size(); i++) {
						batchVOList.add(todayVersionInfo.get(i-1));

						if ((i == todayVersionInfo.size()) || (i % Env.FILES_UPLOAD_PER_BATCH_COUNT == 0)) {
							ssVO = stepService.doBackupFileUpload2FTPStep(batchVOList, ciVO, true);

							batchVOList = null;
							batchVOList = new ArrayList<>();

							totalCount = totalCount.add(new BigDecimal(ssVO.getJobExcuteResultRecords()));
						}
					}

					jsVO.setJobExcuteResultRecords(totalCount.toString());
				}

			} catch (Exception e) {
				result = Result.FAILED;
				log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

			} finally {
				Timestamp endTime = new Timestamp((new Date()).getTime());

				super.insertSysJobLog(JOB_ID, context, result, jsVO.getJobExcuteResultRecords(), startTime, endTime, jsVO.getJobExcuteRemark());
			}
		}		
	}
}
