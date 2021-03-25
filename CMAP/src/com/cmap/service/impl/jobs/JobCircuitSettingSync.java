package com.cmap.service.impl.jobs;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.cmap.comm.enums.ConnectionMode;
import com.cmap.configuration.hibernate.ConnectionFactory;
import com.cmap.dao.DeviceDAO;
import com.cmap.model.DeviceList;
import com.cmap.plugin.module.circuit.CircuitService;
import com.cmap.plugin.module.circuit.ModuleCircuitDiagramSetting;
import com.cmap.security.SecurityUtil;
import com.cmap.service.BaseJobService;
import com.cmap.service.StepService;
import com.cmap.service.vo.ScriptServiceVO;
import com.cmap.service.vo.StepServiceVO;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.impl.ApplicationContextUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisallowConcurrentExecution
public class JobCircuitSettingSync extends BaseJobImpl implements BaseJobService {
    private static Logger log = LoggerFactory.getLogger(JobCircuitSettingSync.class);

    private StepService stepService;

	private DeviceDAO deviceDAO;
	
	private CircuitService circuitService;
	
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	final String JOB_ID = UUID.randomUUID().toString();
		Timestamp startTime = new Timestamp((new Date()).getTime());
		VersionServiceVO vsVO = new VersionServiceVO();

		
		List<String> groupIds = new ArrayList<>();
		List<String> deviceIds = new ArrayList<>();
		boolean actionFlag = true;
		
		try {
			JobDataMap jMap = context.getJobDetail().getJobDataMap();

			final String groupId = jMap.getString(Constants.QUARTZ_PARA_GROUP_ID);
			final String deviceId = jMap.getString(Constants.QUARTZ_PARA_DEVICE_ID);

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
			
			if(actionFlag) {
				ObjectMapper mapper = new ObjectMapper();
				groupIds = mapper.readValue(groupId, new TypeReference<List<String>>(){});
				deviceIds = mapper.readValue(deviceId, new TypeReference<List<String>>(){});

				List<DeviceList> dList = null;
				List<String> deviceListIds = new ArrayList<>();
				List<String> deviceListIps = new ArrayList<>();
				if ((groupIds != null && !groupIds.isEmpty()) || (deviceIds != null && !deviceIds.isEmpty())) {
					deviceDAO = (DeviceDAO)ApplicationContextUtil.getBean("deviceDAO");
					dList = deviceDAO.findDistinctDeviceListByGroupIdsOrDeviceIds(groupIds, deviceIds);

					if (dList != null && !dList.isEmpty()) {
						for (DeviceList d : dList) {
							deviceListIds.add(d.getDeviceListId());
							deviceListIps.add(d.getDeviceIp());
						}
					}
				}

				if (deviceListIds != null && !deviceListIds.isEmpty()) {
					String triggerBy = SecurityUtil.getSecurityUser() == null ? "JOB" : SecurityUtil.getSecurityUser().getUsername();

			        List<ScriptServiceVO> cmdList = new ArrayList<>();

			        //show ip interface brief | include Tunnel|Serial
			        ScriptServiceVO sVO = new ScriptServiceVO();
			        sVO.setScriptContent("show ip interface brief | include Vlan|Te1");
			        sVO.setExpectedTerminalSymbol("#");
			        cmdList.add(sVO);
			        
			        sVO = new ScriptServiceVO();
			        sVO.setScriptContent(" ");
			        sVO.setExpectedTerminalSymbol("#");
			        sVO.setOutput(Constants.DATA_Y);
			        sVO.setHeadCuttingLines("1");
			        sVO.setTailCuttingLines("1");
			        cmdList.add(sVO);
			        
			        StepServiceVO retVO;
			        List<ModuleCircuitDiagramSetting> entities = new ArrayList<>();
			        Map<String, ModuleCircuitDiagramSetting> settingMap;
			        ModuleCircuitDiagramSetting newEntity = null;
			        int count = 0;			        
			        String currIp, currKey;
			        
			        circuitService = (CircuitService)ApplicationContextUtil.getBean("circuitService");
			        stepService = (StepService)ApplicationContextUtil.getBean("stepService");
			        for(String currId : deviceListIds) {
			        	currIp = deviceListIps.get(count);
			        	
			        	settingMap = new HashMap<String, ModuleCircuitDiagramSetting>();
			        	List<ModuleCircuitDiagramSetting> settings = circuitService.findModuleCircuitDiagramInfoSetting(currIp);
			        	for(ModuleCircuitDiagramSetting setting:settings) {
//			        		settingMap.put(setting.getSettingValue(), setting);
			        	}
			        	retVO = stepService.doCommands(ConnectionMode.SSH, currId, null, cmdList, false, triggerBy, "");
			        	
			        	if(StringUtils.isNotBlank(retVO.getCmdProcessLog())) {
			        		String[] result = retVO.getCmdProcessLog().trim().split("\r\n");
			        		
			        		for(String curLine : result) {
			        			if(StringUtils.startsWithIgnoreCase(curLine, "Vlan")) {//Tunnel
			        				currKey = curLine.substring(0, curLine.indexOf(" "));
			        				if(!settingMap.containsKey(currKey)) {
			        					newEntity = newSettingEntity(currIp, currKey);
				        				newEntity.setSettingName("Tunnel");
				        				entities.add(newEntity);
			        				} else {
			        					settingMap.remove(currKey);
			        				}
			        				
			        			}else if(StringUtils.startsWithIgnoreCase(curLine, "Te1")) {//Serial
			        				currKey = curLine.substring(0, curLine.indexOf(" "));
			        				if(!settingMap.containsKey(currKey)) {
			        					newEntity = newSettingEntity(currIp, currKey);
				        				newEntity.setSettingName("Serial");
				        				entities.add(newEntity);
			        				} else {
			        					settingMap.remove(currKey);
			        				}
			        			}
			        			
			        		}
			        		
			        		if(!settingMap.isEmpty()) {
			        			for(String key :settingMap.keySet()) {
			        				newEntity = settingMap.get(key);
			        				newEntity.setDeleteFlag(Constants.DATA_N);
			        				newEntity.setUpdateTime(new Timestamp((new Date()).getTime()));
			        				newEntity.setUpdateBy(Env.USER_NAME_JOB);
			        				entities.add(newEntity);
			        			}
			        		}
			        		
			        	}
			        	count ++;
			        }
			        
			        if(!entities.isEmpty()) {
			        	circuitService.saveOrUpdateSetting(entities);
			        }			        
				}
			}			

		} catch (Exception e) {
			log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

		} finally {
			if(actionFlag) {
				Timestamp endTime = new Timestamp((new Date()).getTime());

				super.insertSysJobLog(JOB_ID, context, vsVO.getJobExcuteResult(), vsVO.getJobExcuteResultRecords(), startTime, endTime, vsVO.getJobExcuteRemark());
			}			
		}
    }
    
    private ModuleCircuitDiagramSetting newSettingEntity(String ip, String val) {
    	Timestamp currTimestamp =  new Timestamp((new Date()).getTime());
    	ModuleCircuitDiagramSetting newEntity = new ModuleCircuitDiagramSetting();
		newEntity.setE1Ip(ip);			        				
		newEntity.setSettingValue(val);
		newEntity.setDeleteFlag(Constants.DATA_N);
		newEntity.setCreateTime(currTimestamp);
		newEntity.setCreateBy(Env.USER_NAME_JOB);
		newEntity.setUpdateTime(currTimestamp);
		newEntity.setUpdateBy(Env.USER_NAME_JOB);
		
		return newEntity;
    }
}
