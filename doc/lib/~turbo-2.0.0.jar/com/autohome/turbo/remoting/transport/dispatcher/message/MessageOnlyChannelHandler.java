// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MessageOnlyChannelHandler.java

package com.autohome.turbo.remoting.transport.dispatcher.message;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable;
import com.autohome.turbo.remoting.transport.dispatcher.WrappedChannelHandler;
import java.util.concurrent.ExecutorService;

public class MessageOnlyChannelHandler extends WrappedChannelHandler
{

	public MessageOnlyChannelHandler(ChannelHandler handler, URL url)
	{
		super(handler, url);
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		ExecutorService cexecutor = executor;
		if (cexecutor == null || cexecutor.isShutdown())
			cexecutor = SHARED_EXECUTOR;
		try
		{
			cexecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.RECEIVED, message));
		}
		catch (Throwable t)
		{
			throw new ExecutionException(message, channel, (new StringBuilder()).append(getClass()).append(" error when process received event .").toString(), t);
		}
	}
}
