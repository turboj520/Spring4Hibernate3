// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StubProxyFactoryWrapper.java

package com.autohome.turbo.rpc.proxy.wrapper;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.service.GenericService;
import java.lang.reflect.Constructor;

public class StubProxyFactoryWrapper
	implements ProxyFactory
{

	private static final Logger LOGGER = LoggerFactory.getLogger(com/autohome/turbo/rpc/proxy/wrapper/StubProxyFactoryWrapper);
	private final ProxyFactory proxyFactory;
	private Protocol protocol;

	public StubProxyFactoryWrapper(ProxyFactory proxyFactory)
	{
		this.proxyFactory = proxyFactory;
	}

	public void setProtocol(Protocol protocol)
	{
		this.protocol = protocol;
	}

	public Object getProxy(Invoker invoker)
		throws RpcException
	{
		Object proxy = proxyFactory.getProxy(invoker);
		if (com/autohome/turbo/rpc/service/GenericService != invoker.getInterface())
		{
			String stub = invoker.getUrl().getParameter("stub", invoker.getUrl().getParameter("local"));
			if (ConfigUtils.isNotEmpty(stub))
			{
				Class serviceType = invoker.getInterface();
				if (ConfigUtils.isDefault(stub))
					if (invoker.getUrl().hasParameter("stub"))
						stub = (new StringBuilder()).append(serviceType.getName()).append("Stub").toString();
					else
						stub = (new StringBuilder()).append(serviceType.getName()).append("Local").toString();
				try
				{
					Class stubClass = ReflectUtils.forName(stub);
					if (!serviceType.isAssignableFrom(stubClass))
						throw new IllegalStateException((new StringBuilder()).append("The stub implemention class ").append(stubClass.getName()).append(" not implement interface ").append(serviceType.getName()).toString());
					try
					{
						Constructor constructor = ReflectUtils.findConstructor(stubClass, serviceType);
						proxy = constructor.newInstance(new Object[] {
							proxy
						});
						URL url = invoker.getUrl();
						if (url.getParameter("dubbo.stub.event", false))
						{
							url = url.addParameter("dubbo.stub.event.methods", StringUtils.join(Wrapper.getWrapper(proxy.getClass()).getDeclaredMethodNames(), ","));
							url = url.addParameter("isserver", Boolean.FALSE.toString());
							try
							{
								export(proxy, invoker.getInterface(), url);
							}
							catch (Exception e)
							{
								LOGGER.error("export a stub service error.", e);
							}
						}
					}
					catch (NoSuchMethodException e)
					{
						throw new IllegalStateException((new StringBuilder()).append("No such constructor \"public ").append(stubClass.getSimpleName()).append("(").append(serviceType.getName()).append(")\" in stub implemention class ").append(stubClass.getName()).toString(), e);
					}
				}
				catch (Throwable t)
				{
					LOGGER.error((new StringBuilder()).append("Failed to create stub implemention class ").append(stub).append(" in consumer ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).append(", cause: ").append(t.getMessage()).toString(), t);
				}
			}
		}
		return proxy;
	}

	public Invoker getInvoker(Object proxy, Class type, URL url)
		throws RpcException
	{
		return proxyFactory.getInvoker(proxy, type, url);
	}

	private Exporter export(Object instance, Class type, URL url)
	{
		return protocol.export(proxyFactory.getInvoker(instance, type, url));
	}

}
