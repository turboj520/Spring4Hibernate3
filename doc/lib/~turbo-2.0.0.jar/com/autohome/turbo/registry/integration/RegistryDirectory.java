// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegistryDirectory.java

package com.autohome.turbo.registry.integration;

import com.autohome.turbo.common.*;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.registry.NotifyListener;
import com.autohome.turbo.registry.Registry;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.*;
import com.autohome.turbo.rpc.cluster.directory.AbstractDirectory;
import com.autohome.turbo.rpc.cluster.directory.StaticDirectory;
import com.autohome.turbo.rpc.cluster.support.ClusterUtils;
import com.autohome.turbo.rpc.protocol.InvokerWrapper;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.util.*;
import java.util.regex.Pattern;

public class RegistryDirectory extends AbstractDirectory
	implements NotifyListener
{
	private static class InvokerDelegete extends InvokerWrapper
	{

		private URL providerUrl;

		public URL getProviderUrl()
		{
			return providerUrl;
		}

		public InvokerDelegete(Invoker invoker, URL url, URL providerUrl)
		{
			super(invoker, url);
			this.providerUrl = providerUrl;
		}
	}

	private static class InvokerComparator
		implements Comparator
	{

		private static final InvokerComparator comparator = new InvokerComparator();

		public static InvokerComparator getComparator()
		{
			return comparator;
		}

		public int compare(Invoker o1, Invoker o2)
		{
			return o1.getUrl().toString().compareTo(o2.getUrl().toString());
		}

		public volatile int compare(Object x0, Object x1)
		{
			return compare((Invoker)x0, (Invoker)x1);
		}


		private InvokerComparator()
		{
		}
	}


	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/registry/integration/RegistryDirectory);
	private static final Cluster cluster = (Cluster)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/Cluster).getAdaptiveExtension();
	private static final RouterFactory routerFactory = (RouterFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/RouterFactory).getAdaptiveExtension();
	private static final ConfiguratorFactory configuratorFactory = (ConfiguratorFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/ConfiguratorFactory).getAdaptiveExtension();
	private Protocol protocol;
	private Registry registry;
	private final String serviceKey;
	private final Class serviceType;
	private final Map queryMap;
	private final URL directoryUrl;
	private final String serviceMethods[];
	private final boolean multiGroup;
	private volatile boolean forbidden;
	private volatile URL overrideDirectoryUrl;
	private volatile List configurators;
	private volatile Map urlInvokerMap;
	private volatile Map methodInvokerMap;
	private volatile Set cachedInvokerUrls;
	private volatile Set cachedCsharpConfigUrls;
	private volatile Set cachedCsharpRouteUrls;

	public RegistryDirectory(Class serviceType, URL url)
	{
		super(url);
		forbidden = false;
		if (serviceType == null)
			throw new IllegalArgumentException("service type is null.");
		if (url.getServiceKey() == null || url.getServiceKey().length() == 0)
		{
			throw new IllegalArgumentException("registry serviceKey is null.");
		} else
		{
			this.serviceType = serviceType;
			serviceKey = url.getServiceKey();
			queryMap = StringUtils.parseQueryString(url.getParameterAndDecoded("refer"));
			overrideDirectoryUrl = directoryUrl = url.setPath(url.getServiceInterface()).clearParameters().addParameters(queryMap).removeParameter("monitor");
			String group = directoryUrl.getParameter("group", "");
			multiGroup = group != null && ("*".equals(group) || group.contains(","));
			String methods = (String)queryMap.get("methods");
			serviceMethods = methods != null ? Constants.COMMA_SPLIT_PATTERN.split(methods) : null;
			return;
		}
	}

	public void setProtocol(Protocol protocol)
	{
		this.protocol = protocol;
	}

	public void setRegistry(Registry registry)
	{
		this.registry = registry;
	}

	public void subscribe(URL url)
	{
		setConsumerUrl(url);
		registry.subscribe(url, this);
	}

	public void destroy()
	{
		if (isDestroyed())
			return;
		try
		{
			if (getConsumerUrl() != null && registry != null && registry.isAvailable())
				registry.unsubscribe(getConsumerUrl(), this);
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("unexpeced error when unsubscribe service ").append(serviceKey).append("from registry").append(registry.getUrl()).toString(), t);
		}
		super.destroy();
		try
		{
			destroyAllInvokers();
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("Failed to destroy service ").append(serviceKey).toString(), t);
		}
	}

	public synchronized void notify(List urls)
	{
		if (StringUtils.isNotEmpty(getConsumerUrl().getParameter("csharp")))
		{
			notifyCsharp(urls);
			return;
		}
		List invokerUrls = new ArrayList();
		List routerUrls = new ArrayList();
		List configuratorUrls = new ArrayList();
		for (Iterator i$ = urls.iterator(); i$.hasNext();)
		{
			URL url = (URL)i$.next();
			String protocol = url.getProtocol();
			String category = url.getParameter("category", "providers");
			if ("routers".equals(category) || "route".equals(protocol))
				routerUrls.add(url);
			else
			if ("configurators".equals(category) || "override".equals(protocol))
				configuratorUrls.add(url);
			else
			if ("providers".equals(category))
				invokerUrls.add(url);
			else
				logger.warn((new StringBuilder()).append("Unsupported category ").append(category).append(" in notified url: ").append(url).append(" from registry ").append(getUrl().getAddress()).append(" to consumer ").append(NetUtils.getLocalHost()).toString());
		}

		if (configuratorUrls != null && configuratorUrls.size() > 0)
			configurators = toConfigurators(configuratorUrls);
		if (routerUrls != null && routerUrls.size() > 0)
		{
			List routers = toRouters(routerUrls);
			if (routers != null)
				setRouters(routers);
		}
		List localConfigurators = configurators;
		overrideDirectoryUrl = directoryUrl;
		if (localConfigurators != null && localConfigurators.size() > 0)
		{
			for (Iterator i$ = localConfigurators.iterator(); i$.hasNext();)
			{
				Configurator configurator = (Configurator)i$.next();
				overrideDirectoryUrl = configurator.configure(overrideDirectoryUrl);
			}

		}
		refreshInvoker(invokerUrls);
	}

	public synchronized void notifyCsharp(List urls)
	{
		List invokerUrls = new ArrayList();
		List routerUrls = new ArrayList();
		List configuratorUrls = new ArrayList();
		Iterator i$ = urls.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			URL url = (URL)i$.next();
			String category = url.getParameter("category", "providers");
			if ("hessian".equalsIgnoreCase(url.getProtocol()))
				invokerUrls.add(url);
			else
			if ("configurators".equals(category) || "override".equalsIgnoreCase(url.getProtocol()))
				configuratorUrls.add(url);
			else
			if ("routers".equals(category) || "route".equalsIgnoreCase(url.getProtocol()))
				routerUrls.add(url);
		} while (true);
		if (CollectionUtils.isNotEmpty(configuratorUrls))
		{
			cachedCsharpConfigUrls = new HashSet();
			cachedCsharpConfigUrls.addAll(configuratorUrls);
		}
		if (CollectionUtils.isNotEmpty(routerUrls))
		{
			cachedCsharpRouteUrls = new HashSet();
			cachedCsharpRouteUrls.addAll(routerUrls);
		}
		List newInvokerUrls = new ArrayList();
		if (CollectionUtils.isNotEmpty(invokerUrls))
		{
			URL url;
			for (Iterator i$ = invokerUrls.iterator(); i$.hasNext(); newInvokerUrls.add(url.addParameters(new String[] {
	"csharp", getConsumerUrl().getParameter("csharp")
})))
				url = (URL)i$.next();

		}
		refreshCsharpInvoker(newInvokerUrls);
	}

	private void refreshInvoker(List invokerUrls)
	{
		if (invokerUrls != null && invokerUrls.size() == 1 && invokerUrls.get(0) != null && "empty".equals(((URL)invokerUrls.get(0)).getProtocol()))
		{
			forbidden = true;
			methodInvokerMap = null;
			destroyAllInvokers();
		} else
		{
			forbidden = false;
			Map oldUrlInvokerMap = urlInvokerMap;
			if (invokerUrls.size() == 0 && cachedInvokerUrls != null)
			{
				invokerUrls.addAll(cachedInvokerUrls);
			} else
			{
				cachedInvokerUrls = new HashSet();
				cachedInvokerUrls.addAll(invokerUrls);
			}
			if (invokerUrls.size() == 0)
				return;
			Map newUrlInvokerMap = toInvokers(invokerUrls);
			Map newMethodInvokerMap = toMethodInvokers(newUrlInvokerMap);
			if (newUrlInvokerMap == null || newUrlInvokerMap.size() == 0)
				logger.error(new IllegalStateException((new StringBuilder()).append("urls to invokers error .invokerUrls.size :").append(invokerUrls.size()).append(", invoker.size :0. urls :").append(invokerUrls.toString()).toString()));
			methodInvokerMap = multiGroup ? toMergeMethodInvokerMap(newMethodInvokerMap) : newMethodInvokerMap;
			urlInvokerMap = newUrlInvokerMap;
			try
			{
				destroyUnusedInvokers(oldUrlInvokerMap, newUrlInvokerMap);
			}
			catch (Exception e)
			{
				logger.warn("destroyUnusedInvokers error. ", e);
			}
		}
	}

	private void refreshCsharpInvoker(List invokerUrls)
	{
		forbidden = false;
		Map oldUrlInvokerMap = urlInvokerMap;
		if (invokerUrls.size() == 0 && cachedInvokerUrls != null)
		{
			invokerUrls.addAll(cachedInvokerUrls);
		} else
		{
			cachedInvokerUrls = new HashSet();
			cachedInvokerUrls.addAll(invokerUrls);
		}
		if (invokerUrls.size() == 0)
			return;
		Map newUrlInvokerMap = toCsharpInvokers(invokerUrls);
		Map newMethodInvokerMap = toMethodInvokers(newUrlInvokerMap);
		methodInvokerMap = newMethodInvokerMap;
		urlInvokerMap = newUrlInvokerMap;
		try
		{
			destroyUnusedInvokers(oldUrlInvokerMap, newUrlInvokerMap);
		}
		catch (Exception e)
		{
			logger.warn("destroyUnusedInvokers error. ", e);
		}
	}

	private Map toMergeMethodInvokerMap(Map methodMap)
	{
		Map result = new HashMap();
		for (Iterator i$ = methodMap.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String method = (String)entry.getKey();
			List invokers = (List)entry.getValue();
			Map groupMap = new HashMap();
			Invoker invoker;
			List groupInvokers;
			for (Iterator i$ = invokers.iterator(); i$.hasNext(); groupInvokers.add(invoker))
			{
				invoker = (Invoker)i$.next();
				String group = invoker.getUrl().getParameter("group", "");
				groupInvokers = (List)groupMap.get(group);
				if (groupInvokers == null)
				{
					groupInvokers = new ArrayList();
					groupMap.put(group, groupInvokers);
				}
			}

			if (groupMap.size() == 1)
				result.put(method, groupMap.values().iterator().next());
			else
			if (groupMap.size() > 1)
			{
				List groupInvokers = new ArrayList();
				List groupList;
				for (Iterator i$ = groupMap.values().iterator(); i$.hasNext(); groupInvokers.add(cluster.join(new StaticDirectory(groupList))))
					groupList = (List)i$.next();

				result.put(method, groupInvokers);
			} else
			{
				result.put(method, invokers);
			}
		}

		return result;
	}

	public static List toConfigurators(List urls)
	{
		List configurators = new ArrayList(urls.size());
		if (urls == null || urls.size() == 0)
			return configurators;
		Iterator i$ = urls.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			URL url = (URL)i$.next();
			if ("empty".equals(url.getProtocol()))
			{
				configurators.clear();
				break;
			}
			Map override = new HashMap(url.getParameters());
			override.remove("anyhost");
			if (override.size() == 0)
				configurators.clear();
			else
				configurators.add(configuratorFactory.getConfigurator(url));
		} while (true);
		Collections.sort(configurators);
		return configurators;
	}

	private List toRouters(List urls)
	{
		List routers = new ArrayList();
		if (urls == null || urls.size() < 1)
			return routers;
		if (urls != null && urls.size() > 0)
		{
			Iterator i$ = urls.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				URL url = (URL)i$.next();
				if (!"empty".equals(url.getProtocol()))
				{
					String routerType = url.getParameter("router");
					if (routerType != null && routerType.length() > 0)
						url = url.setProtocol(routerType);
					try
					{
						Router router = routerFactory.getRouter(url);
						if (!routers.contains(router))
							routers.add(router);
					}
					catch (Throwable t)
					{
						logger.error((new StringBuilder()).append("convert router url to router error, url: ").append(url).toString(), t);
					}
				}
			} while (true);
		}
		return routers;
	}

	private Map toInvokers(List urls)
	{
		Map newUrlInvokerMap = new HashMap();
		if (urls == null || urls.size() == 0)
			return newUrlInvokerMap;
		Set keys = new HashSet();
		String queryProtocols = (String)queryMap.get("protocol");
		Iterator i$ = urls.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			URL providerUrl = (URL)i$.next();
			if (queryProtocols != null && queryProtocols.length() > 0)
			{
				boolean accept = false;
				String acceptProtocols[] = queryProtocols.split(",");
				String arr$[] = acceptProtocols;
				int len$ = arr$.length;
				int i$ = 0;
				do
				{
					if (i$ >= len$)
						break;
					String acceptProtocol = arr$[i$];
					if (providerUrl.getProtocol().equals(acceptProtocol))
					{
						accept = true;
						break;
					}
					i$++;
				} while (true);
				if (!accept)
					continue;
			}
			if (!"empty".equals(providerUrl.getProtocol()))
				if (!ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).hasExtension(providerUrl.getProtocol()))
				{
					logger.error(new IllegalStateException((new StringBuilder()).append("Unsupported protocol ").append(providerUrl.getProtocol()).append(" in notified url: ").append(providerUrl).append(" from registry ").append(getUrl().getAddress()).append(" to consumer ").append(NetUtils.getLocalHost()).append(", supported protocol: ").append(ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getSupportedExtensions()).toString()));
				} else
				{
					URL url = mergeUrl(providerUrl);
					String key = url.toFullString();
					if (!keys.contains(key))
					{
						keys.add(key);
						Map localUrlInvokerMap = urlInvokerMap;
						Invoker invoker = localUrlInvokerMap != null ? (Invoker)localUrlInvokerMap.get(key) : null;
						if (invoker == null)
						{
							try
							{
								boolean enabled = true;
								if (url.hasParameter("disabled"))
									enabled = !url.getParameter("disabled", false);
								else
									enabled = url.getParameter("enabled", true);
								if (enabled)
									invoker = new InvokerDelegete(protocol.refer(serviceType, url), url, providerUrl);
							}
							catch (Throwable t)
							{
								logger.error((new StringBuilder()).append("Failed to refer invoker for interface:").append(serviceType).append(",url:(").append(url).append(")").append(t.getMessage()).toString(), t);
							}
							if (invoker != null)
								newUrlInvokerMap.put(key, invoker);
						} else
						{
							newUrlInvokerMap.put(key, invoker);
						}
					}
				}
		} while (true);
		keys.clear();
		return newUrlInvokerMap;
	}

	private Map toCsharpInvokers(List urls)
	{
		Map newUrlInvokerMap = new HashMap();
		if (urls == null || urls.size() == 0)
			return newUrlInvokerMap;
		Set keys = new HashSet();
		Iterator i$ = urls.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			URL providerUrl = (URL)i$.next();
			if ("hessian".equalsIgnoreCase(providerUrl.getProtocol()))
				if (!ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).hasExtension(providerUrl.getProtocol()))
				{
					logger.error(new IllegalStateException((new StringBuilder()).append("Unsupported protocol ").append(providerUrl.getProtocol()).append(" in notified url: ").append(providerUrl).append(" from registry ").append(getUrl().getAddress()).append(" to consumer ").append(NetUtils.getLocalHost()).append(", supported protocol: ").append(ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getSupportedExtensions()).toString()));
				} else
				{
					URL url = mergeCsharpUrl(providerUrl);
					String key = url.toFullString();
					if (!keys.contains(key))
					{
						keys.add(key);
						Map localUrlInvokerMap = urlInvokerMap;
						Invoker invoker = localUrlInvokerMap != null ? (Invoker)localUrlInvokerMap.get(key) : null;
						if (invoker == null)
						{
							try
							{
								boolean enabled = true;
								if (url.hasParameter("disabled"))
									enabled = !url.getParameter("disabled", false);
								else
									enabled = url.getParameter("enabled", true);
								if (enabled)
									invoker = new InvokerDelegete(protocol.refer(serviceType, url), url, providerUrl);
							}
							catch (Throwable t)
							{
								logger.error((new StringBuilder()).append("Failed to refer invoker for interface:").append(serviceType).append(",url:(").append(url).append(")").append(t.getMessage()).toString(), t);
							}
							if (invoker != null)
								newUrlInvokerMap.put(key, invoker);
						} else
						{
							newUrlInvokerMap.put(key, invoker);
						}
					}
				}
		} while (true);
		keys.clear();
		return newUrlInvokerMap;
	}

	private URL mergeUrl(URL providerUrl)
	{
		providerUrl = ClusterUtils.mergeUrl(providerUrl, queryMap);
		List localConfigurators = configurators;
		if (localConfigurators != null && localConfigurators.size() > 0)
		{
			for (Iterator i$ = localConfigurators.iterator(); i$.hasNext();)
			{
				Configurator configurator = (Configurator)i$.next();
				providerUrl = configurator.configure(providerUrl);
			}

		}
		providerUrl = providerUrl.addParameter("check", String.valueOf(false));
		overrideDirectoryUrl = overrideDirectoryUrl.addParametersIfAbsent(providerUrl.getParameters());
		if ((providerUrl.getPath() == null || providerUrl.getPath().length() == 0) && "dubbo".equals(providerUrl.getProtocol()))
		{
			String path = directoryUrl.getParameter("interface");
			if (path != null)
			{
				int i = path.indexOf('/');
				if (i >= 0)
					path = path.substring(i + 1);
				i = path.lastIndexOf(':');
				if (i >= 0)
					path = path.substring(0, i);
				providerUrl = providerUrl.setPath(path);
			}
		}
		return providerUrl;
	}

	private URL mergeCsharpUrl(URL providerUrl)
	{
		boolean disable;
label0:
		{
			disable = false;
			if (!CollectionUtils.isNotEmpty(cachedCsharpConfigUrls))
				break label0;
			Iterator i$ = cachedCsharpConfigUrls.iterator();
			URL url;
			do
			{
				if (!i$.hasNext())
					break label0;
				url = (URL)i$.next();
			} while ("empty".equalsIgnoreCase(url.getProtocol()) || !url.getHost().equalsIgnoreCase(providerUrl.getHost()) || url.getPort() != providerUrl.getPort());
			disable = true;
		}
label1:
		{
			if (!CollectionUtils.isNotEmpty(cachedCsharpRouteUrls))
				break label1;
			Iterator i$ = cachedCsharpRouteUrls.iterator();
			URL url;
			do
			{
				if (!i$.hasNext())
					break label1;
				url = (URL)i$.next();
			} while ("empty".equalsIgnoreCase(url.getProtocol()) || !url.getHost().equalsIgnoreCase(getConsumerUrl().getHost()));
			disable = true;
		}
		return providerUrl.addParameter("disabled", disable);
	}

	private List route(List invokers, String method)
	{
		Invocation invocation = new RpcInvocation(method, new Class[0], new Object[0]);
		List routers = getRouters();
		if (routers != null)
		{
			Iterator i$ = routers.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Router router = (Router)i$.next();
				if (router.getUrl() != null && !router.getUrl().getParameter("runtime", true))
					invokers = router.route(invokers, getConsumerUrl(), invocation);
			} while (true);
		}
		return invokers;
	}

	private Map toMethodInvokers(Map invokersMap)
	{
		Map newMethodInvokerMap = new HashMap();
		List invokersList = new ArrayList();
		if (invokersMap != null && invokersMap.size() > 0)
		{
			Invoker invoker;
			for (Iterator i$ = invokersMap.values().iterator(); i$.hasNext(); invokersList.add(invoker))
			{
				invoker = (Invoker)i$.next();
				String parameter = invoker.getUrl().getParameter("methods");
				if (parameter == null || parameter.length() <= 0)
					continue;
				String methods[] = Constants.COMMA_SPLIT_PATTERN.split(parameter);
				if (methods == null || methods.length <= 0)
					continue;
				String arr$[] = methods;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String method = arr$[i$];
					if (method == null || method.length() <= 0 || "*".equals(method))
						continue;
					List methodInvokers = (List)newMethodInvokerMap.get(method);
					if (methodInvokers == null)
					{
						methodInvokers = new ArrayList();
						newMethodInvokerMap.put(method, methodInvokers);
					}
					methodInvokers.add(invoker);
				}

			}

		}
		newMethodInvokerMap.put("*", invokersList);
		if (serviceMethods != null && serviceMethods.length > 0)
		{
			String arr$[] = serviceMethods;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String method = arr$[i$];
				List methodInvokers = (List)newMethodInvokerMap.get(method);
				if (methodInvokers == null || methodInvokers.size() == 0)
					methodInvokers = invokersList;
				newMethodInvokerMap.put(method, route(methodInvokers, method));
			}

		}
		String method;
		List methodInvokers;
		for (Iterator i$ = (new HashSet(newMethodInvokerMap.keySet())).iterator(); i$.hasNext(); newMethodInvokerMap.put(method, Collections.unmodifiableList(methodInvokers)))
		{
			method = (String)i$.next();
			methodInvokers = (List)newMethodInvokerMap.get(method);
			Collections.sort(methodInvokers, InvokerComparator.getComparator());
		}

		return Collections.unmodifiableMap(newMethodInvokerMap);
	}

	private void destroyAllInvokers()
	{
		Map localUrlInvokerMap = urlInvokerMap;
		if (localUrlInvokerMap != null)
		{
			for (Iterator i$ = (new ArrayList(localUrlInvokerMap.values())).iterator(); i$.hasNext();)
			{
				Invoker invoker = (Invoker)i$.next();
				try
				{
					invoker.destroy();
				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("Failed to destroy service ").append(serviceKey).append(" to provider ").append(invoker.getUrl()).toString(), t);
				}
			}

			localUrlInvokerMap.clear();
		}
		methodInvokerMap = null;
	}

	private void destroyUnusedInvokers(Map oldUrlInvokerMap, Map newUrlInvokerMap)
	{
		if (newUrlInvokerMap == null || newUrlInvokerMap.size() == 0)
		{
			destroyAllInvokers();
			return;
		}
		List deleted = null;
		if (oldUrlInvokerMap != null)
		{
			Collection newInvokers = newUrlInvokerMap.values();
			Iterator i$ = oldUrlInvokerMap.entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				if (!newInvokers.contains(entry.getValue()))
				{
					if (deleted == null)
						deleted = new ArrayList();
					deleted.add(entry.getKey());
				}
			} while (true);
		}
		if (deleted != null)
		{
			Iterator i$ = deleted.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				String url = (String)i$.next();
				if (url != null)
				{
					Invoker invoker = (Invoker)oldUrlInvokerMap.remove(url);
					if (invoker != null)
						try
						{
							invoker.destroy();
							if (logger.isDebugEnabled())
								logger.debug((new StringBuilder()).append("destory invoker[").append(invoker.getUrl()).append("] success. ").toString());
						}
						catch (Exception e)
						{
							logger.warn((new StringBuilder()).append("destory invoker[").append(invoker.getUrl()).append("] faild. ").append(e.getMessage()).toString(), e);
						}
				}
			} while (true);
		}
	}

	public List doList(Invocation invocation)
	{
		if (forbidden)
			throw new RpcException(4, (new StringBuilder()).append("Forbid consumer ").append(NetUtils.getLocalHost()).append(" access service ").append(getInterface().getName()).append(" from registry ").append(getUrl().getAddress()).append(" use dubbo version ").append(Version.getVersion()).append(", Please check registry access list (whitelist/blacklist).").toString());
		List invokers = null;
		Map localMethodInvokerMap = methodInvokerMap;
		if (localMethodInvokerMap != null && localMethodInvokerMap.size() > 0)
		{
			String methodName = RpcUtils.getMethodName(invocation);
			Object args[] = RpcUtils.getArguments(invocation);
			if (args != null && args.length > 0 && args[0] != null && ((args[0] instanceof String) || args[0].getClass().isEnum()))
				invokers = (List)localMethodInvokerMap.get((new StringBuilder()).append(methodName).append(".").append(args[0]).toString());
			if (invokers == null)
				invokers = (List)localMethodInvokerMap.get(methodName);
			if (invokers == null)
				invokers = (List)localMethodInvokerMap.get("*");
			if (invokers == null)
			{
				Iterator iterator = localMethodInvokerMap.values().iterator();
				if (iterator.hasNext())
					invokers = (List)iterator.next();
			}
		}
		return ((List) (invokers != null ? invokers : new ArrayList(0)));
	}

	public Class getInterface()
	{
		return serviceType;
	}

	public URL getUrl()
	{
		return overrideDirectoryUrl;
	}

	public boolean isAvailable()
	{
label0:
		{
			if (isDestroyed())
				return false;
			Map localUrlInvokerMap = urlInvokerMap;
			if (localUrlInvokerMap == null || localUrlInvokerMap.size() <= 0)
				break label0;
			Iterator i$ = (new ArrayList(localUrlInvokerMap.values())).iterator();
			Invoker invoker;
			do
			{
				if (!i$.hasNext())
					break label0;
				invoker = (Invoker)i$.next();
			} while (!invoker.isAvailable());
			return true;
		}
		return false;
	}

	public Map getUrlInvokerMap()
	{
		return urlInvokerMap;
	}

	public Map getMethodInvokerMap()
	{
		return methodInvokerMap;
	}

}
