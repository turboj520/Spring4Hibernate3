// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Transporters.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.remoting.transport.ChannelHandlerAdapter;
import com.autohome.turbo.remoting.transport.ChannelHandlerDispatcher;

// Referenced classes of package com.autohome.turbo.remoting:
//			Transporter, RemotingException, ChannelHandler, Server, 
//			Client

public class Transporters
{

	public static transient Server bind(String url, ChannelHandler handler[])
		throws RemotingException
	{
		return bind(URL.valueOf(url), handler);
	}

	public static transient Server bind(URL url, ChannelHandler handlers[])
		throws RemotingException
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		if (handlers == null || handlers.length == 0)
			throw new IllegalArgumentException("handlers == null");
		ChannelHandler handler;
		if (handlers.length == 1)
			handler = handlers[0];
		else
			handler = new ChannelHandlerDispatcher(handlers);
		return getTransporter().bind(url, handler);
	}

	public static transient Client connect(String url, ChannelHandler handler[])
		throws RemotingException
	{
		return connect(URL.valueOf(url), handler);
	}

	public static transient Client connect(URL url, ChannelHandler handlers[])
		throws RemotingException
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		ChannelHandler handler;
		if (handlers == null || handlers.length == 0)
			handler = new ChannelHandlerAdapter();
		else
		if (handlers.length == 1)
			handler = handlers[0];
		else
			handler = new ChannelHandlerDispatcher(handlers);
		return getTransporter().connect(url, handler);
	}

	public static Transporter getTransporter()
	{
		return (Transporter)ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Transporter).getAdaptiveExtension();
	}

	private Transporters()
	{
	}

	static 
	{
		Version.checkDuplicate(com/autohome/turbo/remoting/Transporters);
		Version.checkDuplicate(com/autohome/turbo/remoting/RemotingException);
	}
}
