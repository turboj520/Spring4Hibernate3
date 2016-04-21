// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MessageOnlyDispatcher.java

package com.autohome.turbo.remoting.transport.dispatcher.message;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Dispatcher;

// Referenced classes of package com.autohome.turbo.remoting.transport.dispatcher.message:
//			MessageOnlyChannelHandler

public class MessageOnlyDispatcher
	implements Dispatcher
{

	public static final String NAME = "message";

	public MessageOnlyDispatcher()
	{
	}

	public ChannelHandler dispatch(ChannelHandler handler, URL url)
	{
		return new MessageOnlyChannelHandler(handler, url);
	}
}
