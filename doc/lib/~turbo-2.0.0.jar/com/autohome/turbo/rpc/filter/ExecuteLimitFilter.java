// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExecuteLimitFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;

public class ExecuteLimitFilter
	implements Filter
{

	public ExecuteLimitFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		Exception exception;
		URL url = invoker.getUrl();
		String methodName = invocation.getMethodName();
		int max = url.getMethodParameter(methodName, "executes", 0);
		if (max > 0)
		{
			RpcStatus count = RpcStatus.getStatus(url, invocation.getMethodName());
			if (count.getActive() >= max)
				throw new RpcException((new StringBuilder()).append("Failed to invoke method ").append(invocation.getMethodName()).append(" in provider ").append(url).append(", cause: The service using threads greater than <dubbo:service executes=\"").append(max).append("\" /> limited.").toString());
		}
		long begin = System.currentTimeMillis();
		boolean isException = false;
		RpcStatus.beginCount(url, methodName);
		Result result1;
		try
		{
			Result result = invoker.invoke(invocation);
			result1 = result;
		}
		catch (Throwable t)
		{
			isException = true;
			if (t instanceof RuntimeException)
				throw (RuntimeException)t;
			else
				throw new RpcException("unexpected exception when ExecuteLimitFilter", t);
		}
		finally
		{
			RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, isException);
		}
		RpcStatus.endCount(url, methodName, System.currentTimeMillis() - begin, isException);
		return result1;
		throw exception;
	}
}
