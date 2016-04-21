// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EchoFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.rpc.*;

public class EchoFilter
	implements Filter
{

	public EchoFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation inv)
		throws RpcException
	{
		if (inv.getMethodName().equals("$echo") && inv.getArguments() != null && inv.getArguments().length == 1)
			return new RpcResult(inv.getArguments()[0]);
		else
			return invoker.invoke(inv);
	}
}
