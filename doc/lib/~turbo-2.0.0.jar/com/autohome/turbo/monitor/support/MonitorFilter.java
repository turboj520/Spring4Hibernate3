// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MonitorFilter.java

package com.autohome.turbo.monitor.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.monitor.Monitor;
import com.autohome.turbo.monitor.MonitorFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MonitorFilter
	implements Filter
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/monitor/support/MonitorFilter);
	private final ConcurrentMap concurrents = new ConcurrentHashMap();
	private MonitorFactory monitorFactory;

	public MonitorFilter()
	{
	}

	public void setMonitorFactory(MonitorFactory monitorFactory)
	{
		this.monitorFactory = monitorFactory;
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		Exception exception;
		if (invoker.getUrl().hasParameter("monitor"))
		{
			RpcContext context = RpcContext.getContext();
			long start = System.currentTimeMillis();
			getConcurrent(invoker, invocation).incrementAndGet();
			Result result1;
			try
			{
				Result result = invoker.invoke(invocation);
				collect(invoker, invocation, result, context, start, false);
				result1 = result;
			}
			catch (RpcException e)
			{
				collect(invoker, invocation, null, context, start, true);
				throw e;
			}
			finally
			{
				getConcurrent(invoker, invocation).decrementAndGet();
			}
			getConcurrent(invoker, invocation).decrementAndGet();
			return result1;
		} else
		{
			return invoker.invoke(invocation);
		}
		throw exception;
	}

	private void collect(Invoker invoker, Invocation invocation, Result result, RpcContext context, long start, boolean error)
	{
		try
		{
			long elapsed = System.currentTimeMillis() - start;
			int concurrent = getConcurrent(invoker, invocation).get();
			String application = invoker.getUrl().getParameter("application");
			String service = invoker.getInterface().getName();
			String method = RpcUtils.getMethodName(invocation);
			URL url = invoker.getUrl().getUrlParameter("monitor");
			Monitor monitor = monitorFactory.getMonitor(url);
			int localPort;
			String remoteKey;
			String remoteValue;
			if ("consumer".equals(invoker.getUrl().getParameter("side")))
			{
				context = RpcContext.getContext();
				localPort = 0;
				remoteKey = "provider";
				remoteValue = invoker.getUrl().getAddress();
			} else
			{
				localPort = invoker.getUrl().getPort();
				remoteKey = "consumer";
				remoteValue = context.getRemoteHost();
			}
			String input = "";
			String output = "";
			if (invocation.getAttachment("input") != null)
				input = invocation.getAttachment("input");
			if (result != null && result.getAttachment("output") != null)
				output = result.getAttachment("output");
			monitor.collect(new URL("count", NetUtils.getLocalHost(), localPort, (new StringBuilder()).append(service).append("/").append(method).toString(), new String[] {
				"application", application, "interface", service, "method", method, remoteKey, remoteValue, error ? "failure" : "success", "1", 
				"elapsed", String.valueOf(elapsed), "concurrent", String.valueOf(concurrent), "input", input, "output", output
			}));
		}
		catch (Throwable t)
		{
			logger.error((new StringBuilder()).append("Failed to monitor count service ").append(invoker.getUrl()).append(", cause: ").append(t.getMessage()).toString(), t);
		}
	}

	private AtomicInteger getConcurrent(Invoker invoker, Invocation invocation)
	{
		String key = (new StringBuilder()).append(invoker.getInterface().getName()).append(".").append(invocation.getMethodName()).toString();
		AtomicInteger concurrent = (AtomicInteger)concurrents.get(key);
		if (concurrent == null)
		{
			concurrents.putIfAbsent(key, new AtomicInteger());
			concurrent = (AtomicInteger)concurrents.get(key);
		}
		return concurrent;
	}

}
