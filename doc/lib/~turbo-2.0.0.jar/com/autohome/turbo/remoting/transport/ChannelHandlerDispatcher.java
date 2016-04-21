// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelHandlerDispatcher.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.ChannelHandler;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChannelHandlerDispatcher
	implements ChannelHandler
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/ChannelHandlerDispatcher);
	private final Collection channelHandlers;

	public ChannelHandlerDispatcher()
	{
		channelHandlers = new CopyOnWriteArraySet();
	}

	public transient ChannelHandlerDispatcher(ChannelHandler handlers[])
	{
		this(((Collection) (handlers != null ? ((Collection) (Arrays.asList(handlers))) : null)));
	}

	public ChannelHandlerDispatcher(Collection handlers)
	{
		channelHandlers = new CopyOnWriteArraySet();
		if (handlers != null && handlers.size() > 0)
			channelHandlers.addAll(handlers);
	}

	public Collection getChannelHandlers()
	{
		return channelHandlers;
	}

	public ChannelHandlerDispatcher addChannelHandler(ChannelHandler handler)
	{
		channelHandlers.add(handler);
		return this;
	}

	public ChannelHandlerDispatcher removeChannelHandler(ChannelHandler handler)
	{
		channelHandlers.remove(handler);
		return this;
	}

	public void connected(Channel channel)
	{
		for (Iterator i$ = channelHandlers.iterator(); i$.hasNext();)
		{
			ChannelHandler listener = (ChannelHandler)i$.next();
			try
			{
				listener.connected(channel);
			}
			catch (Throwable t)
			{
				logger.error(t.getMessage(), t);
			}
		}

	}

	public void disconnected(Channel channel)
	{
		for (Iterator i$ = channelHandlers.iterator(); i$.hasNext();)
		{
			ChannelHandler listener = (ChannelHandler)i$.next();
			try
			{
				listener.disconnected(channel);
			}
			catch (Throwable t)
			{
				logger.error(t.getMessage(), t);
			}
		}

	}

	public void sent(Channel channel, Object message)
	{
		for (Iterator i$ = channelHandlers.iterator(); i$.hasNext();)
		{
			ChannelHandler listener = (ChannelHandler)i$.next();
			try
			{
				listener.sent(channel, message);
			}
			catch (Throwable t)
			{
				logger.error(t.getMessage(), t);
			}
		}

	}

	public void received(Channel channel, Object message)
	{
		for (Iterator i$ = channelHandlers.iterator(); i$.hasNext();)
		{
			ChannelHandler listener = (ChannelHandler)i$.next();
			try
			{
				listener.received(channel, message);
			}
			catch (Throwable t)
			{
				logger.error(t.getMessage(), t);
			}
		}

	}

	public void caught(Channel channel, Throwable exception)
	{
		for (Iterator i$ = channelHandlers.iterator(); i$.hasNext();)
		{
			ChannelHandler listener = (ChannelHandler)i$.next();
			try
			{
				listener.caught(channel, exception);
			}
			catch (Throwable t)
			{
				logger.error(t.getMessage(), t);
			}
		}

	}

}
