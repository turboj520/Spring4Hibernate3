// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegistryProtocol.java

package com.autohome.turbo.registry.integration;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.registry.*;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Cluster;
import com.autohome.turbo.rpc.cluster.Configurator;
import com.autohome.turbo.rpc.protocol.InvokerWrapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.registry.integration:
//			RegistryDirectory

public class RegistryProtocol
	implements Protocol
{
	private class ExporterChangeableWrapper
		implements Exporter
	{

		private Exporter exporter;
		private final Invoker originInvoker;
		final RegistryProtocol this$0;

		public Invoker getOriginInvoker()
		{
			return originInvoker;
		}

		public Invoker getInvoker()
		{
			return exporter.getInvoker();
		}

		public void setExporter(Exporter exporter)
		{
			this.exporter = exporter;
		}

		public void unexport()
		{
			String key = getCacheKey(originInvoker);
			bounds.remove(key);
			exporter.unexport();
		}

		public ExporterChangeableWrapper(Exporter exporter, Invoker originInvoker)
		{
			this$0 = RegistryProtocol.this;
			super();
			this.exporter = exporter;
			this.originInvoker = originInvoker;
		}
	}

	public static class InvokerDelegete extends InvokerWrapper
	{

		private final Invoker invoker;

		public Invoker getInvoker()
		{
			if (invoker instanceof InvokerDelegete)
				return ((InvokerDelegete)invoker).getInvoker();
			else
				return invoker;
		}

		public InvokerDelegete(Invoker invoker, URL url)
		{
			super(invoker, url);
			this.invoker = invoker;
		}
	}

	private class OverrideListener
		implements NotifyListener
	{

		private volatile List configurators;
		private final URL subscribeUrl;
		final RegistryProtocol this$0;

		public void notify(List urls)
		{
			List result = null;
			Iterator i$ = urls.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				URL url = (URL)i$.next();
				URL overrideUrl = url;
				if (url.getParameter("category") == null && "override".equals(url.getProtocol()))
					overrideUrl = url.addParameter("category", "configurators");
				if (!UrlUtils.isMatch(subscribeUrl, overrideUrl))
				{
					if (result == null)
						result = new ArrayList(urls);
					result.remove(url);
					RegistryProtocol.logger.warn((new StringBuilder()).append("Subsribe category=configurator, but notifed non-configurator urls. may be registry bug. unexcepted url: ").append(url).toString());
				}
			} while (true);
			if (result != null)
				urls = result;
			configurators = RegistryDirectory.toConfigurators(urls);
			List exporters = new ArrayList(bounds.values());
			Iterator i$ = exporters.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				ExporterChangeableWrapper exporter = (ExporterChangeableWrapper)i$.next();
				Invoker invoker = exporter.getOriginInvoker();
				Invoker originInvoker;
				if (invoker instanceof InvokerDelegete)
					originInvoker = ((InvokerDelegete)invoker).getInvoker();
				else
					originInvoker = invoker;
				URL originUrl = getProviderUrl(originInvoker);
				URL newUrl = getNewInvokerUrl(originUrl, urls);
				if (!originUrl.equals(newUrl))
					doChangeLocalExport(originInvoker, newUrl);
			} while (true);
		}

		private URL getNewInvokerUrl(URL url, List urls)
		{
			List localConfigurators = configurators;
			if (localConfigurators != null && localConfigurators.size() > 0)
			{
				for (Iterator i$ = localConfigurators.iterator(); i$.hasNext();)
				{
					Configurator configurator = (Configurator)i$.next();
					url = configurator.configure(url);
				}

			}
			return url;
		}

		public OverrideListener(URL subscribeUrl)
		{
			this$0 = RegistryProtocol.this;
			super();
			this.subscribeUrl = subscribeUrl;
		}
	}


	private Cluster cluster;
	private Protocol protocol;
	private RegistryFactory registryFactory;
	private ProxyFactory proxyFactory;
	private static RegistryProtocol INSTANCE;
	private final Map overrideListeners = new ConcurrentHashMap();
	private final Map bounds = new ConcurrentHashMap();
	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/registry/integration/RegistryProtocol);

	public void setCluster(Cluster cluster)
	{
		this.cluster = cluster;
	}

	public void setProtocol(Protocol protocol)
	{
		this.protocol = protocol;
	}

	public void setRegistryFactory(RegistryFactory registryFactory)
	{
		this.registryFactory = registryFactory;
	}

	public void setProxyFactory(ProxyFactory proxyFactory)
	{
		this.proxyFactory = proxyFactory;
	}

	public int getDefaultPort()
	{
		return 9090;
	}

	public RegistryProtocol()
	{
		INSTANCE = this;
	}

	public static RegistryProtocol getRegistryProtocol()
	{
		if (INSTANCE == null)
			ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getExtension("registry");
		return INSTANCE;
	}

	public Map getOverrideListeners()
	{
		return overrideListeners;
	}

	public Exporter export(Invoker originInvoker)
		throws RpcException
	{
		final ExporterChangeableWrapper exporter = doLocalExport(originInvoker);
		final Registry registry = getRegistry(originInvoker);
		final URL registedProviderUrl = getRegistedProviderUrl(originInvoker);
		registry.register(registedProviderUrl);
		final URL overrideSubscribeUrl = getSubscribedOverrideUrl(registedProviderUrl);
		final OverrideListener overrideSubscribeListener = new OverrideListener(overrideSubscribeUrl);
		overrideListeners.put(overrideSubscribeUrl, overrideSubscribeListener);
		registry.subscribe(overrideSubscribeUrl, overrideSubscribeListener);
		return new Exporter() {

			final ExporterChangeableWrapper val$exporter;
			final Registry val$registry;
			final URL val$registedProviderUrl;
			final URL val$overrideSubscribeUrl;
			final OverrideListener val$overrideSubscribeListener;
			final RegistryProtocol this$0;

			public Invoker getInvoker()
			{
				return exporter.getInvoker();
			}

			public void unexport()
			{
				try
				{
					exporter.unexport();
				}
				catch (Throwable t)
				{
					RegistryProtocol.logger.warn(t.getMessage(), t);
				}
				try
				{
					registry.unregister(registedProviderUrl);
				}
				catch (Throwable t)
				{
					RegistryProtocol.logger.warn(t.getMessage(), t);
				}
				try
				{
					overrideListeners.remove(overrideSubscribeUrl);
					registry.unsubscribe(overrideSubscribeUrl, overrideSubscribeListener);
				}
				catch (Throwable t)
				{
					RegistryProtocol.logger.warn(t.getMessage(), t);
				}
			}

			
			{
				this$0 = RegistryProtocol.this;
				exporter = exporterchangeablewrapper;
				registry = registry1;
				registedProviderUrl = url;
				overrideSubscribeUrl = url1;
				overrideSubscribeListener = overridelistener;
				super();
			}
		};
	}

	private ExporterChangeableWrapper doLocalExport(Invoker originInvoker)
	{
		String key = getCacheKey(originInvoker);
		ExporterChangeableWrapper exporter = (ExporterChangeableWrapper)bounds.get(key);
		if (exporter == null)
			synchronized (bounds)
			{
				exporter = (ExporterChangeableWrapper)bounds.get(key);
				if (exporter == null)
				{
					Invoker invokerDelegete = new InvokerDelegete(originInvoker, getProviderUrl(originInvoker));
					exporter = new ExporterChangeableWrapper(protocol.export(invokerDelegete), originInvoker);
					bounds.put(key, exporter);
				}
			}
		return exporter;
	}

	private void doChangeLocalExport(Invoker originInvoker, URL newInvokerUrl)
	{
		String key = getCacheKey(originInvoker);
		ExporterChangeableWrapper exporter = (ExporterChangeableWrapper)bounds.get(key);
		if (exporter == null)
		{
			logger.warn(new IllegalStateException("error state, exporter should not be null"));
			return;
		} else
		{
			Invoker invokerDelegete = new InvokerDelegete(originInvoker, newInvokerUrl);
			exporter.setExporter(protocol.export(invokerDelegete));
			return;
		}
	}

	private Registry getRegistry(Invoker originInvoker)
	{
		URL registryUrl = originInvoker.getUrl();
		if ("registry".equals(registryUrl.getProtocol()))
		{
			String protocol = registryUrl.getParameter("registry", "dubbo");
			registryUrl = registryUrl.setProtocol(protocol).removeParameter("registry");
		}
		return registryFactory.getRegistry(registryUrl);
	}

	private URL getRegistedProviderUrl(Invoker originInvoker)
	{
		URL providerUrl = getProviderUrl(originInvoker);
		URL registedProviderUrl = providerUrl.removeParameters(getFilteredKeys(providerUrl)).removeParameter("monitor");
		return registedProviderUrl;
	}

	private URL getSubscribedOverrideUrl(URL registedProviderUrl)
	{
		return registedProviderUrl.setProtocol("provider").addParameters(new String[] {
			"category", "configurators", "check", String.valueOf(false)
		});
	}

	private URL getProviderUrl(Invoker origininvoker)
	{
		String export = origininvoker.getUrl().getParameterAndDecoded("export");
		if (export == null || export.length() == 0)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("The registry export url is null! registry: ").append(origininvoker.getUrl()).toString());
		} else
		{
			URL providerUrl = URL.valueOf(export);
			return providerUrl;
		}
	}

	private String getCacheKey(Invoker originInvoker)
	{
		URL providerUrl = getProviderUrl(originInvoker);
		String key = providerUrl.removeParameters(new String[] {
			"dynamic", "enabled"
		}).toFullString();
		return key;
	}

	public Invoker refer(Class type, URL url)
		throws RpcException
	{
		url = url.setProtocol(url.getParameter("registry", "dubbo")).removeParameter("registry");
		Registry registry = registryFactory.getRegistry(url);
		if (com/autohome/turbo/registry/RegistryService.equals(type))
			return proxyFactory.getInvoker(registry, type, url);
		Map qs = StringUtils.parseQueryString(url.getParameterAndDecoded("refer"));
		String group = (String)qs.get("group");
		if (group != null && group.length() > 0 && (Constants.COMMA_SPLIT_PATTERN.split(group).length > 1 || "*".equals(group)))
			return doRefer(getMergeableCluster(), registry, type, url);
		else
			return doRefer(cluster, registry, type, url);
	}

	private Cluster getMergeableCluster()
	{
		return (Cluster)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/Cluster).getExtension("mergeable");
	}

	private Invoker doRefer(Cluster cluster, Registry registry, Class type, URL url)
	{
		RegistryDirectory directory = new RegistryDirectory(type, url);
		directory.setRegistry(registry);
		directory.setProtocol(protocol);
		URL subscribeUrl = new URL("consumer", NetUtils.getLocalHost(), 0, type.getName(), directory.getUrl().getParameters());
		if (!"*".equals(url.getServiceInterface()) && url.getParameter("register", true))
			registry.register(subscribeUrl.addParameters(new String[] {
				"category", "consumers", "check", String.valueOf(false)
			}));
		directory.subscribe(subscribeUrl.addParameter("category", "providers,configurators,routers"));
		return cluster.join(directory);
	}

	private static String[] getFilteredKeys(URL url)
	{
		Map params = url.getParameters();
		if (params != null && !params.isEmpty())
		{
			List filteredKeys = new ArrayList();
			Iterator i$ = params.entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				if (entry != null && entry.getKey() != null && ((String)entry.getKey()).startsWith("."))
					filteredKeys.add(entry.getKey());
			} while (true);
			return (String[])filteredKeys.toArray(new String[filteredKeys.size()]);
		} else
		{
			return new String[0];
		}
	}

	public void destroy()
	{
		List exporters = new ArrayList(bounds.values());
		Exporter exporter;
		for (Iterator i$ = exporters.iterator(); i$.hasNext(); exporter.unexport())
			exporter = (Exporter)i$.next();

		bounds.clear();
	}







}
