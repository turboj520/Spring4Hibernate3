// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AccessLogFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.rpc.*;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class AccessLogFilter
	implements Filter
{
	private class LogTask
		implements Runnable
	{

		final AccessLogFilter this$0;

		public void run()
		{
			if (logQueue == null || logQueue.size() <= 0) goto _L2; else goto _L1
_L1:
			Iterator i$ = logQueue.entrySet().iterator();
_L3:
			java.util.Map.Entry entry;
			if (!i$.hasNext())
				break; /* Loop/switch isn't completed */
			entry = (java.util.Map.Entry)i$.next();
			Set logSet;
			FileWriter writer;
			String accesslog = (String)entry.getKey();
			logSet = (Set)entry.getValue();
			File file = new File(accesslog);
			File dir = file.getParentFile();
			if (null != dir && !dir.exists())
				dir.mkdirs();
			if (AccessLogFilter.logger.isDebugEnabled())
				AccessLogFilter.logger.debug((new StringBuilder()).append("Append log to ").append(accesslog).toString());
			if (file.exists())
			{
				String now = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
				String last = (new SimpleDateFormat("yyyyMMdd")).format(new Date(file.lastModified()));
				if (!now.equals(last))
				{
					File archive = new File((new StringBuilder()).append(file.getAbsolutePath()).append(".").append(last).toString());
					file.renameTo(archive);
				}
			}
			writer = new FileWriter(file, true);
			for (Iterator iterator = logSet.iterator(); iterator.hasNext(); iterator.remove())
			{
				writer.write((String)iterator.next());
				writer.write("\r\n");
			}

			writer.flush();
			writer.close();
			continue; /* Loop/switch isn't completed */
			Exception exception;
			exception;
			writer.close();
			throw exception;
			Exception e;
			e;
			AccessLogFilter.logger.error(e.getMessage(), e);
			if (true) goto _L3; else goto _L2
			Exception e;
			e;
			AccessLogFilter.logger.error(e.getMessage(), e);
_L2:
		}

		private LogTask()
		{
			this$0 = AccessLogFilter.this;
			super();
		}

	}


	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/filter/AccessLogFilter);
	private static final String ACCESS_LOG_KEY = "dubbo.accesslog";
	private static final String FILE_DATE_FORMAT = "yyyyMMdd";
	private static final String MESSAGE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final int LOG_MAX_BUFFER = 5000;
	private static final long LOG_OUTPUT_INTERVAL = 5000L;
	private final ConcurrentMap logQueue = new ConcurrentHashMap();
	private final ScheduledExecutorService logScheduled = Executors.newScheduledThreadPool(2, new NamedThreadFactory("Dubbo-Access-Log", true));
	private volatile ScheduledFuture logFuture;

	public AccessLogFilter()
	{
		logFuture = null;
	}

	private void init()
	{
		if (logFuture == null)
			synchronized (logScheduled)
			{
				if (logFuture == null)
					logFuture = logScheduled.scheduleWithFixedDelay(new LogTask(), 5000L, 5000L, TimeUnit.MILLISECONDS);
			}
	}

	private void log(String accesslog, String logmessage)
	{
		init();
		Set logSet = (Set)logQueue.get(accesslog);
		if (logSet == null)
		{
			logQueue.putIfAbsent(accesslog, new ConcurrentHashSet());
			logSet = (Set)logQueue.get(accesslog);
		}
		if (logSet.size() < 5000)
			logSet.add(logmessage);
	}

	public Result invoke(Invoker invoker, Invocation inv)
		throws RpcException
	{
		try
		{
			String accesslog = invoker.getUrl().getParameter("accesslog");
			if (ConfigUtils.isNotEmpty(accesslog))
			{
				RpcContext context = RpcContext.getContext();
				String serviceName = invoker.getInterface().getName();
				String version = invoker.getUrl().getParameter("version");
				String group = invoker.getUrl().getParameter("group");
				StringBuilder sn = new StringBuilder();
				sn.append("[").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())).append("] ").append(context.getRemoteHost()).append(":").append(context.getRemotePort()).append(" -> ").append(context.getLocalHost()).append(":").append(context.getLocalPort()).append(" - ");
				if (null != group && group.length() > 0)
					sn.append(group).append("/");
				sn.append(serviceName);
				if (null != version && version.length() > 0)
					sn.append(":").append(version);
				sn.append(" ");
				sn.append(inv.getMethodName());
				sn.append("(");
				Class types[] = inv.getParameterTypes();
				if (types != null && types.length > 0)
				{
					boolean first = true;
					Class arr$[] = types;
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						Class type = arr$[i$];
						if (first)
							first = false;
						else
							sn.append(",");
						sn.append(type.getName());
					}

				}
				sn.append(") ");
				Object args[] = inv.getArguments();
				if (args != null && args.length > 0)
					sn.append(JSON.json(((Object) (args))));
				String msg = sn.toString();
				if (ConfigUtils.isDefault(accesslog))
					LoggerFactory.getLogger((new StringBuilder()).append("dubbo.accesslog.").append(invoker.getInterface().getName()).toString()).info(msg);
				else
					log(accesslog, msg);
			}
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("Exception in AcessLogFilter of service(").append(invoker).append(" -> ").append(inv).append(")").toString(), t);
		}
		return invoker.invoke(inv);
	}



}
