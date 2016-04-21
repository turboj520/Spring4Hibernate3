// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WrappedChannelHandler.java

package com.autohome.turbo.remoting.transport.dispatcher;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.store.DataStore;
import com.autohome.turbo.common.threadpool.ThreadPool;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.ChannelHandlerDelegate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WrappedChannelHandler
	implements ChannelHandlerDelegate
{

	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/dispatcher/WrappedChannelHandler);
	protected static final ExecutorService SHARED_EXECUTOR = Executors.newCachedThreadPool(new NamedThreadFactory("DubboSharedHandler", true));
	protected final ExecutorService executor;
	protected final ChannelHandler handler;
	protected final URL url;

	public WrappedChannelHandler(ChannelHandler handler, URL url)
	{
		this.handler = handler;
		this.url = url;
		executor = (ExecutorService)((ThreadPool)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/threadpool/ThreadPool).getAdaptiveExtension()).getExecutor(url);
		String componentKey = Constants.EXECUTOR_SERVICE_COMPONENT_KEY;
		if ("consumer".equalsIgnoreCase(url.getParameter("side")))
			componentKey = "consumer";
		DataStore dataStore = (DataStore)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/store/DataStore).getDefaultExtension();
		dataStore.put(componentKey, Integer.toString(url.getPort()), executor);
	}

	public void close()
	{
		try
		{
			if (executor instanceof ExecutorService)
				executor.shutdown();
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("fail to destroy thread pool of server: ").append(t.getMessage()).toString(), t);
		}
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		handler.connected(channel);
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		handler.disconnected(channel);
	}

	public void sent(Channel channel, Object message)
		throws RemotingException
	{
		handler.sent(channel, message);
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		handler.received(channel, message);
	}

	public void caught(Channel channel, Throwable exception)
		throws RemotingException
	{
		handler.caught(channel, exception);
	}

	public ExecutorService getExecutor()
	{
		return executor;
	}

	public ChannelHandler getHandler()
	{
		if (handler instanceof ChannelHandlerDelegate)
			return ((ChannelHandlerDelegate)handler).getHandler();
		else
			return handler;
	}

	public URL getUrl()
	{
		return url;
	}

}
