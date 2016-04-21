// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DelegateInvoker.java

package com.autohome.turbo.rpc.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;

public abstract class DelegateInvoker
	implements Invoker
{

	protected final Invoker invoker;

	public DelegateInvoker(Invoker invoker)
	{
		this.invoker = invoker;
	}

	public Class getInterface()
	{
		return invoker.getInterface();
	}

	public URL getUrl()
	{
		return invoker.getUrl();
	}

	public boolean isAvailable()
	{
		return invoker.isAvailable();
	}

	public Result invoke(Invocation invocation)
		throws RpcException
	{
		return invoker.invoke(invocation);
	}

	public void destroy()
	{
		invoker.destroy();
	}
}
