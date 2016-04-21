// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ProtocolListenerWrapper.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.listener.ListenerExporterWrapper;
import com.autohome.turbo.rpc.listener.ListenerInvokerWrapper;
import java.util.Collections;

public class ProtocolListenerWrapper
	implements Protocol
{

	private final Protocol protocol;

	public ProtocolListenerWrapper(Protocol protocol)
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
			return new ListenerExporterWrapper(protocol.export(invoker), Collections.unmodifiableList(ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/ExporterListener).getActivateExtension(invoker.getUrl(), "exporter.listener")));
	}

	public Invoker refer(Class type, URL url)
		throws RpcException
	{
		if ("registry".equals(url.getProtocol()))
			return protocol.refer(type, url);
		else
			return new ListenerInvokerWrapper(protocol.refer(type, url), Collections.unmodifiableList(ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/InvokerListener).getActivateExtension(url, "invoker.listener")));
	}

	public void destroy()
	{
		protocol.destroy();
	}
}
