// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ZookeeperRegistry.java

package com.autohome.turbo.registry.zookeeper;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.registry.NotifyListener;
import com.autohome.turbo.registry.support.FailbackRegistry;
import com.autohome.turbo.remoting.zookeeper.*;
import com.autohome.turbo.rpc.RpcException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ZookeeperRegistry extends FailbackRegistry
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/registry/zookeeper/ZookeeperRegistry);
	private static final int DEFAULT_ZOOKEEPER_PORT = 2181;
	private static final String DEFAULT_ROOT = "dubbo";
	private static final String TURBOC = "turboc";
	private final String root;
	private final Set anyServices = new ConcurrentHashSet();
	private final ConcurrentMap zkListeners = new ConcurrentHashMap();
	private ZookeeperClient zkClient;
	private final URL url;
	private final ZookeeperTransporter zookeeperTransporter;

	public ZookeeperRegistry(URL url, ZookeeperTransporter zookeeperTransporter)
	{
		super(url);
		if (url.isAnyHost())
			throw new IllegalStateException("registry address == null");
		String group = url.getParameter("group", "dubbo");
		if (!group.startsWith("/"))
			group = (new StringBuilder()).append("/").append(group).toString();
		root = group;
		this.url = url;
		this.zookeeperTransporter = zookeeperTransporter;
		try
		{
			getAvailableZk();
		}
		catch (Throwable e)
		{
			if (url.getParameter("check", true))
				throw new RpcException(e);
			logger.error("Connected zookeeper cluster failure,and will try again later", e);
		}
	}

	public boolean isAvailable()
	{
		return zkClient != null && zkClient.isConnected();
	}

	public void destroy()
	{
		super.destroy();
		try
		{
			getAvailableZk().close();
		}
		catch (Exception e)
		{
			logger.warn((new StringBuilder()).append("Failed to close zookeeper client ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
	}

	protected void doRegister(URL url)
	{
		try
		{
			getAvailableZk().create(toUrlPath(url), url.getParameter("dynamic", true));
		}
		catch (Throwable e)
		{
			throw new RpcException((new StringBuilder()).append("Failed to register ").append(url).append(" to zookeeper ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
	}

	protected void doUnregister(URL url)
	{
		try
		{
			getAvailableZk().delete(toUrlPath(url));
		}
		catch (Throwable e)
		{
			throw new RpcException((new StringBuilder()).append("Failed to unregister ").append(url).append(" to zookeeper ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
	}

	protected void doSubscribe(final URL url, final NotifyListener listener)
	{
		try
		{
			if ("*".equals(url.getServiceInterface()) && StringUtils.isEmpty(url.getParameter("csharp")))
			{
				String root = toRootPath();
				ConcurrentMap listeners = (ConcurrentMap)zkListeners.get(url);
				if (listeners == null)
				{
					zkListeners.putIfAbsent(url, new ConcurrentHashMap());
					listeners = (ConcurrentMap)zkListeners.get(url);
				}
				ChildListener zkListener = (ChildListener)listeners.get(listener);
				if (zkListener == null)
				{
					listeners.putIfAbsent(listener, new ChildListener() {

						final URL val$url;
						final NotifyListener val$listener;
						final ZookeeperRegistry this$0;

						public void childChanged(String parentPath, List currentChilds)
						{
							Iterator i$ = currentChilds.iterator();
							do
							{
								if (!i$.hasNext())
									break;
								String child = (String)i$.next();
								child = URL.decode(child);
								if (!anyServices.contains(child))
								{
									anyServices.add(child);
									subscribe(url.setPath(child).addParameters(new String[] {
										"interface", child, "check", String.valueOf(false)
									}), listener);
								}
							} while (true);
						}

			
			{
				this$0 = ZookeeperRegistry.this;
				url = url1;
				listener = notifylistener;
				super();
			}
					});
					zkListener = (ChildListener)listeners.get(listener);
				}
				getAvailableZk().create(root, false);
				List services = getAvailableZk().addChildListener(root, zkListener);
				if (services != null && services.size() > 0)
				{
					String service;
					for (Iterator i$ = services.iterator(); i$.hasNext(); subscribe(url.setPath(service).addParameters(new String[] {
	"interface", service, "check", String.valueOf(false)
}), listener))
					{
						service = (String)i$.next();
						service = URL.decode(service);
						anyServices.add(service);
					}

				}
			} else
			{
				List urls = new ArrayList();
				String arr$[] = toCategoriesPath(url);
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String path = arr$[i$];
					ConcurrentMap listeners = (ConcurrentMap)zkListeners.get(url);
					if (listeners == null)
					{
						zkListeners.putIfAbsent(url, new ConcurrentHashMap());
						listeners = (ConcurrentMap)zkListeners.get(url);
					}
					ChildListener zkListener = (ChildListener)listeners.get(listener);
					if (zkListener == null)
					{
						listeners.putIfAbsent(listener, new ChildListener() {

							final URL val$url;
							final NotifyListener val$listener;
							final ZookeeperRegistry this$0;

							public void childChanged(String parentPath, List currentChilds)
							{
								notify(url, listener, toUrlsWithEmpty(url, parentPath, currentChilds));
							}

			
			{
				this$0 = ZookeeperRegistry.this;
				url = url1;
				listener = notifylistener;
				super();
			}
						});
						zkListener = (ChildListener)listeners.get(listener);
					}
					getAvailableZk().create(path, false);
					List children = getAvailableZk().addChildListener(path, zkListener);
					if (children != null)
						urls.addAll(toUrlsWithEmpty(url, path, children));
				}

				notify(url, listener, urls);
			}
		}
		catch (Throwable e)
		{
			throw new RpcException((new StringBuilder()).append("Failed to subscribe ").append(url).append(" to zookeeper ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
	}

	protected void doUnsubscribe(URL url, NotifyListener listener)
	{
		ConcurrentMap listeners = (ConcurrentMap)zkListeners.get(url);
		if (listeners != null)
		{
			ChildListener zkListener = (ChildListener)listeners.get(listener);
			if (zkListener != null)
				getAvailableZk().removeChildListener(toUrlPath(url), zkListener);
		}
	}

	public List lookup(URL url)
	{
		if (url == null)
			throw new IllegalArgumentException("lookup url == null");
		List providers;
		providers = new ArrayList();
		String arr$[] = toCategoriesPath(url);
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String path = arr$[i$];
			List children = getAvailableZk().getChildren(path);
			if (children != null)
				providers.addAll(children);
		}

		return toUrlsWithoutEmpty(url, providers);
		Throwable e;
		e;
		throw new RpcException((new StringBuilder()).append("Failed to lookup ").append(url).append(" from zookeeper ").append(getUrl()).append(", cause: ").append(e.getMessage()).toString(), e);
	}

	private String toRootDir()
	{
		if (root.equals("/"))
			return root;
		else
			return (new StringBuilder()).append(root).append("/").toString();
	}

	private String toRootPath()
	{
		return root;
	}

	private String toServicePath(URL url)
	{
		if (StringUtils.isNotEmpty(url.getParameter("csharp")))
			return (new StringBuilder()).append("/turboc/").append(URL.encode(url.getParameter("csharp"))).toString();
		String name = url.getServiceInterface();
		if ("*".equals(name))
			return toRootPath();
		else
			return (new StringBuilder()).append(toRootDir()).append(URL.encode(name)).toString();
	}

	private String[] toCategoriesPath(URL url)
	{
		String categroies[];
		if ("*".equals(url.getParameter("category")))
			categroies = (new String[] {
				"providers", "consumers", "routers", "configurators"
			});
		else
			categroies = url.getParameter("category", new String[] {
				"providers"
			});
		String paths[] = new String[categroies.length];
		for (int i = 0; i < categroies.length; i++)
			paths[i] = (new StringBuilder()).append(toServicePath(url)).append("/").append(categroies[i]).toString();

		return paths;
	}

	private String toCategoryPath(URL url)
	{
		return (new StringBuilder()).append(toServicePath(url)).append("/").append(url.getParameter("category", "providers")).toString();
	}

	private String toUrlPath(URL url)
	{
		return (new StringBuilder()).append(toCategoryPath(url)).append("/").append(URL.encode(url.toFullString())).toString();
	}

	private List toUrlsWithoutEmpty(URL consumer, List providers)
	{
		List urls = new ArrayList();
		if (providers != null && providers.size() > 0)
		{
			Iterator i$ = providers.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				String provider = (String)i$.next();
				provider = URL.decode(provider);
				if (provider.contains("://"))
				{
					URL url = URL.valueOf(provider);
					if (UrlUtils.isMatch(consumer, url))
						urls.add(url);
				}
			} while (true);
		}
		return urls;
	}

	private List toUrlsWithEmpty(URL consumer, String path, List providers)
	{
		List urls = toUrlsWithoutEmpty(consumer, providers);
		if (urls.isEmpty())
		{
			int i = path.lastIndexOf('/');
			String category = i >= 0 ? path.substring(i + 1) : path;
			URL empty = consumer.setProtocol("empty").addParameter("category", category);
			urls.add(empty);
		}
		return urls;
	}

	static String appendDefaultPort(String address)
	{
		if (address != null && address.length() > 0)
		{
			int i = address.indexOf(':');
			if (i < 0)
				return (new StringBuilder()).append(address).append(":").append(2181).toString();
			if (Integer.parseInt(address.substring(i + 1)) == 0)
				return (new StringBuilder()).append(address.substring(0, i + 1)).append(2181).toString();
		}
		return address;
	}

	private ZookeeperClient getAvailableZk()
	{
		if (!isAvailable())
		{
			if (zkClient != null)
				try
				{
					zkClient.close();
				}
				catch (Throwable e)
				{
					logger.error(e);
				}
			zkClient = zookeeperTransporter.connect(url);
			zkClient.addStateListener(new StateListener() {

				final ZookeeperRegistry this$0;

				public void stateChanged(int state)
				{
					if (state == 2)
						try
						{
							recover();
						}
						catch (Exception e)
						{
							ZookeeperRegistry.logger.error(e.getMessage(), e);
						}
				}

			
			{
				this$0 = ZookeeperRegistry.this;
				super();
			}
			});
		}
		ZookeeperClient localZkClient = zkClient;
		return localZkClient;
	}






}
