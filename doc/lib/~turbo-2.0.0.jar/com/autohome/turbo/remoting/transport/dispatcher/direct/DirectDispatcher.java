// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DirectDispatcher.java

package com.autohome.turbo.remoting.transport.dispatcher.direct;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Dispatcher;

public class DirectDispatcher
	implements Dispatcher
{

	public static final String NAME = "direct";

	public DirectDispatcher()
	{
	}

	public ChannelHandler dispatch(ChannelHandler handler, URL url)
	{
		return handler;
	}
}
