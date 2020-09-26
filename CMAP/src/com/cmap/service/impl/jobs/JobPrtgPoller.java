package com.cmap.service.impl.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.configuration.hibernate.ConnectionFactory;
import com.cmap.service.BaseJobService;
import com.cmap.service.CommonService;
import com.cmap.service.vo.CommonServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;

public class JobPrtgPoller extends BaseJobImpl implements BaseJobService {
    private static Logger log = LoggerFactory.getLogger(JobDataPollerOperator.class);

    private CommonService commonService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	JobDataMap jMap = context.getJobDetail().getJobDataMap();        
        boolean actionFlag = true;
        
		try {
			if(StringUtils.equalsIgnoreCase(Env.DISTRIBUTED_FLAG, Constants.DATA_Y)) {
				String disGroupId = jMap.getString(Constants.QUARTZ_PARA_DISTRIBUTED_GROUP_ID);
				
				Properties prop = new Properties();
				final String propFileName = "distributed_setting.properties";
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
	        Timestamp startTime = new Timestamp((new Date()).getTime());
	        CommonServiceVO csVO = new CommonServiceVO();

	        commonService = (CommonService)ApplicationContextUtil.getBean("commonService");

	        // Step 1. 更新 Group & Device 清單
	        try {
	            csVO = commonService.refreshGroupAndDeviceMenu();

	        } catch (Exception e) {
	            log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

	            csVO.setJobExcuteResult(Result.FAILED);
	            csVO.setJobExcuteResultRecords("0");
	            csVO.setJobExcuteRemark(e.getMessage() + ", JID:["+JOB_ID+"]");

	        } finally {
	            Timestamp endTime = new Timestamp((new Date()).getTime());

	            Result result = csVO.getJobExcuteResult();
	            String resultRecords = csVO.getJobExcuteResultRecords();
	            String remark = csVO.getJobExcuteRemark();

	            super.insertSysJobLog(JOB_ID, context, result, resultRecords, startTime, endTime, remark);
	        }

	        startTime = new Timestamp((new Date()).getTime());
	        csVO = new CommonServiceVO();

	        // Step 2. 更新使用者 Group & Device 權限表
	        try {
	            csVO = commonService.refreshPrtgUserRightSetting(null);

	        } catch (Exception e) {
	            log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

	            csVO.setJobExcuteResult(Result.FAILED);
	            csVO.setJobExcuteResultRecords("0");
	            csVO.setJobExcuteRemark(e.getMessage() + ", JID:["+JOB_ID+"]");

	        } finally {
	            Timestamp endTime = new Timestamp((new Date()).getTime());

	            Result result = csVO.getJobExcuteResult();
	            String resultRecords = csVO.getJobExcuteResultRecords();
	            String remark = csVO.getJobExcuteRemark();

	            super.insertSysJobLog(JOB_ID, context, result, resultRecords, startTime, endTime, remark);
	        }
		}        
    }
}
