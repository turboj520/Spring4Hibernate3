// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ListenerInvokerWrapper.java

package com.autohome.turbo.rpc.listener;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.*;
import java.util.Iterator;
import java.util.List;

public class ListenerInvokerWrapper
	implements Invoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/listener/ListenerInvokerWrapper);
	private final Invoker invoker;
	private final List listeners;

	public ListenerInvokerWrapper(Invoker invoker, List listeners)
	{
		if (invoker == null)
			throw new IllegalArgumentException("invoker == null");
		this.invoker = invoker;
		this.listeners = listeners;
		if (listeners != null && listeners.size() > 0)
		{
			Iterator i$ = listeners.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				InvokerListener listener = (InvokerListener)i$.next();
				if (listener != null)
					try
					{
						listener.referred(invoker);
					}
					catch (Throwable t)
					{
						logger.error(t.getMessage(), t);
					}
			} while (true);
		}
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

	public String toString()
	{
		return (new StringBuilder()).append(getInterface()).append(" -> ").append(getUrl()).toString() != null ? getUrl().toString() : " ";
	}

	public void destroy()
	{
		invoker.destroy();
		if (listeners != null && listeners.size() > 0)
		{
			Iterator i$ = listeners.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				InvokerListener listener = (InvokerListener)i$.next();
				if (listener != null)
					try
					{
						listener.destroyed(invoker);
					}
					catch (Throwable t)
					{
						logger.error(t.getMessage(), t);
					}
			} while (true);
		}
		break MISSING_BLOCK_LABEL_190;
		Exception exception;
		exception;
		if (listeners != null && listeners.size() > 0)
		{
			Iterator i$ = listeners.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				InvokerListener listener = (InvokerListener)i$.next();
				if (listener != null)
					try
					{
						listener.destroyed(invoker);
					}
					catch (Throwable t)
					{
						logger.error(t.getMessage(), t);
					}
			} while (true);
		}
		throw exception;
	}

}
