// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExecutorUtil.java

package com.autohome.turbo.common.utils;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.common.utils:
//			NamedThreadFactory

public class ExecutorUtil
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/utils/ExecutorUtil);
	private static final ThreadPoolExecutor shutdownExecutor;

	public ExecutorUtil()
	{
	}

	public static boolean isShutdown(Executor executor)
	{
		return (executor instanceof ExecutorService) && ((ExecutorService)executor).isShutdown();
	}

	public static void gracefulShutdown(Executor executor, int timeout)
	{
		if (!(executor instanceof ExecutorService) || isShutdown(executor))
			return;
		ExecutorService es = (ExecutorService)executor;
		try
		{
			es.shutdown();
		}
		catch (SecurityException ex2)
		{
			return;
		}
		catch (NullPointerException ex2)
		{
			return;
		}
		try
		{
			if (!es.awaitTermination(timeout, TimeUnit.MILLISECONDS))
				es.shutdownNow();
		}
		catch (InterruptedException ex)
		{
			es.shutdownNow();
			Thread.currentThread().interrupt();
		}
		if (!isShutdown(es))
			newThreadToCloseExecutor(es);
	}

	public static void shutdownNow(Executor executor, int timeout)
	{
		if (!(executor instanceof ExecutorService) || isShutdown(executor))
			return;
		ExecutorService es = (ExecutorService)executor;
		try
		{
			es.shutdownNow();
		}
		catch (SecurityException ex2)
		{
			return;
		}
		catch (NullPointerException ex2)
		{
			return;
		}
		try
		{
			es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
		if (!isShutdown(es))
			newThreadToCloseExecutor(es);
	}

	private static void newThreadToCloseExecutor(ExecutorService es)
	{
		if (!isShutdown(es))
			shutdownExecutor.execute(new Runnable(es) {

				final ExecutorService val$es;

				public void run()
				{
					try
					{
						int i = 0;
						do
						{
							if (i >= 1000)
								break;
							es.shutdownNow();
							if (es.awaitTermination(10L, TimeUnit.MILLISECONDS))
								break;
							i++;
						} while (true);
					}
					catch (InterruptedException ex)
					{
						Thread.currentThread().interrupt();
					}
					catch (Throwable e)
					{
						ExecutorUtil.logger.warn(e.getMessage(), e);
					}
				}

			
			{
				es = executorservice;
				super();
			}
			});
	}

	public static URL setThreadName(URL url, String defaultName)
	{
		String name = url.getParameter("threadname", defaultName);
		name = (new StringBuilder(32)).append(name).append("-").append(url.getAddress()).toString();
		url = url.addParameter("threadname", name);
		return url;
	}

	static 
	{
		shutdownExecutor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(100), new NamedThreadFactory("Close-ExecutorService-Timer", true));
	}

}
