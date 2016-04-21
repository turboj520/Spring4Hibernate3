// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.config.annotation.Service;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Configurator;
import com.autohome.turbo.rpc.cluster.ConfiguratorFactory;
import com.autohome.turbo.rpc.service.GenericService;
import com.autohome.turbo.rpc.support.ProtocolUtils;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractServiceConfig, ProtocolConfig, MethodConfig, ArgumentConfig, 
//			ProviderConfig, ModuleConfig, ApplicationConfig

public class ServiceConfig extends AbstractServiceConfig
{

	private static final long serialVersionUID = 0x2a1a2e1c8671e9e2L;
	private static final Protocol protocol = (Protocol)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getAdaptiveExtension();
	private static final ProxyFactory proxyFactory = (ProxyFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/ProxyFactory).getAdaptiveExtension();
	private static final Map RANDOM_PORT_MAP = new HashMap();
	private String interfaceName;
	private Class interfaceClass;
	private Object ref;
	private String path;
	private List methods;
	private ProviderConfig provider;
	private final List urls;
	private final List exporters;
	private volatile transient boolean exported;
	private volatile transient boolean unexported;
	private volatile String generic;

	public ServiceConfig()
	{
		urls = new ArrayList();
		exporters = new ArrayList();
	}

	public ServiceConfig(Service service)
	{
		urls = new ArrayList();
		exporters = new ArrayList();
		appendAnnotation(com/autohome/turbo/config/annotation/Service, service);
	}

	public URL toUrl()
	{
		return urls != null && urls.size() != 0 ? (URL)urls.iterator().next() : null;
	}

	public List toUrls()
	{
		return urls;
	}

	public boolean isExported()
	{
		return exported;
	}

	public boolean isUnexported()
	{
		return unexported;
	}

	public synchronized void export()
	{
		if (provider != null)
		{
			if (export == null)
				export = provider.getExport();
			if (delay == null)
				delay = provider.getDelay();
		}
		if (export != null && !export.booleanValue())
			return;
		if (delay != null && delay.intValue() > 0)
		{
			Thread thread = new Thread(new Runnable() {

				final ServiceConfig this$0;

				public void run()
				{
					try
					{
						Thread.sleep(delay.intValue());
					}
					catch (Throwable e) { }
					doExport();
				}

			
			{
				this$0 = ServiceConfig.this;
				super();
			}
			});
			thread.setDaemon(true);
			thread.setName("DelayExportServiceThread");
			thread.start();
		} else
		{
			doExport();
		}
	}

