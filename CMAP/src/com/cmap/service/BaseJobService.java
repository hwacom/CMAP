package com.cmap.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface BaseJobService extends Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException;

}
