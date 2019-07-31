package com.cmap.plugin.module.ip.mapping.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cmap.service.BaseJobService;
import com.cmap.service.impl.jobs.BaseJobImpl;

@DisallowConcurrentExecution
public class JobIpMacPortMappingPoller extends BaseJobImpl implements BaseJobService {
    private static Logger log = LoggerFactory.getLogger(JobIpMacPortMappingPoller.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // TODO 自動產生的方法 Stub

    }
}
