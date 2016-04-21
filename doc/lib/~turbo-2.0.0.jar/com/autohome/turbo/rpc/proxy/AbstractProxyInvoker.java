// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractProxyInvoker.java

package com.autohome.turbo.rpc.proxy;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractProxyInvoker
	implements Invoker
{

	private final Object proxy;
	private final Class type;
	private final URL url;

	public AbstractProxyInvoker(Object proxy, Class type, URL url)
	{
		if (proxy == null)
			throw new IllegalArgumentException("proxy == null");
		if (type == null)
			throw new IllegalArgumentException("interface == null");
		if (!type.isInstance(proxy))
		{
			throw new IllegalArgumentException((new StringBuilder()).append(proxy.getClass().getName()).append(" not implement interface ").append(type).toString());
		} else
		{
			this.proxy = proxy;
			this.type = type;
			this.url = url;
			return;
		}
	}

	public Class getInterface()
	{
		return type;
	}

	public URL getUrl()
	{
		return url;
	}

	public boolean isAvailable()
	{
		return true;
	}

	public void destroy()
	{
	}

	public Result invoke(Invocation invocation)
		throws RpcException
	{
		return new RpcResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
		InvocationTargetException e;
		e;
		return new RpcResult(e.getTargetException());
		e;
		throw new RpcException((new StringBuilder()).append("Failed to invoke remote proxy method ").append(invocation.getMethodName()).append(" to ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
	}

	protected abstract Object doInvoke(Object obj, String s, Class aclass[], Object aobj[])
		throws Throwable;

	public String toString()
	{
		return (new StringBuilder()).append(getInterface()).append(" -> ").append(getUrl()).toString() != null ? getUrl().toString() : " ";
	}
}
