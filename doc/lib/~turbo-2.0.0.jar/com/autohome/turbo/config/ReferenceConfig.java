// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReferenceConfig.java

package com.autohome.turbo.config;

import com.autohome.turbo.common.*;
import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.config.annotation.Reference;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Cluster;
import com.autohome.turbo.rpc.cluster.directory.StaticDirectory;
import com.autohome.turbo.rpc.cluster.support.ClusterUtils;
import com.autohome.turbo.rpc.protocol.injvm.InjvmProtocol;
import com.autohome.turbo.rpc.service.GenericService;
import com.autohome.turbo.rpc.support.ProtocolUtils;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.config:
//			AbstractReferenceConfig, MethodConfig, ConsumerConfig, ModuleConfig, 
//			ApplicationConfig, AbstractConfig

public class ReferenceConfig extends AbstractReferenceConfig
{

	private static final long serialVersionUID = 0xae9da3b55db4adf5L;
	private static final Protocol refprotocol = (Protocol)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getAdaptiveExtension();
	private static final Cluster cluster = (Cluster)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/Cluster).getAdaptiveExtension();
	private static final ProxyFactory proxyFactory = (ProxyFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/ProxyFactory).getAdaptiveExtension();
	private String interfaceName;
	private Class interfaceClass;
	private String client;
	private String url;
	private String csharp;
	private List methods;
	private ConsumerConfig consumer;
	private String protocol;
	private volatile transient Object ref;
	private volatile transient Invoker invoker;
	private volatile transient boolean initialized;
	private volatile transient boolean destroyed;
	private final List urls;
	private final Object finalizerGuardian;

	public ReferenceConfig()
	{
		urls = new ArrayList();
		finalizerGuardian = new Object() {

			final ReferenceConfig this$0;

			protected void finalize()
				throws Throwable
			{
				super.finalize();
				if (!destroyed)
					AbstractConfig.logger.warn((new StringBuilder()).append("ReferenceConfig(").append(url).append(") is not DESTROYED when FINALIZE").toString());
			}

			
			{
				this$0 = ReferenceConfig.this;
				super();
			}
		};
	}

	public ReferenceConfig(Reference reference)
	{
		urls = new ArrayList();
		finalizerGuardian = new 1();
		appendAnnotation(com/autohome/turbo/config/annotation/Reference, reference);
	}

	public URL toUrl()
	{
		return urls != null && urls.size() != 0 ? (URL)urls.iterator().next() : null;
	}

	public List toUrls()
	{
		return urls;
	}

	public synchronized Object get()
	{
		if (destroyed)
			throw new IllegalStateException("Already destroyed!");
		if (ref == null)
			init();
		return ref;
	}

	public synchronized void destroy()
	{
		if (ref == null)
			return;
		if (destroyed)
			return;
		destroyed = true;
		try
		{
			invoker.destroy();
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("Unexpected err when destroy invoker of ReferenceConfig(").append(url).append(").").toString(), t);
		}
		invoker = null;
		ref = null;
	}

	private void init()
	{
		String resolve;
		String resolveFile;
		Properties properties;
		FileInputStream fis;
		Exception exception;
		if (initialized)
			return;
		initialized = true;
		if (interfaceName == null || interfaceName.length() == 0)
			throw new IllegalStateException("<dubbo:reference interface=\"\" /> interface not allow null!");
		checkDefault();
		appendProperties(this);
		if (getGeneric() == null && getConsumer() != null)
			setGeneric(getConsumer().getGeneric());
		if (ProtocolUtils.isGeneric(getGeneric()))
		{
			interfaceClass = com/autohome/turbo/rpc/service/GenericService;
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
			checkInterfaceAndMethods(interfaceClass, this.methods);
		}
		resolve = System.getProperty(interfaceName);
		resolveFile = null;
		if (resolve != null && resolve.length() != 0)
			break MISSING_BLOCK_LABEL_380;
		resolveFile = System.getProperty("dubbo.resolve.file");
		if (resolveFile == null || resolveFile.length() == 0)
		{
			File userResolveFile = new File(new File(System.getProperty("user.home")), "dubbo-resolve.properties");
			if (userResolveFile.exists())
				resolveFile = userResolveFile.getAbsolutePath();
		}
		if (resolveFile == null || resolveFile.length() <= 0)
			break MISSING_BLOCK_LABEL_380;
		properties = new Properties();
		fis = null;
		try
		{
			fis = new FileInputStream(new File(resolveFile));
			properties.load(fis);
		}
		catch (IOException e)
		{
			throw new IllegalStateException((new StringBuilder()).append("Unload ").append(resolveFile).append(", cause: ").append(e.getMessage()).toString(), e);
		}
		finally { }
		try
		{
			if (null != fis)
				fis.close();
		}
		catch (IOException e)
		{
			logger.warn(e.getMessage(), e);
		}
		break MISSING_BLOCK_LABEL_371;
		try
		{
			if (null != fis)
				fis.close();
		}
		catch (IOException e)
		{
			logger.warn(e.getMessage(), e);
		}
		throw exception;
		resolve = properties.getProperty(interfaceName);
		if (resolve != null && resolve.length() > 0)
		{
			url = resolve;
			if (logger.isWarnEnabled())
				if (resolveFile != null && resolveFile.length() > 0)
					logger.warn((new StringBuilder()).append("Using default dubbo resolve file ").append(resolveFile).append(" replace ").append(interfaceName).append("").append(resolve).append(" to p2p invoke remote service.").toString());
				else
					logger.warn((new StringBuilder()).append("Using -D").append(interfaceName).append("=").append(resolve).append(" to p2p invoke remote service.").toString());
		}
		if (consumer != null)
		{
			if (application == null)
				application = consumer.getApplication();
			if (module == null)
				module = consumer.getModule();
			if (registries == null)
				registries = consumer.getRegistries();
			if (monitor == null)
				monitor = consumer.getMonitor();
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
		checkApplication();
		checkStubAndMock(interfaceClass);
		Map map = new HashMap();
		Map attributes = new HashMap();
		map.put("side", "consumer");
		map.put("dubbo", Version.getVersion());
		map.put("timestamp", String.valueOf(System.currentTimeMillis()));
		if (ConfigUtils.getPid() > 0)
			map.put("pid", String.valueOf(ConfigUtils.getPid()));
		if (!isGeneric().booleanValue())
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
		map.put("interface", interfaceName);
		appendParameters(map, application);
		appendParameters(map, module);
		appendParameters(map, consumer, "default");
		appendParameters(map, this);
		String prifix = StringUtils.getServiceKey(map);
		if (this.methods != null && this.methods.size() > 0)
		{
			MethodConfig method;
			for (Iterator i$ = this.methods.iterator(); i$.hasNext(); checkAndConvertImplicitConfig(method, map, attributes))
			{
				method = (MethodConfig)i$.next();
				appendParameters(map, method, method.getName());
				String retryKey = (new StringBuilder()).append(method.getName()).append(".retry").toString();
				if (map.containsKey(retryKey))
				{
					String retryValue = (String)map.remove(retryKey);
					if ("false".equals(retryValue))
						map.put((new StringBuilder()).append(method.getName()).append(".retries").toString(), "0");
				}
				appendAttributes(attributes, method, (new StringBuilder()).append(prifix).append(".").append(method.getName()).toString());
			}

		}
		StaticContext.getSystemContext().putAll(attributes);
		ref = createProxy(map);
		return;
	}

	private static void checkAndConvertImplicitConfig(MethodConfig method, Map map, Map attributes)
	{
		if (Boolean.FALSE.equals(method.isReturn()) && (method.getOnreturn() != null || method.getOnthrow() != null))
			throw new IllegalStateException("method config error : return attribute must be set true when onreturn or onthrow has been setted.");
		String onReturnMethodKey = StaticContext.getKey(map, method.getName(), "onreturn.method");
		Object onReturnMethod = attributes.get(onReturnMethodKey);
		if (onReturnMethod != null && (onReturnMethod instanceof String))
			attributes.put(onReturnMethodKey, getMethodByName(method.getOnreturn().getClass(), onReturnMethod.toString()));
		String onThrowMethodKey = StaticContext.getKey(map, method.getName(), "onthrow.method");
		Object onThrowMethod = attributes.get(onThrowMethodKey);
		if (onThrowMethod != null && (onThrowMethod instanceof String))
			attributes.put(onThrowMethodKey, getMethodByName(method.getOnthrow().getClass(), onThrowMethod.toString()));
		String onInvokeMethodKey = StaticContext.getKey(map, method.getName(), "oninvoke.method");
		Object onInvokeMethod = attributes.get(onInvokeMethodKey);
		if (onInvokeMethod != null && (onInvokeMethod instanceof String))
			attributes.put(onInvokeMethodKey, getMethodByName(method.getOninvoke().getClass(), onInvokeMethod.toString()));
	}

	private static Method getMethodByName(Class clazz, String methodName)
	{
		return ReflectUtils.findMethodByMethodName(clazz, methodName);
		Exception e;
		e;
		throw new IllegalStateException(e);
	}

	private Object createProxy(Map map)
	{
		URL tmpUrl = new URL("temp", "localhost", 0, map);
		boolean isJvmRefer;
		if (isInjvm() == null)
		{
			if (this.url != null && this.url.length() > 0)
				isJvmRefer = false;
			else
			if (InjvmProtocol.getInjvmProtocol().isInjvmRefer(tmpUrl))
				isJvmRefer = true;
			else
				isJvmRefer = false;
		} else
		{
			isJvmRefer = isInjvm().booleanValue();
		}
		if (isJvmRefer)
		{
			URL url = (new URL("injvm", "127.0.0.1", 0, interfaceClass.getName())).addParameters(map);
			invoker = refprotocol.refer(interfaceClass, url);
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Using injvm service ").append(interfaceClass.getName()).toString());
		} else
		{
			if (this.url != null && this.url.length() > 0)
			{
				String us[] = Constants.SEMICOLON_SPLIT_PATTERN.split(this.url);
				if (us != null && us.length > 0)
				{
					String arr$[] = us;
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						String u = arr$[i$];
						URL url = URL.valueOf(u);
						if (url.getPath() == null || url.getPath().length() == 0)
							url = url.setPath(interfaceName);
						if ("registry".equals(url.getProtocol()))
							urls.add(url.addParameterAndEncoded("refer", StringUtils.toQueryString(map)));
						else
							urls.add(ClusterUtils.mergeUrl(url, map));
					}

				}
			} else
			{
				List us = loadRegistries(false);
				if (us != null && us.size() > 0)
				{
					URL u;
					for (Iterator i$ = us.iterator(); i$.hasNext(); urls.add(u.addParameterAndEncoded("refer", StringUtils.toQueryString(map))))
					{
						u = (URL)i$.next();
						URL monitorUrl = loadMonitor(u);
						if (monitorUrl != null)
							map.put("monitor", URL.encode(monitorUrl.toFullString()));
					}

				}
				if (urls == null || urls.size() == 0)
					throw new IllegalStateException((new StringBuilder()).append("No such any registry to reference ").append(interfaceName).append(" on the consumer ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).append(", please config <dubbo:registry address=\"...\" /> to your spring config.").toString());
			}
			if (urls.size() == 1)
			{
				invoker = refprotocol.refer(interfaceClass, (URL)urls.get(0));
			} else
			{
				List invokers = new ArrayList();
				URL registryURL = null;
				Iterator i$ = urls.iterator();
				do
				{
					if (!i$.hasNext())
						break;
					URL url = (URL)i$.next();
					invokers.add(refprotocol.refer(interfaceClass, url));
					if ("registry".equals(url.getProtocol()))
						registryURL = url;
				} while (true);
				if (registryURL != null)
				{
					URL u = registryURL.addParameter("cluster", "available");
					invoker = cluster.join(new StaticDirectory(u, invokers));
				} else
				{
					invoker = cluster.join(new StaticDirectory(invokers));
				}
			}
		}
		Boolean c = check;
		if (c == null && consumer != null)
			c = consumer.isCheck();
		if (c == null)
			c = Boolean.valueOf(true);
		if (c.booleanValue() && !invoker.isAvailable())
			throw new IllegalStateException((new StringBuilder()).append("Failed to check the status of the service ").append(interfaceName).append(". No provider available for the service ").append(group != null ? (new StringBuilder()).append(group).append("/").toString() : "").append(interfaceName).append(version != null ? (new StringBuilder()).append(":").append(version).toString() : "").append(" from the url ").append(invoker.getUrl()).append(" to the consumer ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).toString());
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Refer dubbo service ").append(interfaceClass.getName()).append(" from url ").append(invoker.getUrl()).toString());
		return proxyFactory.getProxy(invoker);
	}

	private void checkDefault()
	{
		if (consumer == null)
			consumer = new ConsumerConfig();
		appendProperties(consumer);
	}

	public Class getInterfaceClass()
	{
		if (interfaceClass != null)
			return interfaceClass;
		if (isGeneric().booleanValue() || getConsumer() != null && getConsumer().isGeneric().booleanValue())
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

	public String getClient()
	{
		return client;
	}

	public void setClient(String client)
	{
		checkName("client", client);
		this.client = client;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public List getMethods()
	{
		return methods;
	}

	public void setMethods(List methods)
	{
		this.methods = methods;
	}

	public ConsumerConfig getConsumer()
	{
		return consumer;
	}

	public void setConsumer(ConsumerConfig consumer)
	{
		this.consumer = consumer;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	Invoker getInvoker()
	{
		return invoker;
	}

	public String getCsharp()
	{
		return csharp;
	}

	public void setCsharp(String csharp)
	{
		this.csharp = csharp;
	}



}
