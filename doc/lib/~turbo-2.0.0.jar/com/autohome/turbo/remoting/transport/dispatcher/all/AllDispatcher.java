// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AllDispatcher.java

package com.autohome.turbo.remoting.transport.dispatcher.all;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Dispatcher;

// Referenced classes of package com.autohome.turbo.remoting.transport.dispatcher.all:
//			AllChannelHandler

public class AllDispatcher
	implements Dispatcher
{

	public static final String NAME = "all";

	public AllDispatcher()
	{
	}

	public ChannelHandler dispatch(ChannelHandler handler, URL url)
	{
		return new AllChannelHandler(handler, url);
	}
}
