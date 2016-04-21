// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InvokerWrapper.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;

public class InvokerWrapper
	implements Invoker
{

	private final Invoker invoker;
	private final URL url;

	public InvokerWrapper(Invoker invoker, URL url)
	{
		this.invoker = invoker;
		this.url = url;
	}

	public Class getInterface()
	{
		return invoker.getInterface();
	}

	public URL getUrl()
	{
		return url;
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
