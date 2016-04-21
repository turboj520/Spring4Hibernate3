// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboInvoker.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.utils.AtomicPositiveInteger;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.TimeoutException;
import com.autohome.turbo.remoting.exchange.ExchangeClient;
import com.autohome.turbo.remoting.exchange.ResponseFuture;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.AbstractInvoker;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			FutureAdapter

public class DubboInvoker extends AbstractInvoker
{

	private final ExchangeClient clients[];
	private final AtomicPositiveInteger index;
	private final String version;
	private final ReentrantLock destroyLock;
	private final Set invokers;

	public DubboInvoker(Class serviceType, URL url, ExchangeClient clients[])
	{
		this(serviceType, url, clients, null);
	}

	public DubboInvoker(Class serviceType, URL url, ExchangeClient clients[], Set invokers)
	{
		super(serviceType, url, new String[] {
			"interface", "group", "token", "timeout"
		});
		index = new AtomicPositiveInteger();
		destroyLock = new ReentrantLock();
		this.clients = clients;
		version = url.getParameter("version", "0.0.0");
		this.invokers = invokers;
	}

	protected Result doInvoke(Invocation invocation)
		throws Throwable
	{
		RpcInvocation inv;
		String methodName;
		ExchangeClient currentClient;
		inv = (RpcInvocation)invocation;
		methodName = RpcUtils.getMethodName(invocation);
		inv.setAttachment("path", getUrl().getPath());
		inv.setAttachment("version", version);
		if (clients.length == 1)
			currentClient = clients[0];
		else
			currentClient = clients[index.getAndIncrement() % clients.length];
		boolean isAsync;
		int timeout;
		isAsync = RpcUtils.isAsync(getUrl(), invocation);
		boolean isOneway = RpcUtils.isOneway(getUrl(), invocation);
		timeout = getUrl().getMethodParameter(methodName, "timeout", 1000);
		if (!isOneway)
			break MISSING_BLOCK_LABEL_151;
		boolean isSent = getUrl().getMethodParameter(methodName, "sent", false);
		currentClient.send(inv, isSent);
		RpcContext.getContext().setFuture(null);
		return new RpcResult();
		if (!isAsync)
			break MISSING_BLOCK_LABEL_191;
		ResponseFuture future = currentClient.request(inv, timeout);
		RpcContext.getContext().setFuture(new FutureAdapter(future));
		return new RpcResult();
		RpcContext.getContext().setFuture(null);
		return (Result)currentClient.request(inv, timeout).get();
		TimeoutException e;
		e;
		throw new RpcException(2, (new StringBuilder()).append("Invoke remote method timeout. method: ").append(invocation.getMethodName()).append(", provider: ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
		e;
		throw new RpcException(1, (new StringBuilder()).append("Failed to invoke remote method: ").append(invocation.getMethodName()).append(", provider: ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
	}

	public boolean isAvailable()
	{
		if (!super.isAvailable())
			return false;
		ExchangeClient arr$[] = clients;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			ExchangeClient client = arr$[i$];
			if (client.isConnected() && !client.hasAttribute("channel.readonly"))
				return true;
		}

		return false;
	}

	public void destroy()
	{
		if (super.isDestroyed())
			return;
		destroyLock.lock();
		if (super.isDestroyed())
		{
			destroyLock.unlock();
			return;
		}
		super.destroy();
		if (invokers != null)
			invokers.remove(this);
		ExchangeClient arr$[] = clients;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			ExchangeClient client = arr$[i$];
			try
			{
				client.close();
			}
			catch (Throwable t)
			{
				logger.warn(t.getMessage(), t);
			}
		}

		destroyLock.unlock();
		break MISSING_BLOCK_LABEL_128;
		Exception exception;
		exception;
		destroyLock.unlock();
		throw exception;
	}
}
