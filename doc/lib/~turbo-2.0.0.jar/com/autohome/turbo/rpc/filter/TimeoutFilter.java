// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TimeoutFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.*;
import java.util.Arrays;

public class TimeoutFilter
	implements Filter
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/filter/TimeoutFilter);

	public TimeoutFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		long start = System.currentTimeMillis();
		Result result = invoker.invoke(invocation);
		long elapsed = System.currentTimeMillis() - start;
		if (invoker.getUrl() != null && elapsed > (long)invoker.getUrl().getMethodParameter(invocation.getMethodName(), "timeout", 0x7fffffff) && logger.isWarnEnabled())
			logger.warn((new StringBuilder()).append("invoke time out. method: ").append(invocation.getMethodName()).append("arguments: ").append(Arrays.toString(invocation.getArguments())).append(" , url is ").append(invoker.getUrl()).append(", invoke elapsed ").append(elapsed).append(" ms.").toString());
		return result;
	}

}