	protected synchronized void doExport()
	{
		if (unexported)
			throw new IllegalStateException("Already unexported!");
		if (exported)
			return;
		exported = true;
		if (interfaceName == null || interfaceName.length() == 0)
			throw new IllegalStateException("<dubbo:service interface=\"\" /> interface not allow null!");
		checkDefault();
		if (provider != null)
		{
			if (application == null)
				application = provider.getApplication();
			if (module == null)
				module = provider.getModule();
			if (registries == null)
				registries = provider.getRegistries();
			if (monitor == null)
				monitor = provider.getMonitor();
			if (protocols == null)
				protocols = provider.getProtocols();
		}
		if (module != null)
		{
			if (registries == null)
				registries = module.getRegistries();
			if (monitor == null)
				monitor = module.getMonitor();
		}
		if (application != null)
		{
			if (registries == null)
				registries = application.getRegistries();
			if (monitor == null)
				monitor = application.getMonitor();
		}
		if (ref instanceof GenericService)
		{
			interfaceClass = com/autohome/turbo/rpc/service/GenericService;
			if (StringUtils.isEmpty(generic))
				generic = Boolean.TRUE.toString();
		} else
		{
			try
			{
				interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
			checkInterfaceAndMethods(interfaceClass, methods);
			checkRef();
			generic = Boolean.FALSE.toString();
		}
		if (local != null)
		{
			if (local == "true")
				local = (new StringBuilder()).append(interfaceName).append("Local").toString();
			Class localClass;
			try
			{
				localClass = ClassHelper.forNameWithThreadContextClassLoader(local);
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
			if (!interfaceClass.isAssignableFrom(localClass))
				throw new IllegalStateException((new StringBuilder()).append("The local implemention class ").append(localClass.getName()).append(" not implement interface ").append(interfaceName).toString());
		}
		if (stub != null)
		{
			if (stub == "true")
				stub = (new StringBuilder()).append(interfaceName).append("Stub").toString();
			Class stubClass;
			try
			{
				stubClass = ClassHelper.forNameWithThreadContextClassLoader(stub);
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
			if (!interfaceClass.isAssignableFrom(stubClass))
				throw new IllegalStateException((new StringBuilder()).append("The stub implemention class ").append(stubClass.getName()).append(" not implement interface ").append(interfaceName).toString());
		}
		checkApplication();
		checkRegistry();
		checkProtocol();
		appendProperties(this);
		checkStubAndMock(interfaceClass);
		if (path == null || path.length() == 0)
			path = interfaceName;
		doExportUrls();
	}

	private void checkRef()
	{
		if (ref == null)
			throw new IllegalStateException("ref not allow null!");
		if (!interfaceClass.isInstance(ref))
			throw new IllegalStateException((new StringBuilder()).append("The class ").append(ref.getClass().getName()).append(" unimplemented interface ").append(interfaceClass).append("!").toString());
		else
			return;
	}

	public synchronized void unexport()
	{
		if (!exported)
			return;
		if (unexported)
			return;
		if (exporters != null && exporters.size() > 0)
		{
			for (Iterator i$ = exporters.iterator(); i$.hasNext();)
			{
				Exporter exporter = (Exporter)i$.next();
				try
				{
					exporter.unexport();
				}
				catch (Throwable t)
				{
					logger.warn((new StringBuilder()).append("unexpected err when unexport").append(exporter).toString(), t);
				}
			}

			exporters.clear();
		}
		unexported = true;
	}

	private void doExportUrls()
	{
		List registryURLs = loadRegistries(true);
		ProtocolConfig protocolConfig;
		for (Iterator i$ = protocols.iterator(); i$.hasNext(); doExportUrlsFor1Protocol(protocolConfig, registryURLs))
			protocolConfig = (ProtocolConfig)i$.next();

	}

	private void doExportUrlsFor1Protocol(ProtocolConfig protocolConfig, List registryURLs)
	{
		String name;
		String host;
		boolean anyhost;
		name = protocolConfig.getName();
		if (name == null || name.length() == 0)
			name = "dubbo";
		host = protocolConfig.getHost();
		if (provider != null && (host == null || host.length() == 0))
			host = provider.getHost();
		anyhost = false;
		if (!NetUtils.isInvalidLocalHost(host))
			break MISSING_BLOCK_LABEL_256;
		anyhost = true;
		try
		{
			host = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
			logger.warn(e.getMessage(), e);
		}
		if (!NetUtils.isInvalidLocalHost(host))
			break MISSING_BLOCK_LABEL_256;
		if (registryURLs == null || registryURLs.size() <= 0) goto _L2; else goto _L1
_L1:
		Iterator i$ = registryURLs.iterator();
_L3:
		URL registryURL;
		if (!i$.hasNext())
			break; /* Loop/switch isn't completed */
		registryURL = (URL)i$.next();
		Socket socket = new Socket();
		SocketAddress addr = new InetSocketAddress(registryURL.getHost(), registryURL.getPort());
		socket.connect(addr, 1000);
		host = socket.getLocalAddress().getHostAddress();
		try
		{
			socket.close();
			break; /* Loop/switch isn't completed */
		}
		catch (Throwable e) { }
		break; /* Loop/switch isn't completed */
		Exception exception;
		exception;
		try
		{
			socket.close();
		}
		catch (Throwable e) { }
		throw exception;
		Exception e;
		e;
		logger.warn(e.getMessage(), e);
		if (true) goto _L3; else goto _L2
_L2:
		if (NetUtils.isInvalidLocalHost(host))
			host = NetUtils.getLocalHost();
		Integer port = protocolConfig.getPort();
		if (provider != null && (port == null || port.intValue() == 0))
			port = provider.getPort();
		int defaultPort = ((Protocol)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getExtension(name)).getDefaultPort();
		if (port == null || port.intValue() == 0)
			port = Integer.valueOf(defaultPort);
		if (port == null || port.intValue() <= 0)
		{
			port = getRandomPort(name);
			if (port == null || port.intValue() < 0)
			{
				port = Integer.valueOf(NetUtils.getAvailablePort(defaultPort));
				putRandomPort(name, port);
			}
			logger.warn((new StringBuilder()).append("Use random available port(").append(port).append(") for protocol ").append(name).toString());
		}
		Map map = new HashMap();
		if (anyhost)
			map.put("anyhost", "true");
		map.put("side", "provider");
		map.put("dubbo", Version.getVersion());
		map.put("timestamp", String.valueOf(System.currentTimeMillis()));
		if (ConfigUtils.getPid() > 0)
			map.put("pid", String.valueOf(ConfigUtils.getPid()));
		appendParameters(map, application);
		appendParameters(map, module);
		appendParameters(map, provider, "default");
		appendParameters(map, protocolConfig);
		appendParameters(map, this);
		if (this.methods != null && this.methods.size() > 0)
		{
			for (Iterator i$ = this.methods.iterator(); i$.hasNext();)
			{
				MethodConfig method = (MethodConfig)i$.next();
				appendParameters(map, method, method.getName());
				String retryKey = (new StringBuilder()).append(method.getName()).append(".retry").toString();
				if (map.containsKey(retryKey))
				{
					String retryValue = (String)map.remove(retryKey);
					if ("false".equals(retryValue))
						map.put((new StringBuilder()).append(method.getName()).append(".retries").toString(), "0");
				}
				List arguments = method.getArguments();
				if (arguments != null && arguments.size() > 0)
				{
					Iterator i$ = arguments.iterator();
					while (i$.hasNext()) 
					{
						ArgumentConfig argument = (ArgumentConfig)i$.next();
						if (argument.getType() != null && argument.getType().length() > 0)
						{
							Method methods[] = interfaceClass.getMethods();
							if (methods != null && methods.length > 0)
							{
								int i = 0;
								while (i < methods.length) 
								{
									String methodName = methods[i].getName();
									if (methodName.equals(method.getName()))
									{
										Class argtypes[] = methods[i].getParameterTypes();
										if (argument.getIndex().intValue() != -1)
										{
											if (argtypes[argument.getIndex().intValue()].getName().equals(argument.getType()))
												appendParameters(map, argument, (new StringBuilder()).append(method.getName()).append(".").append(argument.getIndex()).toString());
											else
												throw new IllegalArgumentException((new StringBuilder()).append("argument config error : the index attribute and type attirbute not match :index :").append(argument.getIndex()).append(", type:").append(argument.getType()).toString());
										} else
										{
											for (int j = 0; j < argtypes.length; j++)
											{
												Class argclazz = argtypes[j];
												if (!argclazz.getName().equals(argument.getType()))
													continue;
												appendParameters(map, argument, (new StringBuilder()).append(method.getName()).append(".").append(j).toString());
												if (argument.getIndex().intValue() != -1 && argument.getIndex().intValue() != j)
													throw new IllegalArgumentException((new StringBuilder()).append("argument config error : the index attribute and type attirbute not match :index :").append(argument.getIndex()).append(", type:").append(argument.getType()).toString());
											}

										}
									}
									i++;
								}
							}
						} else
						if (argument.getIndex().intValue() != -1)
							appendParameters(map, argument, (new StringBuilder()).append(method.getName()).append(".").append(argument.getIndex()).toString());
						else
							throw new IllegalArgumentException("argument config must set index or type attribute.eg: <dubbo:argument index='0' .../> or <dubbo:argument type=xxx .../>");
					}
				}
			}

		}
		if (ProtocolUtils.isGeneric(generic))
		{
			map.put("generic", generic);
			map.put("methods", "*");
		} else
		{
			String revision = Version.getVersion(interfaceClass, version);
			if (revision != null && revision.length() > 0)
				map.put("revision", revision);
			String methods[] = Wrapper.getWrapper(interfaceClass).getMethodNames();
			if (methods.length == 0)
			{
				logger.warn((new StringBuilder()).append("NO method found in service interface ").append(interfaceClass.getName()).toString());
				map.put("methods", "*");
			} else
			{
				map.put("methods", StringUtils.join(new HashSet(Arrays.asList(methods)), ","));
			}
		}
		if (!ConfigUtils.isEmpty(token))
			if (ConfigUtils.isDefault(token))
				map.put("token", UUID.randomUUID().toString());
			else
				map.put("token", token);
		if ("injvm".equals(protocolConfig.getName()))
		{
			protocolConfig.setRegister(Boolean.valueOf(false));
			map.put("notify", "false");
		}
		String contextPath = protocolConfig.getContextpath();
		if ((contextPath == null || contextPath.length() == 0) && provider != null)
			contextPath = provider.getContextpath();
		URL url = new URL(name, host, port.intValue(), (new StringBuilder()).append(contextPath != null && contextPath.length() != 0 ? (new StringBuilder()).append(contextPath).append("/").toString() : "").append(path).toString(), map);
		if (ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/ConfiguratorFactory).hasExtension(url.getProtocol()))
			url = ((ConfiguratorFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/ConfiguratorFactory).getExtension(url.getProtocol())).getConfigurator(url).configure(url);
		String scope = url.getParameter("scope");
		if (!"none".toString().equalsIgnoreCase(scope))
		{
			if (!"remote".toString().equalsIgnoreCase(scope))
				exportLocal(url);
			if (!"local".toString().equalsIgnoreCase(scope))
			{
				if (logger.isInfoEnabled())
					logger.info((new StringBuilder()).append("Export dubbo service ").append(interfaceClass.getName()).append(" to url ").append(url).toString());
				if (registryURLs != null && registryURLs.size() > 0 && url.getParameter("register", true))
				{
					Exporter exporter;
					for (Iterator i$ = registryURLs.iterator(); i$.hasNext(); exporters.add(exporter))
					{
						URL registryURL = (URL)i$.next();
						url = url.addParameterIfAbsent("dynamic", registryURL.getParameter("dynamic"));
						URL monitorUrl = loadMonitor(registryURL);
						if (monitorUrl != null)
							url = url.addParameterAndEncoded("monitor", monitorUrl.toFullString());
						if (logger.isInfoEnabled())
							logger.info((new StringBuilder()).append("Register dubbo service ").append(interfaceClass.getName()).append(" url ").append(url).append(" to registry ").append(registryURL).toString());
						com.autohome.turbo.rpc.Invoker invoker = proxyFactory.getInvoker(ref, interfaceClass, registryURL.addParameterAndEncoded("export", url.toFullString()));
						exporter = protocol.export(invoker);
					}

				} else
				{
					com.autohome.turbo.rpc.Invoker invoker = proxyFactory.getInvoker(ref, interfaceClass, url);
					Exporter exporter = protocol.export(invoker);
					exporters.add(exporter);
				}
			}
		}
		urls.add(url);
		return;
	}

	private void exportLocal(URL url)
	{
		if (!"injvm".equalsIgnoreCase(url.getProtocol()))
		{
			URL local = URL.valueOf(url.toFullString()).setProtocol("injvm").setHost("127.0.0.1").setPort(0);
			ServiceClassHolder.getInstance().pushServiceClass(getServiceClass(ref));
			Exporter exporter = protocol.export(proxyFactory.getInvoker(ref, interfaceClass, local));
			exporters.add(exporter);
			logger.info((new StringBuilder()).append("Export dubbo service ").append(interfaceClass.getName()).append(" to local registry").toString());
		}
	}

	protected Class getServiceClass(Object ref)
	{
		return ref.getClass();
	}

	private void checkDefault()
	{
		if (provider == null)
			provider = new ProviderConfig();
		appendProperties(provider);
	}

	private void checkProtocol()
	{
		if ((protocols == null || protocols.size() == 0) && provider != null)
			setProtocols(provider.getProtocols());
		if (protocols == null || protocols.size() == 0)
			setProtocol(new ProtocolConfig());
		ProtocolConfig protocolConfig;
		for (Iterator i$ = protocols.iterator(); i$.hasNext(); appendProperties(protocolConfig))
		{
			protocolConfig = (ProtocolConfig)i$.next();
			if (StringUtils.isEmpty(protocolConfig.getName()))
				protocolConfig.setName("dubbo");
		}

	}

	public Class getInterfaceClass()
	{
		if (interfaceClass != null)
			return interfaceClass;
		if (ref instanceof GenericService)
			return com/autohome/turbo/rpc/service/GenericService;
		try
		{
			if (interfaceName != null && interfaceName.length() > 0)
				interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
		}
		catch (ClassNotFoundException t)
		{
			throw new IllegalStateException(t.getMessage(), t);
		}
		return interfaceClass;
	}

	/**
	 * @deprecated Method setInterfaceClass is deprecated
	 */

	public void setInterfaceClass(Class interfaceClass)
	{
		setInterface(interfaceClass);
	}

	public String getInterface()
	{
		return interfaceName;
	}

	public void setInterface(String interfaceName)
	{
		this.interfaceName = interfaceName;
		if (id == null || id.length() == 0)
			id = interfaceName;
	}

	public void setInterface(Class interfaceClass)
	{
		if (interfaceClass != null && !interfaceClass.isInterface())
		{
			throw new IllegalStateException((new StringBuilder()).append("The interface class ").append(interfaceClass).append(" is not a interface!").toString());
		} else
		{
			this.interfaceClass = interfaceClass;
			setInterface(interfaceClass != null ? interfaceClass.getName() : (String)null);
			return;
		}
	}

	public Object getRef()
	{
		return ref;
	}

	public void setRef(Object ref)
	{
		this.ref = ref;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		checkPathName("path", path);
		this.path = path;
	}

	public List getMethods()
	{
		return methods;
	}

	public void setMethods(List methods)
	{
		this.methods = methods;
	}

	public ProviderConfig getProvider()
	{
		return provider;
	}

	public void setGeneric(String generic)
	{
		if (StringUtils.isEmpty(generic))
			return;
		if (ProtocolUtils.isGeneric(generic))
			this.generic = generic;
		else
			throw new IllegalArgumentException((new StringBuilder()).append("Unsupported generic type ").append(generic).toString());
	}

	public String getGeneric()
	{
		return generic;
	}

	public void setProvider(ProviderConfig provider)
	{
		this.provider = provider;
	}

	public List getExportedUrls()
	{
		return urls;
	}

	/**
	 * @deprecated Method getProviders is deprecated
	 */

	public List getProviders()
	{
		return convertProtocolToProvider(protocols);
	}

	/**
	 * @deprecated Method setProviders is deprecated
	 */

	public void setProviders(List providers)
	{
		protocols = convertProviderToProtocol(providers);
	}

	/**
	 * @deprecated Method convertProviderToProtocol is deprecated
	 */

	private static final List convertProviderToProtocol(List providers)
	{
		if (providers == null || providers.size() == 0)
			return null;
		List protocols = new ArrayList(providers.size());
		ProviderConfig provider;
		for (Iterator i$ = providers.iterator(); i$.hasNext(); protocols.add(convertProviderToProtocol(provider)))
			provider = (ProviderConfig)i$.next();

		return protocols;
	}

	/**
	 * @deprecated Method convertProtocolToProvider is deprecated
	 */

	private static final List convertProtocolToProvider(List protocols)
	{
		if (protocols == null || protocols.size() == 0)
			return null;
		List providers = new ArrayList(protocols.size());
		ProtocolConfig provider;
		for (Iterator i$ = protocols.iterator(); i$.hasNext(); providers.add(convertProtocolToProvider(provider)))
			provider = (ProtocolConfig)i$.next();

		return providers;
	}

	/**
	 * @deprecated Method convertProviderToProtocol is deprecated
	 */

	private static final ProtocolConfig convertProviderToProtocol(ProviderConfig provider)
	{
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName(provider.getProtocol().getName());
		protocol.setServer(provider.getServer());
		protocol.setClient(provider.getClient());
		protocol.setCodec(provider.getCodec());
		protocol.setHost(provider.getHost());
		protocol.setPort(provider.getPort());
		protocol.setPath(provider.getPath());
		protocol.setPayload(provider.getPayload());
		protocol.setThreads(provider.getThreads());
		protocol.setParameters(provider.getParameters());
		return protocol;
	}

	/**
	 * @deprecated Method convertProtocolToProvider is deprecated
	 */

	private static final ProviderConfig convertProtocolToProvider(ProtocolConfig protocol)
	{
		ProviderConfig provider = new ProviderConfig();
		provider.setProtocol(protocol);
		provider.setServer(protocol.getServer());
		provider.setClient(protocol.getClient());
		provider.setCodec(protocol.getCodec());
		provider.setHost(protocol.getHost());
		provider.setPort(protocol.getPort());
		provider.setPath(protocol.getPath());
		provider.setPayload(protocol.getPayload());
		provider.setThreads(protocol.getThreads());
		provider.setParameters(protocol.getParameters());
		return provider;
	}

	private static Integer getRandomPort(String protocol)
	{
		protocol = protocol.toLowerCase();
		if (RANDOM_PORT_MAP.containsKey(protocol))
			return (Integer)RANDOM_PORT_MAP.get(protocol);
		else
			return Integer.valueOf(0x80000000);
	}

	private static void putRandomPort(String protocol, Integer port)
	{
		protocol = protocol.toLowerCase();
		if (!RANDOM_PORT_MAP.containsKey(protocol))
			RANDOM_PORT_MAP.put(protocol, port);
	}

}
