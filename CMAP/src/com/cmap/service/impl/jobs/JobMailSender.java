package com.cmap.service.impl.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
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
import com.cmap.service.SysMailService;
import com.cmap.service.vo.SysMailServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;

@DisallowConcurrentExecution
public class JobMailSender extends BaseJobImpl implements BaseJobService {
    private static Logger log = LoggerFactory.getLogger(JobMailSender.class);

    private SysMailService sysMailService;

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
	        final String settingIdVar = jMap.getString(Constants.QUARTZ_PARA_MAIL_LIST_SETTING_ID);
	        String[] settingIds = settingIdVar.split(",");
	
	        sysMailService = (SysMailService)ApplicationContextUtil.getBean("sysMailService");
	
	        for (String mailListSettingId : settingIds) {
	            final String JOB_ID = UUID.randomUUID().toString();
	            Timestamp startTime = new Timestamp((new Date()).getTime());
	            SysMailServiceVO smVO = new SysMailServiceVO();
	
	            try {
	                smVO = sysMailService.executeSendMail(mailListSettingId);
	
	            } catch (Exception e) {
	                log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);
	
	                smVO.setJobExcuteResult(Result.FAILED);
	                smVO.setJobExcuteResultRecords("0");
	                smVO.setJobExcuteRemark(e.getMessage() + ", JID:["+JOB_ID+"]");
	
	            } finally {
	                Timestamp endTime = new Timestamp((new Date()).getTime());
	
	                super.insertSysJobLog(
	                        JOB_ID, context, smVO.getJobExcuteResult(), smVO.getJobExcuteResultRecords(),
	                        startTime, endTime, smVO.getJobExcuteRemark());
	            }
	        }
		}
    }
}
