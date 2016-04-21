// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExecutionDispatcher.java

package com.autohome.turbo.remoting.transport.dispatcher.execution;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Dispatcher;

// Referenced classes of package com.autohome.turbo.remoting.transport.dispatcher.execution:
//			ExecutionChannelHandler

public class ExecutionDispatcher
	implements Dispatcher
{

	public static final String NAME = "execution";

	public ExecutionDispatcher()
	{
	}

	public ChannelHandler dispatch(ChannelHandler handler, URL url)
	{
		return new ExecutionChannelHandler(handler, url);
	}
}
