// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboMonitor.java

package com.autohome.turbo.monitor.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.monitor.Monitor;
import com.autohome.turbo.monitor.MonitorService;
import com.autohome.turbo.rpc.Invoker;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.autohome.turbo.monitor.dubbo:
//			Statistics

public class DubboMonitor
	implements Monitor
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/monitor/dubbo/DubboMonitor);
	private static final int LENGTH = 10;
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3, new NamedThreadFactory("DubboMonitorSendTimer", true));
	private final ScheduledFuture sendFuture;
	private final Invoker monitorInvoker;
	private final MonitorService monitorService;
	private final long monitorInterval;
	private final ConcurrentMap statisticsMap = new ConcurrentHashMap();

	public DubboMonitor(Invoker monitorInvoker, MonitorService monitorService)
	{
		this.monitorInvoker = monitorInvoker;
		this.monitorService = monitorService;
		monitorInterval = monitorInvoker.getUrl().getPositiveParameter("interval", 60000);
		sendFuture = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

			final DubboMonitor this$0;

			public void run()
			{
				try
				{
					send();
				}
				catch (Throwable t)
				{
					DubboMonitor.logger.error((new StringBuilder()).append("Unexpected error occur at send statistic, cause: ").append(t.getMessage()).toString(), t);
				}
			}

			
			{
				this$0 = DubboMonitor.this;
				super();
			}
		}, monitorInterval, monitorInterval, TimeUnit.MILLISECONDS);
	}

	public void send()
	{
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Send statistics to monitor ").append(getUrl()).toString());
		String timestamp = String.valueOf(System.currentTimeMillis());
		for (Iterator i$ = statisticsMap.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			Statistics statistics = (Statistics)entry.getKey();
			AtomicReference reference = (AtomicReference)entry.getValue();
			long numbers[] = (long[])reference.get();
			long success = numbers[0];
			long failure = numbers[1];
			long input = numbers[2];
			long output = numbers[3];
			long elapsed = numbers[4];
			long concurrent = numbers[5];
			long maxInput = numbers[6];
			long maxOutput = numbers[7];
			long maxElapsed = numbers[8];
			long maxConcurrent = numbers[9];
			URL url = statistics.getUrl().addParameters(new String[] {
				"timestamp", timestamp, "success", String.valueOf(success), "failure", String.valueOf(failure), "input", String.valueOf(input), "output", String.valueOf(output), 
				"elapsed", String.valueOf(elapsed), "concurrent", String.valueOf(concurrent), "max.input", String.valueOf(maxInput), "max.output", String.valueOf(maxOutput), "max.elapsed", String.valueOf(maxElapsed), 
				"max.concurrent", String.valueOf(maxConcurrent)
			});
			monitorService.collect(url);
			long update[] = new long[10];
			long current[];
			do
			{
				current = (long[])reference.get();
				if (current == null)
				{
					update[0] = 0L;
					update[1] = 0L;
					update[2] = 0L;
					update[3] = 0L;
					update[4] = 0L;
					update[5] = 0L;
				} else
				{
					update[0] = current[0] - success;
					update[1] = current[1] - failure;
					update[2] = current[2] - input;
					update[3] = current[3] - output;
					update[4] = current[4] - elapsed;
					update[5] = current[5] - concurrent;
				}
			} while (!reference.compareAndSet(current, update));
		}

	}

	public void collect(URL url)
	{
		int success = url.getParameter("success", 0);
		int failure = url.getParameter("failure", 0);
		int input = url.getParameter("input", 0);
		int output = url.getParameter("output", 0);
		int elapsed = url.getParameter("elapsed", 0);
		int concurrent = url.getParameter("concurrent", 0);
		Statistics statistics = new Statistics(url);
		AtomicReference reference = (AtomicReference)statisticsMap.get(statistics);
		if (reference == null)
		{
			statisticsMap.putIfAbsent(statistics, new AtomicReference());
			reference = (AtomicReference)statisticsMap.get(statistics);
		}
		long update[] = new long[10];
		long current[];
		do
		{
			current = (long[])reference.get();
			if (current == null)
			{
				update[0] = success;
				update[1] = failure;
				update[2] = input;
				update[3] = output;
				update[4] = elapsed;
				update[5] = concurrent;
				update[6] = input;
				update[7] = output;
				update[8] = elapsed;
				update[9] = concurrent;
			} else
			{
				update[0] = current[0] + (long)success;
				update[1] = current[1] + (long)failure;
				update[2] = current[2] + (long)input;
				update[3] = current[3] + (long)output;
				update[4] = current[4] + (long)elapsed;
				update[5] = (current[5] + (long)concurrent) / 2L;
				update[6] = current[6] <= (long)input ? input : current[6];
				update[7] = current[7] <= (long)output ? output : current[7];
				update[8] = current[8] <= (long)elapsed ? elapsed : current[8];
				update[9] = current[9] <= (long)concurrent ? concurrent : current[9];
			}
		} while (!reference.compareAndSet(current, update));
	}

	public List lookup(URL query)
	{
		return monitorService.lookup(query);
	}

	public URL getUrl()
	{
		return monitorInvoker.getUrl();
	}

	public boolean isAvailable()
	{
		return monitorInvoker.isAvailable();
	}

	public void destroy()
	{
		try
		{
			sendFuture.cancel(true);
		}
		catch (Throwable t)
		{
			logger.error((new StringBuilder()).append("Unexpected error occur at cancel sender timer, cause: ").append(t.getMessage()).toString(), t);
		}
		monitorInvoker.destroy();
	}


}
