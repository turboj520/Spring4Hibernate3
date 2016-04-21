// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TpsLimitFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.filter.tps.DefaultTPSLimiter;
import com.autohome.turbo.rpc.filter.tps.TPSLimiter;

public class TpsLimitFilter
	implements Filter
{

	private final TPSLimiter tpsLimiter = new DefaultTPSLimiter();

	public TpsLimitFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		if (!tpsLimiter.isAllowable(invoker.getUrl(), invocation))
			throw new RpcException((new StringBuilder(64)).append("Failed to invoke service ").append(invoker.getInterface().getName()).append(".").append(invocation.getMethodName()).append(" because exceed max service tps.").toString());
		else
			return invoker.invoke(invocation);
	}
}
