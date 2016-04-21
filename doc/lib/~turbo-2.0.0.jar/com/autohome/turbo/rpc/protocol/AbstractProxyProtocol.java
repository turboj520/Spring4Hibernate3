// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractProxyProtocol.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.rpc.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// Referenced classes of package com.autohome.turbo.rpc.protocol:
//			AbstractProtocol, AbstractInvoker, AbstractExporter

public abstract class AbstractProxyProtocol extends AbstractProtocol
{

	private final List rpcExceptions;
	private ProxyFactory proxyFactory;

	public AbstractProxyProtocol()
	{
		rpcExceptions = new CopyOnWriteArrayList();
	}

	public transient AbstractProxyProtocol(Class exceptions[])
	{
		rpcExceptions = new CopyOnWriteArrayList();
		Class arr$[] = exceptions;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Class exception = arr$[i$];
			addRpcException(exception);
		}

	}

	public void addRpcException(Class exception)
	{
		rpcExceptions.add(exception);
	}

	public void setProxyFactory(ProxyFactory proxyFactory)
	{
		this.proxyFactory = proxyFactory;
	}

	public ProxyFactory getProxyFactory()
	{
		return proxyFactory;
	}

	public Exporter export(final Invoker invoker)
		throws RpcException
	{
		final String uri = serviceKey(invoker.getUrl());
		Exporter exporter = (Exporter)exporterMap.get(uri);
		if (exporter != null)
		{
			return exporter;
		} else
		{
			Runnable runnable = doExport(proxyFactory.getProxy(invoker), invoker.getInterface(), invoker.getUrl());
			exporter = new AbstractExporter(runnable) {

				final String val$uri;
				final Runnable val$runnable;
				final AbstractProxyProtocol this$0;

				public void unexport()
				{
					super.unexport();
					exporterMap.remove(uri);
					if (runnable != null)
						try
						{
							runnable.run();
						}
						catch (Throwable t)
						{
							logger.warn(t.getMessage(), t);
						}
				}

			
			{
				this$0 = AbstractProxyProtocol.this;
				uri = s;
				runnable = runnable1;
				super(x0);
			}
			};
			exporterMap.put(uri, exporter);
			return exporter;
		}
	}

	public Invoker refer(final Class type, final URL url)
		throws RpcException
	{
		final Invoker tagert = proxyFactory.getInvoker(doRefer(type, url), type, url);
		Invoker invoker = new AbstractInvoker(type, url) {

			final Invoker val$tagert;
			final Class val$type;
			final URL val$url;
			final AbstractProxyProtocol this$0;

			protected Result doInvoke(Invocation invocation)
				throws Throwable
			{
				Result result;
label0:
				{
					result = tagert.invoke(invocation);
					Throwable e = result.getException();
					if (e == null)
						break label0;
					Iterator i$ = rpcExceptions.iterator();
					Class rpcException;
					do
					{
						if (!i$.hasNext())
							break label0;
						rpcException = (Class)i$.next();
					} while (!rpcException.isAssignableFrom(e.getClass()));
					throw getRpcException(type, url, invocation, e);
				}
				return result;
				RpcException e;
				e;
				if (e.getCode() == 0)
					e.setCode(getErrorCode(e.getCause()));
				throw e;
				e;
				throw getRpcException(type, url, invocation, e);
			}

			
			{
				this$0 = AbstractProxyProtocol.this;
				tagert = invoker;
				type = class1;
				url = url1;
				super(x0, x1);
			}
		};
		invokers.add(invoker);
		return invoker;
	}

	protected RpcException getRpcException(Class type, URL url, Invocation invocation, Throwable e)
	{
		RpcException re = new RpcException((new StringBuilder()).append("Failed to invoke remote service: ").append(type).append(", method: ").append(invocation.getMethodName()).append(", cause: ").append(e.getMessage()).toString(), e);
		re.setCode(getErrorCode(e));
		return re;
	}

	protected int getErrorCode(Throwable e)
	{
		return 0;
	}

	protected abstract Runnable doExport(Object obj, Class class1, URL url)
		throws RpcException;

	protected abstract Object doRefer(Class class1, URL url)
		throws RpcException;

}
