// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractInterfaceConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.monitor.MonitorFactory;
import com.autohome.turbo.monitor.MonitorService;
import com.autohome.turbo.registry.RegistryFactory;
import com.autohome.turbo.registry.RegistryService;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Cluster;
import com.autohome.turbo.rpc.support.MockInvoker;
import java.lang.reflect.Method;
import java.util.*;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractMethodConfig, RegistryConfig, ApplicationConfig, MonitorConfig, 
//			MethodConfig, ModuleConfig

public abstract class AbstractInterfaceConfig extends AbstractMethodConfig
{

	private static final long serialVersionUID = 0xea5c3413ecad6ac3L;
	protected String local;
	protected String stub;
	protected MonitorConfig monitor;
	protected String proxy;
	protected String cluster;
	protected String filter;
	protected String listener;
	protected String owner;
	protected Integer connections;
	protected String layer;
	protected ApplicationConfig application;
	protected ModuleConfig module;
	protected List registries;
	private Integer callbacks;
	protected String onconnect;
	protected String ondisconnect;
	private String scope;
	protected boolean trace;

	public AbstractInterfaceConfig()
	{
	}

	protected void checkRegistry()
	{
		if (registries == null || registries.size() == 0)
		{
			String address = ConfigUtils.getProperty("dubbo.registry.address");
			if (address != null && address.length() > 0)
			{
				registries = new ArrayList();
				String as[] = address.split("\\s*[|]+\\s*");
				String arr$[] = as;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String a = arr$[i$];
					RegistryConfig registryConfig = new RegistryConfig();
					registryConfig.setAddress(a);
					registries.add(registryConfig);
				}

			}
		}
		if (registries == null || registries.size() == 0)
			throw new IllegalStateException((new StringBuilder()).append(getClass().getSimpleName().startsWith("Reference") ? "No such any registry to refer service in consumer " : "No such any registry to export service in provider ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).append(", Please add <dubbo:registry address=\"...\" /> to your spring config. If you want unregister, please set <dubbo:service registry=\"N/A\" />").toString());
		RegistryConfig registryConfig;
		for (Iterator i$ = registries.iterator(); i$.hasNext(); appendProperties(registryConfig))
			registryConfig = (RegistryConfig)i$.next();

	}

	protected void checkApplication()
	{
		if (application == null)
		{
			String applicationName = ConfigUtils.getProperty("dubbo.application.name");
			if (applicationName != null && applicationName.length() > 0)
				application = new ApplicationConfig();
		}
		if (application == null)
			throw new IllegalStateException("No such application config! Please add <dubbo:application name=\"...\" /> to your spring config.");
		appendProperties(application);
		String wait = ConfigUtils.getProperty("dubbo.service.shutdown.wait");
		if (wait != null && wait.trim().length() > 0)
		{
			System.setProperty("dubbo.service.shutdown.wait", wait.trim());
		} else
		{
			wait = ConfigUtils.getProperty("dubbo.service.shutdown.wait.seconds");
			if (wait != null && wait.trim().length() > 0)
				System.setProperty("dubbo.service.shutdown.wait.seconds", wait.trim());
		}
	}

	protected List loadRegistries(boolean provider)
	{
		checkRegistry();
		List registryList = new ArrayList();
		if (registries != null && registries.size() > 0)
		{
			for (Iterator i$ = registries.iterator(); i$.hasNext();)
			{
				RegistryConfig config = (RegistryConfig)i$.next();
				String address = config.getAddress();
				if (address == null || address.length() == 0)
					address = "0.0.0.0";
				String sysaddress = System.getProperty("dubbo.registry.address");
				if (sysaddress != null && sysaddress.length() > 0)
					address = sysaddress;
				if (address != null && address.length() > 0 && !"N/A".equalsIgnoreCase(address))
				{
					Map map = new HashMap();
					appendParameters(map, application);
					appendParameters(map, config);
					map.put("path", com/autohome/turbo/registry/RegistryService.getName());
					map.put("dubbo", Version.getVersion());
					map.put("timestamp", String.valueOf(System.currentTimeMillis()));
					if (ConfigUtils.getPid() > 0)
						map.put("pid", String.valueOf(ConfigUtils.getPid()));
					if (!map.containsKey("protocol"))
						if (ExtensionLoader.getExtensionLoader(com/autohome/turbo/registry/RegistryFactory).hasExtension("remote"))
							map.put("protocol", "remote");
						else
							map.put("protocol", "dubbo");
					List urls = UrlUtils.parseURLs(address, map);
					Iterator i$ = urls.iterator();
					while (i$.hasNext()) 
					{
						URL url = (URL)i$.next();
						url = url.addParameter("registry", url.getProtocol());
						url = url.setProtocol("registry");
						if (provider && url.getParameter("register", true) || !provider && url.getParameter("subscribe", true))
							registryList.add(url);
					}
				}
			}

		}
		return registryList;
	}

	protected URL loadMonitor(URL registryURL)
	{
		if (monitor == null)
		{
			String monitorAddress = ConfigUtils.getProperty("dubbo.monitor.address");
			String monitorProtocol = ConfigUtils.getProperty("dubbo.monitor.protocol");
			if (monitorAddress != null && monitorAddress.length() > 0 || monitorProtocol != null && monitorProtocol.length() > 0)
				monitor = new MonitorConfig();
			else
				return null;
		}
		appendProperties(monitor);
		Map map = new HashMap();
		map.put("interface", com/autohome/turbo/monitor/MonitorService.getName());
		map.put("dubbo", Version.getVersion());
		map.put("timestamp", String.valueOf(System.currentTimeMillis()));
		if (ConfigUtils.getPid() > 0)
			map.put("pid", String.valueOf(ConfigUtils.getPid()));
		appendParameters(map, monitor);
		String address = monitor.getAddress();
		String sysaddress = System.getProperty("dubbo.monitor.address");
		if (sysaddress != null && sysaddress.length() > 0)
			address = sysaddress;
		if (ConfigUtils.isNotEmpty(address))
		{
			if (!map.containsKey("protocol"))
				if (ExtensionLoader.getExtensionLoader(com/autohome/turbo/monitor/MonitorFactory).hasExtension("logstat"))
					map.put("protocol", "logstat");
				else
					map.put("protocol", "dubbo");
			return UrlUtils.parseURL(address, map);
		}
		if ("registry".equals(monitor.getProtocol()) && registryURL != null)
			return registryURL.setProtocol("dubbo").addParameter("protocol", "registry").addParameterAndEncoded("refer", StringUtils.toQueryString(map));
		else
			return null;
	}

	protected void checkInterfaceAndMethods(Class interfaceClass, List methods)
	{
label0:
		{
			if (interfaceClass == null)
				throw new IllegalStateException("interface not allow null!");
			if (!interfaceClass.isInterface())
				throw new IllegalStateException((new StringBuilder()).append("The interface class ").append(interfaceClass).append(" is not a interface!").toString());
			if (methods == null || methods.size() <= 0)
				break label0;
			Iterator i$ = methods.iterator();
			String methodName;
			boolean hasMethod;
			do
			{
				if (!i$.hasNext())
					break label0;
				MethodConfig methodBean = (MethodConfig)i$.next();
				methodName = methodBean.getName();
				if (methodName == null || methodName.length() == 0)
					throw new IllegalStateException((new StringBuilder()).append("<dubbo:method> name attribute is required! Please check: <dubbo:service interface=\"").append(interfaceClass.getName()).append("\" ... ><dubbo:method name=\"\" ... /></<dubbo:reference>").toString());
				hasMethod = false;
				Method arr$[] = interfaceClass.getMethods();
				int len$ = arr$.length;
				int i$ = 0;
				do
				{
					if (i$ >= len$)
						break;
					Method method = arr$[i$];
					if (method.getName().equals(methodName))
					{
						hasMethod = true;
						break;
					}
					i$++;
				} while (true);
			} while (hasMethod);
			throw new IllegalStateException((new StringBuilder()).append("The interface ").append(interfaceClass.getName()).append(" not found method ").append(methodName).toString());
		}
	}

	protected void checkStubAndMock(Class interfaceClass)
	{
		if (ConfigUtils.isNotEmpty(local))
		{
			Class localClass = ConfigUtils.isDefault(local) ? ReflectUtils.forName((new StringBuilder()).append(interfaceClass.getName()).append("Local").toString()) : ReflectUtils.forName(local);
			if (!interfaceClass.isAssignableFrom(localClass))
				throw new IllegalStateException((new StringBuilder()).append("The local implemention class ").append(localClass.getName()).append(" not implement interface ").append(interfaceClass.getName()).toString());
			try
			{
				ReflectUtils.findConstructor(localClass, interfaceClass);
			}
			catch (NoSuchMethodException e)
			{
				throw new IllegalStateException((new StringBuilder()).append("No such constructor \"public ").append(localClass.getSimpleName()).append("(").append(interfaceClass.getName()).append(")\" in local implemention class ").append(localClass.getName()).toString());
			}
		}
		if (ConfigUtils.isNotEmpty(stub))
		{
			Class localClass = ConfigUtils.isDefault(stub) ? ReflectUtils.forName((new StringBuilder()).append(interfaceClass.getName()).append("Stub").toString()) : ReflectUtils.forName(stub);
			if (!interfaceClass.isAssignableFrom(localClass))
				throw new IllegalStateException((new StringBuilder()).append("The local implemention class ").append(localClass.getName()).append(" not implement interface ").append(interfaceClass.getName()).toString());
			try
			{
				ReflectUtils.findConstructor(localClass, interfaceClass);
			}
			catch (NoSuchMethodException e)
			{
				throw new IllegalStateException((new StringBuilder()).append("No such constructor \"public ").append(localClass.getSimpleName()).append("(").append(interfaceClass.getName()).append(")\" in local implemention class ").append(localClass.getName()).toString());
			}
		}
		if (ConfigUtils.isNotEmpty(mock))
			if (mock.startsWith("return "))
			{
				String value = mock.substring("return ".length());
				try
				{
					MockInvoker.parseMockValue(value);
				}
				catch (Exception e)
				{
					throw new IllegalStateException((new StringBuilder()).append("Illegal mock json value in <dubbo:service ... mock=\"").append(mock).append("\" />").toString());
				}
			} else
			{
				Class mockClass = ConfigUtils.isDefault(mock) ? ReflectUtils.forName((new StringBuilder()).append(interfaceClass.getName()).append("Mock").toString()) : ReflectUtils.forName(mock);
				if (!interfaceClass.isAssignableFrom(mockClass))
					throw new IllegalStateException((new StringBuilder()).append("The mock implemention class ").append(mockClass.getName()).append(" not implement interface ").append(interfaceClass.getName()).toString());
				try
				{
					mockClass.getConstructor(new Class[0]);
				}
				catch (NoSuchMethodException e)
				{
					throw new IllegalStateException((new StringBuilder()).append("No such empty constructor \"public ").append(mockClass.getSimpleName()).append("()\" in mock implemention class ").append(mockClass.getName()).toString());
				}
			}
	}

	/**
	 * @deprecated Method getLocal is deprecated
	 */

	public String getLocal()
	{
		return local;
	}

	/**
	 * @deprecated Method setLocal is deprecated
	 */

	public void setLocal(String local)
	{
		checkName("local", local);
		this.local = local;
	}

	/**
	 * @deprecated Method setLocal is deprecated
	 */

	public void setLocal(Boolean local)
	{
		if (local == null)
			setLocal((String)null);
		else
			setLocal(String.valueOf(local));
	}

	public String getStub()
	{
		return stub;
	}

	public void setStub(String stub)
	{
		checkName("stub", stub);
		this.stub = stub;
	}

	public void setStub(Boolean stub)
	{
		if (local == null)
			setStub((String)null);
		else
			setStub(String.valueOf(stub));
	}

	public String getCluster()
	{
		return cluster;
	}

	public void setCluster(String cluster)
	{
		checkExtension(com/autohome/turbo/rpc/cluster/Cluster, "cluster", cluster);
		this.cluster = cluster;
	}

	public String getProxy()
	{
		return proxy;
	}

	public void setProxy(String proxy)
	{
		checkExtension(com/autohome/turbo/rpc/ProxyFactory, "proxy", proxy);
		this.proxy = proxy;
	}

	public Integer getConnections()
	{
		return connections;
	}

	public void setConnections(Integer connections)
	{
		this.connections = connections;
	}

	public String getFilter()
	{
		return filter;
	}

	public void setFilter(String filter)
	{
		checkMultiExtension(com/autohome/turbo/rpc/Filter, "filter", filter);
		this.filter = filter;
	}

	public String getListener()
	{
		checkMultiExtension(com/autohome/turbo/rpc/InvokerListener, "listener", listener);
		return listener;
	}

	public void setListener(String listener)
	{
		this.listener = listener;
	}

	public String getLayer()
	{
		return layer;
	}

	public void setLayer(String layer)
	{
		checkNameHasSymbol("layer", layer);
		this.layer = layer;
	}

	public ApplicationConfig getApplication()
	{
		return application;
	}

	public void setApplication(ApplicationConfig application)
	{
		this.application = application;
	}

	public ModuleConfig getModule()
	{
		return module;
	}

	public void setModule(ModuleConfig module)
	{
		this.module = module;
	}

	public RegistryConfig getRegistry()
	{
		return registries != null && registries.size() != 0 ? (RegistryConfig)registries.get(0) : null;
	}

	public void setRegistry(RegistryConfig registry)
	{
		List registries = new ArrayList(1);
		registries.add(registry);
		this.registries = registries;
	}

	public List getRegistries()
	{
		return registries;
	}

	public void setRegistries(List registries)
	{
		this.registries = registries;
	}

	public MonitorConfig getMonitor()
	{
		return monitor;
	}

	public void setMonitor(MonitorConfig monitor)
	{
		this.monitor = monitor;
	}

	public void setMonitor(String monitor)
	{
		this.monitor = new MonitorConfig(monitor);
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		checkMultiName("owner", owner);
		this.owner = owner;
	}

	public void setCallbacks(Integer callbacks)
	{
		this.callbacks = callbacks;
	}

	public Integer getCallbacks()
	{
		return callbacks;
	}

	public String getOnconnect()
	{
		return onconnect;
	}

	public void setOnconnect(String onconnect)
	{
		this.onconnect = onconnect;
	}

	public String getOndisconnect()
	{
		return ondisconnect;
	}

	public void setOndisconnect(String ondisconnect)
	{
		this.ondisconnect = ondisconnect;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public boolean isTrace()
	{
		return trace;
	}

	public void setTrace(boolean trace)
	{
		this.trace = trace;
	}
}
