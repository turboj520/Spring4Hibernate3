// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractDirectory.java

package com.autohome.turbo.rpc.cluster.directory;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.RpcException;
import com.autohome.turbo.rpc.cluster.*;
import com.autohome.turbo.rpc.cluster.router.MockInvokersSelector;
import java.util.*;

public abstract class AbstractDirectory
	implements Directory
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/directory/AbstractDirectory);
	private final URL url;
	private volatile boolean destroyed;
	private volatile URL consumerUrl;
	private volatile List routers;

	public AbstractDirectory(URL url)
	{
		this(url, null);
	}

	public AbstractDirectory(URL url, List routers)
	{
		this(url, url, routers);
	}

	public AbstractDirectory(URL url, URL consumerUrl, List routers)
	{
		destroyed = false;
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		} else
		{
			this.url = url;
			this.consumerUrl = consumerUrl;
			setRouters(routers);
			return;
		}
	}

	public List list(Invocation invocation)
		throws RpcException
	{
		if (destroyed)
			throw new RpcException((new StringBuilder()).append("Directory already destroyed .url: ").append(getUrl()).toString());
		List invokers = doList(invocation);
		List localRouters = routers;
		if (localRouters != null && localRouters.size() > 0)
		{
			Iterator i$ = localRouters.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Router router = (Router)i$.next();
				try
				{
					if (router.getUrl() == null || router.getUrl().getParameter("runtime", true))
						invokers = router.route(invokers, getConsumerUrl(), invocation);
				}
				catch (Throwable t)
				{
					logger.error((new StringBuilder()).append("Failed to execute router: ").append(getUrl()).append(", cause: ").append(t.getMessage()).toString(), t);
				}
			} while (true);
		}
		return invokers;
	}

	public URL getUrl()
	{
		return url;
	}

	public List getRouters()
	{
		return routers;
	}

	public URL getConsumerUrl()
	{
		return consumerUrl;
	}

	public void setConsumerUrl(URL consumerUrl)
	{
		this.consumerUrl = consumerUrl;
	}

	protected void setRouters(List routers)
	{
		routers = routers != null ? ((List) (new ArrayList(routers))) : ((List) (new ArrayList()));
		String routerkey = url.getParameter("router");
		if (routerkey != null && routerkey.length() > 0)
		{
			RouterFactory routerFactory = (RouterFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/RouterFactory).getExtension(routerkey);
			routers.add(routerFactory.getRouter(url));
		}
		routers.add(new MockInvokersSelector());
		Collections.sort(routers);
		this.routers = routers;
	}

	public boolean isDestroyed()
	{
		return destroyed;
	}

	public void destroy()
	{
		destroyed = true;
	}

	protected abstract List doList(Invocation invocation)
		throws RpcException;

}
