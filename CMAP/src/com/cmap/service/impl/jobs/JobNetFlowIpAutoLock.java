package com.cmap.service.impl.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import com.cmap.plugin.module.netflow.NetFlowVO;
import com.cmap.plugin.module.netflow.statistics.NetFlowStatisticsService;
import com.cmap.service.BaseJobService;
import com.cmap.service.CommonService;
import com.cmap.service.DeliveryService;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.utils.impl.ApplicationContextUtil;

@DisallowConcurrentExecution
public class JobNetFlowIpAutoLock extends BaseJobImpl implements BaseJobService {
    private static Logger log = LoggerFactory.getLogger(JobNetFlowIpAutoLock.class);

    private NetFlowStatisticsService netFlowStatisticsService;

	private DeliveryService deliveryService;
    
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
	        NetFlowVO nfVO = new NetFlowVO();

	        netFlowStatisticsService = (NetFlowStatisticsService)ApplicationContextUtil.getBean("netFlowStatisticsService");
	        deliveryService = (DeliveryService)ApplicationContextUtil.getBean("deliveryService");
	        commonService = (CommonService)ApplicationContextUtil.getBean("commonService");
	        
	        try {
	        	List<Object[]> ipList = netFlowStatisticsService.executeNetFlowIpAutoLock();
	        	
	            final String limitSizeUnit = commonService.convertByteSizeUnit(
	                    new BigDecimal(Env.ABNORMAL_NET_FLOW_LIMIT_BLOCK_SIZE), Env.NET_FLOW_LIMIT_BLOCK_UNIT_OF_TOTOAL_FLOW).replace(" ", "");
	            
	            // 定義IP封鎖腳本中「IP_Address」的變數名稱 for 寫入異動紀錄table使用
	            String ipAddressVarKey = Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
	            
	        	String reason ="該IP流量超過限制 "+limitSizeUnit+" ，系統自動鎖定";
	        	DeliveryParameterVO dpVO ;
	        	
				for (Object[] ipRecord : ipList) {
					
					String groupId = Objects.toString(ipRecord[1], "");
					String deviceId = Objects.toString(ipRecord[2], "");
					
					try {
						dpVO = new DeliveryParameterVO();
						dpVO.setDeviceId(Arrays.asList(deviceId));
						dpVO.setScriptCode(Env.DELIVERY_IP_AUTO_BLOCK_SCRIPT_CODE);
						dpVO.setVarKey(Arrays.asList(ipAddressVarKey));
						dpVO.setVarValue(Arrays.asList(Arrays.asList(Objects.toString(ipRecord[0], ""))));
						dpVO.setReason(reason);
		
						deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, Constants.SYS, reason, false);
		
					} catch (Exception e) {
						log.error(e.toString(), e);
		
						// 設備執行腳本失敗則跳過此設備
						break;
					}
					
				}
	            
	    		
	        } catch (Exception e) {
	            log.error("JID:["+JOB_ID+"] >> "+e.toString(), e);

	            nfVO.setJobExcuteResult(Result.FAILED);
	            nfVO.setJobExcuteResultRecords("0");
	            nfVO.setJobExcuteRemark(e.getMessage() + ", JID:["+JOB_ID+"]");

	        } finally {
	            Timestamp endTime = new Timestamp((new Date()).getTime());

	            super.insertSysJobLog(JOB_ID, context, nfVO.getJobExcuteResult(), nfVO.getJobExcuteResultRecords(), startTime, endTime, nfVO.getJobExcuteRemark());
	        }
		}        
    }
    
}
