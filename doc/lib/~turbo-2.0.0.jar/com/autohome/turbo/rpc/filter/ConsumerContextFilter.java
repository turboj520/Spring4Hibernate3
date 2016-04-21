// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConsumerContextFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.rpc.*;

public class ConsumerContextFilter
	implements Filter
{

	public ConsumerContextFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		RpcContext.getContext().setInvoker(invoker).setInvocation(invocation).setLocalAddress(NetUtils.getLocalHost(), 0).setRemoteAddress(invoker.getUrl().getHost(), invoker.getUrl().getPort());
		if (invocation instanceof RpcInvocation)
			((RpcInvocation)invocation).setInvoker(invoker);
		Result result = invoker.invoke(invocation);
		RpcContext.getContext().clearAttachments();
		return result;
		Exception exception;
		exception;
		RpcContext.getContext().clearAttachments();
		throw exception;
	}
}
