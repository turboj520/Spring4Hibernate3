// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExecutionChannelHandler.java

package com.autohome.turbo.remoting.transport.dispatcher.execution;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable;
import com.autohome.turbo.remoting.transport.dispatcher.WrappedChannelHandler;
import java.util.concurrent.ExecutorService;

public class ExecutionChannelHandler extends WrappedChannelHandler
{

	public ExecutionChannelHandler(ChannelHandler handler, URL url)
	{
		super(handler, url);
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		executor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.CONNECTED));
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		executor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.DISCONNECTED));
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		executor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.RECEIVED, message));
	}

	public void caught(Channel channel, Throwable exception)
		throws RemotingException
	{
		executor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.CAUGHT, exception));
	}
}
