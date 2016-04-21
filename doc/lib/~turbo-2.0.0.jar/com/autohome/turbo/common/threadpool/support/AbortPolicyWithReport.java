// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbortPolicyWithReport.java

package com.autohome.turbo.common.threadpool.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class AbortPolicyWithReport extends java.util.concurrent.ThreadPoolExecutor.AbortPolicy
{

	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/threadpool/support/AbortPolicyWithReport);
	private final String threadName;
	private final URL url;

	public AbortPolicyWithReport(String threadName, URL url)
	{
		this.threadName = threadName;
		this.url = url;
	}

	public void rejectedExecution(Runnable r, ThreadPoolExecutor e)
	{
		String msg = String.format("Thread pool is EXHAUSTED! Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d), Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in %s://%s:%d!", new Object[] {
			threadName, Integer.valueOf(e.getPoolSize()), Integer.valueOf(e.getActiveCount()), Integer.valueOf(e.getCorePoolSize()), Integer.valueOf(e.getMaximumPoolSize()), Integer.valueOf(e.getLargestPoolSize()), Long.valueOf(e.getTaskCount()), Long.valueOf(e.getCompletedTaskCount()), Boolean.valueOf(e.isShutdown()), Boolean.valueOf(e.isTerminated()), 
			Boolean.valueOf(e.isTerminating()), url.getProtocol(), url.getIp(), Integer.valueOf(url.getPort())
		});
		logger.warn(msg);
		throw new RejectedExecutionException(msg);
	}

}
