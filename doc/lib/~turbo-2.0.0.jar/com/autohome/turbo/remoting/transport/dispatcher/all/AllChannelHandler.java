// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AllChannelHandler.java

package com.autohome.turbo.remoting.transport.dispatcher.all;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable;
import com.autohome.turbo.remoting.transport.dispatcher.WrappedChannelHandler;
import java.util.concurrent.ExecutorService;

public class AllChannelHandler extends WrappedChannelHandler
{

	public AllChannelHandler(ChannelHandler handler, URL url)
	{
		super(handler, url);
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		ExecutorService cexecutor = getExecutorService();
		try
		{
			cexecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.CONNECTED));
		}
		catch (Throwable t)
		{
			throw new ExecutionException("connect event", channel, (new StringBuilder()).append(getClass()).append(" error when process connected event .").toString(), t);
		}
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		ExecutorService cexecutor = getExecutorService();
		try
		{
			cexecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.DISCONNECTED));
		}
		catch (Throwable t)
		{
			throw new ExecutionException("disconnect event", channel, (new StringBuilder()).append(getClass()).append(" error when process disconnected event .").toString(), t);
		}
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		ExecutorService cexecutor = getExecutorService();
		try
		{
			cexecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.RECEIVED, message));
		}
		catch (Throwable t)
		{
			throw new ExecutionException(message, channel, (new StringBuilder()).append(getClass()).append(" error when process received event .").toString(), t);
		}
	}

	public void caught(Channel channel, Throwable exception)
		throws RemotingException
	{
		ExecutorService cexecutor = getExecutorService();
		try
		{
			cexecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.CAUGHT, exception));
		}
		catch (Throwable t)
		{
			throw new ExecutionException("caught event", channel, (new StringBuilder()).append(getClass()).append(" error when process caught event .").toString(), t);
		}
	}

	private ExecutorService getExecutorService()
	{
		ExecutorService cexecutor = executor;
		if (cexecutor == null || cexecutor.isShutdown())
			cexecutor = SHARED_EXECUTOR;
		return cexecutor;
	}
}
