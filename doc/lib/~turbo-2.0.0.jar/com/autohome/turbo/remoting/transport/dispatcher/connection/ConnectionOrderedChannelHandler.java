// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConnectionOrderedChannelHandler.java

package com.autohome.turbo.remoting.transport.dispatcher.connection;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.threadpool.support.AbortPolicyWithReport;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable;
import com.autohome.turbo.remoting.transport.dispatcher.WrappedChannelHandler;
import java.util.concurrent.*;

public class ConnectionOrderedChannelHandler extends WrappedChannelHandler
{

	protected final ThreadPoolExecutor connectionExecutor;
	private final int queuewarninglimit;

	public ConnectionOrderedChannelHandler(ChannelHandler handler, URL url)
	{
		super(handler, url);
		String threadName = url.getParameter("threadname", "Dubbo");
		connectionExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(url.getPositiveParameter("connect.queue.capacity", 0x7fffffff)), new NamedThreadFactory(threadName, true), new AbortPolicyWithReport(threadName, url));
		queuewarninglimit = url.getParameter("connect.queue.warning.size", 1000);
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		try
		{
			checkQueueLength();
			connectionExecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.CONNECTED));
		}
		catch (Throwable t)
		{
			throw new ExecutionException("connect event", channel, (new StringBuilder()).append(getClass()).append(" error when process connected event .").toString(), t);
		}
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		try
		{
			checkQueueLength();
			connectionExecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.DISCONNECTED));
		}
		catch (Throwable t)
		{
			throw new ExecutionException("disconnected event", channel, (new StringBuilder()).append(getClass()).append(" error when process disconnected event .").toString(), t);
		}
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

	public void caught(Channel channel, Throwable exception)
		throws RemotingException
	{
		ExecutorService cexecutor = executor;
		if (cexecutor == null || cexecutor.isShutdown())
			cexecutor = SHARED_EXECUTOR;
		try
		{
			cexecutor.execute(new ChannelEventRunnable(channel, handler, com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState.CAUGHT, exception));
		}
		catch (Throwable t)
		{
			throw new ExecutionException("caught event", channel, (new StringBuilder()).append(getClass()).append(" error when process caught event .").toString(), t);
		}
	}

	private void checkQueueLength()
	{
		if (connectionExecutor.getQueue().size() > queuewarninglimit)
			logger.warn(new IllegalThreadStateException((new StringBuilder()).append("connectionordered channel handler `queue size: ").append(connectionExecutor.getQueue().size()).append(" exceed the warning limit number :").append(queuewarninglimit).toString()));
	}
}
