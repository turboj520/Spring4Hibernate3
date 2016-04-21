// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.util.*;

public abstract class AbstractClusterInvoker
	implements Invoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/AbstractClusterInvoker);
	protected final Directory directory;
	protected final boolean availablecheck;
	private volatile boolean destroyed;
	private volatile Invoker stickyInvoker;

	public AbstractClusterInvoker(Directory directory)
	{
		this(directory, directory.getUrl());
	}

	public AbstractClusterInvoker(Directory directory, URL url)
	{
		destroyed = false;
		stickyInvoker = null;
		if (directory == null)
		{
			throw new IllegalArgumentException("service directory == null");
		} else
		{
			this.directory = directory;
			availablecheck = url.getParameter("cluster.availablecheck", true);
			return;
		}
	}

	public Class getInterface()
	{
		return directory.getInterface();
	}

	public URL getUrl()
	{
		return directory.getUrl();
	}

	public boolean isAvailable()
	{
		Invoker invoker = stickyInvoker;
		if (invoker != null)
			return invoker.isAvailable();
		else
			return directory.isAvailable();
	}

	public void destroy()
	{
		directory.destroy();
		destroyed = true;
	}

	protected Invoker select(LoadBalance loadbalance, Invocation invocation, List invokers, List selected)
		throws RpcException
	{
		if (invokers == null || invokers.size() == 0)
			return null;
		String methodName = invocation != null ? invocation.getMethodName() : "";
		boolean sticky = ((Invoker)invokers.get(0)).getUrl().getMethodParameter(methodName, "sticky", false);
		if (stickyInvoker != null && !invokers.contains(stickyInvoker))
			stickyInvoker = null;
		if (sticky && stickyInvoker != null && (selected == null || !selected.contains(stickyInvoker)) && availablecheck && stickyInvoker.isAvailable())
			return stickyInvoker;
		Invoker invoker = doselect(loadbalance, invocation, invokers, selected);
		if (sticky)
			stickyInvoker = invoker;
		return invoker;
	}

	private Invoker doselect(LoadBalance loadbalance, Invocation invocation, List invokers, List selected)
		throws RpcException
	{
		if (invokers == null || invokers.size() == 0)
			return null;
		if (invokers.size() == 1)
			return (Invoker)invokers.get(0);
		if (invokers.size() == 2 && selected != null && selected.size() > 0)
			return selected.get(0) != invokers.get(0) ? (Invoker)invokers.get(0) : (Invoker)invokers.get(1);
		Invoker invoker = loadbalance.select(invokers, getUrl(), invocation);
		if (selected != null && selected.contains(invoker) || !invoker.isAvailable() && getUrl() != null && availablecheck)
			try
			{
				Invoker rinvoker = reselect(loadbalance, invocation, invokers, selected, availablecheck);
				if (rinvoker != null)
				{
					invoker = rinvoker;
				} else
				{
					int index = invokers.indexOf(invoker);
					try
					{
						invoker = index >= invokers.size() - 1 ? invoker : (Invoker)invokers.get(index + 1);
					}
					catch (Exception e)
					{
						logger.warn((new StringBuilder()).append(e.getMessage()).append(" may because invokers list dynamic change, ignore.").toString(), e);
					}
				}
			}
			catch (Throwable t)
			{
				logger.error((new StringBuilder()).append("clustor relselect fail reason is :").append(t.getMessage()).append(" if can not slove ,you can set cluster.availablecheck=false in url").toString(), t);
			}
		return invoker;
	}

	private Invoker reselect(LoadBalance loadbalance, Invocation invocation, List invokers, List selected, boolean availablecheck)
		throws RpcException
	{
		List reselectInvokers = new ArrayList(invokers.size() <= 1 ? invokers.size() : invokers.size() - 1);
		if (availablecheck)
		{
			Iterator i$ = invokers.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Invoker invoker = (Invoker)i$.next();
				if (invoker.isAvailable() && (selected == null || !selected.contains(invoker)))
					reselectInvokers.add(invoker);
			} while (true);
			if (reselectInvokers.size() > 0)
				return loadbalance.select(reselectInvokers, getUrl(), invocation);
		} else
		{
			Iterator i$ = invokers.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Invoker invoker = (Invoker)i$.next();
				if (selected == null || !selected.contains(invoker))
					reselectInvokers.add(invoker);
			} while (true);
			if (reselectInvokers.size() > 0)
				return loadbalance.select(reselectInvokers, getUrl(), invocation);
		}
		if (selected != null)
		{
			Iterator i$ = selected.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Invoker invoker = (Invoker)i$.next();
				if (invoker.isAvailable() && !reselectInvokers.contains(invoker))
					reselectInvokers.add(invoker);
			} while (true);
		}
		if (reselectInvokers.size() > 0)
			return loadbalance.select(reselectInvokers, getUrl(), invocation);
		else
			return null;
	}

	public Result invoke(Invocation invocation)
		throws RpcException
	{
		checkWheatherDestoried();
		List invokers = list(invocation);
		LoadBalance loadbalance;
		if (invokers != null && invokers.size() > 0)
			loadbalance = (LoadBalance)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/LoadBalance).getExtension(((Invoker)invokers.get(0)).getUrl().getMethodParameter(invocation.getMethodName(), "loadbalance", "random"));
		else
			loadbalance = (LoadBalance)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/LoadBalance).getExtension("random");
		RpcUtils.attachInvocationIdIfAsync(getUrl(), invocation);
		return doInvoke(invocation, invokers, loadbalance);
	}

	protected void checkWheatherDestoried()
	{
		if (destroyed)
			throw new RpcException((new StringBuilder()).append("Rpc cluster invoker for ").append(getInterface()).append(" on consumer ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).append(" is now destroyed! Can not invoke any more.").toString());
		else
			return;
	}

	public String toString()
	{
		return (new StringBuilder()).append(getInterface()).append(" -> ").append(getUrl().toString()).toString();
	}

	protected void checkInvokers(List invokers, Invocation invocation)
	{
		if (invokers == null || invokers.size() == 0)
			throw new RpcException((new StringBuilder()).append("Failed to invoke the method ").append(invocation.getMethodName()).append(" in the service ").append(getInterface().getName()).append(". No provider available for the service ").append(directory.getUrl().getServiceKey()).append(" from registry ").append(directory.getUrl().getAddress()).append(" on the consumer ").append(NetUtils.getLocalHost()).append(" using the dubbo version ").append(Version.getVersion()).append(". Please check if the providers have been started and registered.").toString());
		else
			return;
	}

	protected abstract Result doInvoke(Invocation invocation, List list1, LoadBalance loadbalance)
		throws RpcException;

	protected List list(Invocation invocation)
		throws RpcException
	{
		List invokers = directory.list(invocation);
		return invokers;
	}

}
