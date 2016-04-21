// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ProtocolFilterWrapper.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.rpc.*;
import java.util.List;

public class ProtocolFilterWrapper
	implements Protocol
{

	private final Protocol protocol;

	public ProtocolFilterWrapper(Protocol protocol)
	{
		if (protocol == null)
		{
			throw new IllegalArgumentException("protocol == null");
		} else
		{
			this.protocol = protocol;
			return;
		}
	}

	public int getDefaultPort()
	{
		return protocol.getDefaultPort();
	}

	public Exporter export(Invoker invoker)
		throws RpcException
	{
		if ("registry".equals(invoker.getUrl().getProtocol()))
			return protocol.export(invoker);
		else
			return protocol.export(buildInvokerChain(invoker, "service.filter", "provider"));
	}

	public Invoker refer(Class type, URL url)
		throws RpcException
	{
		if ("registry".equals(url.getProtocol()))
			return protocol.refer(type, url);
		else
			return buildInvokerChain(protocol.refer(type, url), "reference.filter", "consumer");
	}

	public void destroy()
	{
		protocol.destroy();
	}

	private static Invoker buildInvokerChain(Invoker invoker, String key, String group)
	{
		Invoker last = invoker;
		List filters = ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Filter).getActivateExtension(invoker.getUrl(), key, group);
		if (filters.size() > 0)
		{
			for (int i = filters.size() - 1; i >= 0; i--)
			{
				Filter filter = (Filter)filters.get(i);
				Invoker next = last;
				last = new Invoker(invoker, filter, next) {

					final Invoker val$invoker;
					final Filter val$filter;
					final Invoker val$next;

					public Class getInterface()
					{
						return invoker.getInterface();
					}

					public URL getUrl()
					{
						return invoker.getUrl();
					}

					public boolean isAvailable()
					{
						return invoker.isAvailable();
					}

					public Result invoke(Invocation invocation)
						throws RpcException
					{
						return filter.invoke(next, invocation);
					}

					public void destroy()
					{
						invoker.destroy();
					}

					public String toString()
					{
						return invoker.toString();
					}

			
			{
				invoker = invoker1;
				filter = filter1;
				next = invoker2;
				super();
			}
				};
			}

		}
		return last;
	}
}
