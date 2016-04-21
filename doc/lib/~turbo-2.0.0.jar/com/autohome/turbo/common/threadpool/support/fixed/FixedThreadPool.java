// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FixedThreadPool.java

package com.autohome.turbo.common.threadpool.support.fixed;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.threadpool.ThreadPool;
import com.autohome.turbo.common.threadpool.support.AbortPolicyWithReport;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import java.util.AbstractQueue;
import java.util.concurrent.*;

public class FixedThreadPool
	implements ThreadPool
{

	public FixedThreadPool()
	{
	}

	public Executor getExecutor(URL url)
	{
		String name = url.getParameter("threadname", "Dubbo");
		int threads = url.getParameter("threads", 200);
		int queues = url.getParameter("queues", 0);
		return new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, (BlockingQueue)(queues != 0 ? queues >= 0 ? new LinkedBlockingQueue(queues) : new LinkedBlockingQueue() : new SynchronousQueue()), new NamedThreadFactory(name, true), new AbortPolicyWithReport(name, url));
	}
}
