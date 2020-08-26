package com.cmap.service.impl.jobs;

import java.io.InputStream;
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
import com.cmap.dao.DeviceDAO;
import com.cmap.model.DeviceList;
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

	private DeviceDAO deviceDAO;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final String JOB_ID = UUID.randomUUID().toString();
		Timestamp startTime = new Timestamp((new Date()).getTime());
		VersionServiceVO vsVO = new VersionServiceVO();

		List<String> deviceListIds = new ArrayList<>();
		List<String> groupIds = new ArrayList<>();
		List<String> deviceIds = new ArrayList<>();
		boolean actionFlag = true;
		
		try {
			JobDataMap jMap = context.getJobDetail().getJobDataMap();

			final String groupId = jMap.getString(Constants.QUARTZ_PARA_GROUP_ID);
			final String deviceId = jMap.getString(Constants.QUARTZ_PARA_DEVICE_ID);
			final String configType = jMap.getString(Constants.QUARTZ_PARA_CONFIG_TYPE);

			if(StringUtils.equalsIgnoreCase(Env.DISTRIBUTED_FLAG, Constants.DATA_Y)) {
				String disGroupId = jMap.getString(Constants.QUARTZ_PARA_DISTRIBUTED_GROUP_ID);
				
				Properties prop = new Properties();
				final String propFileName = "application.properties";
				InputStream inputStream = ConnectionFactory.class.getClassLoader().getResourceAsStream(propFileName);
				prop.load(inputStream);
				
				if(!StringUtils.equalsAnyIgnoreCase(prop.getProperty("distributed.group.id"), disGroupId)){
					actionFlag = false;
					log.info("for debug action flag is false!!");
				}
			}
			
			if(actionFlag) {
				log.info("for debug action start!!");
				ObjectMapper mapper = new ObjectMapper();
				groupIds = mapper.readValue(groupId, new TypeReference<List<String>>(){});
				deviceIds = mapper.readValue(deviceId, new TypeReference<List<String>>(){});

				List<DeviceList> dList = null;
				if ((groupIds != null && !groupIds.isEmpty()) || (deviceIds != null && !deviceIds.isEmpty())) {
					deviceDAO = (DeviceDAO)ApplicationContextUtil.getBean("deviceDAO");
					dList = deviceDAO.findDistinctDeviceListByGroupIdsOrDeviceIds(groupIds, deviceIds);

					if (dList != null && !dList.isEmpty()) {
						for (DeviceList d : dList) {
							deviceListIds.add(d.getDeviceListId());
						}
					}
				}

				if (deviceListIds != null && !deviceListIds.isEmpty()) {
					versionService = (VersionService)ApplicationContextUtil.getBean("versionService");
					vsVO = versionService.backupConfig(configType, deviceListIds, true, null);
				}
			}			

		} catch (Exception e) {
			log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

		} finally {
			if(actionFlag) {
				log.info("for debug action log start!!");
				Timestamp endTime = new Timestamp((new Date()).getTime());

				super.insertSysJobLog(JOB_ID, context, vsVO.getJobExcuteResult(), vsVO.getJobExcuteResultRecords(), startTime, endTime, vsVO.getJobExcuteRemark());
			}			
		}
	}
}
