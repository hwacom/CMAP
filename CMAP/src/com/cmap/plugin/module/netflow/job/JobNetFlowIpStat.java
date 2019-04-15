package com.cmap.plugin.module.netflow.job;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cmap.plugin.module.netflow.NetFlowService;
import com.cmap.plugin.module.netflow.NetFlowVO;
import com.cmap.service.BaseJobService;
import com.cmap.service.impl.jobs.BaseJobImpl;
import com.cmap.service.impl.jobs.JobDataPoller;
import com.cmap.utils.impl.ApplicationContextUtil;

@DisallowConcurrentExecution
public class JobNetFlowIpStat extends BaseJobImpl implements BaseJobService {
    private static Logger log = LoggerFactory.getLogger(JobDataPoller.class);

    private NetFlowService netFlowService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final String JOB_ID = UUID.randomUUID().toString();
        Timestamp startTime = new Timestamp((new Date()).getTime());
        NetFlowVO nfVO = new NetFlowVO();

        netFlowService = (NetFlowService)ApplicationContextUtil.getBean("netFlowService");

        try {
            nfVO = netFlowService.executeNetFlowIpStat();

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
