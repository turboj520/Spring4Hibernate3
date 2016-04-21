// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelHandlers.java

package com.autohome.turbo.remoting.transport.dispatcher;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Dispatcher;
import com.autohome.turbo.remoting.exchange.support.header.HeartbeatHandler;
import com.autohome.turbo.remoting.transport.MultiMessageHandler;

public class ChannelHandlers
{

	private static ChannelHandlers INSTANCE = new ChannelHandlers();

	public static ChannelHandler wrap(ChannelHandler handler, URL url)
	{
		return getInstance().wrapInternal(handler, url);
	}

	protected ChannelHandlers()
	{
	}

	protected ChannelHandler wrapInternal(ChannelHandler handler, URL url)
	{
		return new MultiMessageHandler(new HeartbeatHandler(((Dispatcher)ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Dispatcher).getAdaptiveExtension()).dispatch(handler, url)));
	}

	protected static ChannelHandlers getInstance()
	{
		return INSTANCE;
	}

	static void setTestingChannelHandlers(ChannelHandlers instance)
	{
		INSTANCE = instance;
	}

}
