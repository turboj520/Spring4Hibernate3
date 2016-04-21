// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractInvoker.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractInvoker
	implements Invoker
{

	protected final Logger logger;
	private final Class type;
	private final URL url;
	private final Map attachment;
	private volatile boolean available;
	private volatile boolean destroyed;

	public AbstractInvoker(Class type, URL url)
	{
		this(type, url, (Map)null);
	}

	public AbstractInvoker(Class type, URL url, String keys[])
	{
		this(type, url, convertAttachment(url, keys));
	}

	public AbstractInvoker(Class type, URL url, Map attachment)
	{
		logger = LoggerFactory.getLogger(getClass());
		available = true;
		destroyed = false;
		if (type == null)
			throw new IllegalArgumentException("service type == null");
		if (url == null)
		{
			throw new IllegalArgumentException("service url == null");
		} else
		{
			this.type = type;
			this.url = url;
			this.attachment = attachment != null ? Collections.unmodifiableMap(attachment) : null;
			return;
		}
	}

	private static Map convertAttachment(URL url, String keys[])
	{
		if (keys == null || keys.length == 0)
			return null;
		Map attachment = new HashMap();
		String arr$[] = keys;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String key = arr$[i$];
			String value = url.getParameter(key);
			if (value != null && value.length() > 0)
				attachment.put(key, value);
		}

		return attachment;
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
		return available;
	}

	protected void setAvailable(boolean available)
	{
		this.available = available;
	}

	public void destroy()
	{
		if (isDestroyed())
		{
			return;
		} else
		{
			destroyed = true;
			setAvailable(false);
			return;
		}
	}

	public boolean isDestroyed()
	{
		return destroyed;
	}

	public String toString()
	{
		return (new StringBuilder()).append(getInterface()).append(" -> ").append(getUrl() != null ? getUrl().toString() : "").toString();
	}

	public Result invoke(Invocation inv)
		throws RpcException
	{
		RpcInvocation invocation;
		if (destroyed)
			throw new RpcException((new StringBuilder()).append("Rpc invoker for service ").append(this).append(" on consumer ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).append(" is DESTROYED, can not be invoked any more!").toString());
		invocation = (RpcInvocation)inv;
		invocation.setInvoker(this);
		if (attachment != null && attachment.size() > 0)
			invocation.addAttachmentsIfAbsent(attachment);
		Map context = RpcContext.getContext().getAttachments();
		if (context != null)
			invocation.addAttachmentsIfAbsent(context);
		if (getUrl().getMethodParameter(invocation.getMethodName(), "async", false))
			invocation.setAttachment("async", Boolean.TRUE.toString());
		RpcUtils.attachInvocationIdIfAsync(getUrl(), invocation);
		return doInvoke(invocation);
		InvocationTargetException e;
		e;
		Throwable te = e.getTargetException();
		if (te == null)
			return new RpcResult(e);
		if (te instanceof RpcException)
			((RpcException)te).setCode(3);
		return new RpcResult(te);
		e;
		if (e.isBiz())
			return new RpcResult(e);
		else
			throw e;
		e;
		return new RpcResult(e);
	}

	protected abstract Result doInvoke(Invocation invocation)
		throws Throwable;
}
