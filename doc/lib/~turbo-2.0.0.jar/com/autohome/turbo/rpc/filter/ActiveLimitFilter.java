// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ActiveLimitFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;

public class ActiveLimitFilter
	implements Filter
{

	public ActiveLimitFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		URL url;
		String methodName;
		int max;
		RpcStatus count;
		url = invoker.getUrl();
		methodName = invocation.getMethodName();
		max = invoker.getUrl().getMethodParameter(methodName, "actives", 0);
		count = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName());
		if (max > 0)
		{
			long timeout = invoker.getUrl().getMethodParameter(invocation.getMethodName(), "timeout", 0);
			long start = System.currentTimeMillis();
			long remain = timeout;
			int active = count.getActive();
			if (active >= max)
				synchronized (count)
				{
					while ((active = count.getActive()) >= max) 
					{
						try
						{
							count.wait(remain);
						}
						catch (InterruptedException e) { }
						long elapsed = System.currentTimeMillis() - start;
						remain = timeout - elapsed;
						if (remain <= 0L)
							throw new RpcException((new StringBuilder()).append("Waiting concurrent invoke timeout in client-side for service:  ").append(invoker.getInterface().getName()).append(", method: ").append(invocation.getMethodName()).append(", elapsed: ").append(elapsed).append(", timeout: ").append(timeout).append(". concurrent invokes: ").append(active).append(". max concurrent invoke limit: ").append(max).toString());
					}
				}
		}
		long begin;
		begin = System.currentTimeMillis();
		RpcStatus.beginCount(url, methodName);
		Result result1;
		try
		{
			Result result = invoker.invoke(invocation);
			RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, true);
			result1 = result;
		}
		catch (RuntimeException t)
		{
			RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, false);
			throw t;
		}
		if (max > 0)
			synchronized (count)
			{
				count.notify();
			}
		return result1;
		Exception exception2;
		exception2;
		if (max > 0)
			synchronized (count)
			{
				count.notify();
			}
		throw exception2;
	}
}
