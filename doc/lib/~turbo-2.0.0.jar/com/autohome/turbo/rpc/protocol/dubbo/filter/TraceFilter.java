// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TraceFilter.java

package com.autohome.turbo.rpc.protocol.dubbo.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.rpc.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceFilter
	implements Filter
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/filter/TraceFilter);
	private static final String TRACE_MAX = "trace.max";
	private static final String TRACE_COUNT = "trace.count";
	private static final ConcurrentMap tracers = new ConcurrentHashMap();

	public TraceFilter()
	{
	}

	public static void addTracer(Class type, String method, Channel channel, int max)
	{
		channel.setAttribute("trace.max", Integer.valueOf(max));
		channel.setAttribute("trace.count", new AtomicInteger());
		String key = method == null || method.length() <= 0 ? type.getName() : (new StringBuilder()).append(type.getName()).append(".").append(method).toString();
		Set channels = (Set)tracers.get(key);
		if (channels == null)
		{
			tracers.putIfAbsent(key, new ConcurrentHashSet());
			channels = (Set)tracers.get(key);
		}
		channels.add(channel);
	}

	public static void removeTracer(Class type, String method, Channel channel)
	{
		channel.removeAttribute("trace.max");
		channel.removeAttribute("trace.count");
		String key = method == null || method.length() <= 0 ? type.getName() : (new StringBuilder()).append(type.getName()).append(".").append(method).toString();
		Set channels = (Set)tracers.get(key);
		if (channels != null)
			channels.remove(channel);
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		long start = System.currentTimeMillis();
		Result result = invoker.invoke(invocation);
		long end = System.currentTimeMillis();
		if (tracers.size() > 0)
		{
			String key = (new StringBuilder()).append(invoker.getInterface().getName()).append(".").append(invocation.getMethodName()).toString();
			Set channels = (Set)tracers.get(key);
			if (channels == null || channels.size() == 0)
			{
				key = invoker.getInterface().getName();
				channels = (Set)tracers.get(key);
			}
			if (channels != null && channels.size() > 0)
			{
				Iterator i$ = (new ArrayList(channels)).iterator();
				do
				{
					if (!i$.hasNext())
						break;
					Channel channel = (Channel)i$.next();
					if (channel.isConnected())
						try
						{
							int max = 1;
							Integer m = (Integer)channel.getAttribute("trace.max");
							if (m != null)
								max = m.intValue();
							int count = 0;
							AtomicInteger c = (AtomicInteger)channel.getAttribute("trace.count");
							if (c == null)
							{
								c = new AtomicInteger();
								channel.setAttribute("trace.count", c);
							}
							count = c.getAndIncrement();
							if (count < max)
							{
								String prompt = channel.getUrl().getParameter("prompt", "dubbo>");
								channel.send((new StringBuilder()).append("\r\n").append(RpcContext.getContext().getRemoteAddress()).append(" -> ").append(invoker.getInterface().getName()).append(".").append(invocation.getMethodName()).append("(").append(JSON.json(((Object) (invocation.getArguments())))).append(")").append(" -> ").append(JSON.json(result.getValue())).append("\r\nelapsed: ").append(end - start).append(" ms.").append("\r\n\r\n").append(prompt).toString());
							}
							if (count >= max - 1)
								channels.remove(channel);
						}
						catch (Throwable e)
						{
							channels.remove(channel);
							logger.warn(e.getMessage(), e);
						}
					else
						channels.remove(channel);
				} while (true);
			}
		}
		return result;
	}

}
