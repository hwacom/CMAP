package com.cmap.service.impl.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
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
import com.cmap.service.SysCheckService;
import com.cmap.service.vo.SysCheckServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;

@DisallowConcurrentExecution
public class JobSysCheck4Update extends BaseJobImpl implements BaseJobService {
	private static Logger log = LoggerFactory.getLogger(JobSysCheck4Update.class);

	SysCheckService sysCheckService;

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
			SysCheckServiceVO scsVO = new SysCheckServiceVO();

			try {
				final List<String> sqls = (List<String>)jMap.get(Constants.QUARTZ_PARA_SYS_CHECK_SQLS);

				if (sqls != null && !sqls.isEmpty()) {
					sysCheckService = (SysCheckService)ApplicationContextUtil.getBean("sysCheckService");
					scsVO = sysCheckService.excuteUpdateSQLs(sqls, true);
				}

			} catch (Exception e) {
				result = Result.FAILED;
				log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

			} finally {
				Timestamp endTime = new Timestamp((new Date()).getTime());

				super.insertSysJobLog(JOB_ID, context, result, scsVO.getJobExcuteResultRecords(), startTime, endTime, scsVO.getJobExcuteRemark());
			}
		}		
	}
}
