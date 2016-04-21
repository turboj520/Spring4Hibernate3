// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeHandlerDispatcher.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;
import com.autohome.turbo.remoting.exchange.ExchangeHandler;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.remoting.telnet.support.TelnetHandlerAdapter;
import com.autohome.turbo.remoting.transport.ChannelHandlerDispatcher;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support:
//			ReplierDispatcher, Replier

public class ExchangeHandlerDispatcher
	implements ExchangeHandler
{

	private final ReplierDispatcher replierDispatcher;
	private final ChannelHandlerDispatcher handlerDispatcher;
	private final TelnetHandler telnetHandler;

	public ExchangeHandlerDispatcher()
	{
		replierDispatcher = new ReplierDispatcher();
		handlerDispatcher = new ChannelHandlerDispatcher();
		telnetHandler = new TelnetHandlerAdapter();
	}

	public ExchangeHandlerDispatcher(Replier replier)
	{
		replierDispatcher = new ReplierDispatcher(replier);
		handlerDispatcher = new ChannelHandlerDispatcher();
		telnetHandler = new TelnetHandlerAdapter();
	}

	public transient ExchangeHandlerDispatcher(ChannelHandler handlers[])
	{
		replierDispatcher = new ReplierDispatcher();
		handlerDispatcher = new ChannelHandlerDispatcher(handlers);
		telnetHandler = new TelnetHandlerAdapter();
	}

	public transient ExchangeHandlerDispatcher(Replier replier, ChannelHandler handlers[])
	{
		replierDispatcher = new ReplierDispatcher(replier);
		handlerDispatcher = new ChannelHandlerDispatcher(handlers);
		telnetHandler = new TelnetHandlerAdapter();
	}

	public ExchangeHandlerDispatcher addChannelHandler(ChannelHandler handler)
	{
		handlerDispatcher.addChannelHandler(handler);
		return this;
	}

	public ExchangeHandlerDispatcher removeChannelHandler(ChannelHandler handler)
	{
		handlerDispatcher.removeChannelHandler(handler);
		return this;
	}

	public ExchangeHandlerDispatcher addReplier(Class type, Replier replier)
	{
		replierDispatcher.addReplier(type, replier);
		return this;
	}

	public ExchangeHandlerDispatcher removeReplier(Class type)
	{
		replierDispatcher.removeReplier(type);
		return this;
	}

	public Object reply(ExchangeChannel channel, Object request)
		throws RemotingException
	{
		return replierDispatcher.reply(channel, request);
	}

	public void connected(Channel channel)
	{
		handlerDispatcher.connected(channel);
	}

	public void disconnected(Channel channel)
	{
		handlerDispatcher.disconnected(channel);
	}

	public void sent(Channel channel, Object message)
	{
		handlerDispatcher.sent(channel, message);
	}

	public void received(Channel channel, Object message)
	{
		handlerDispatcher.received(channel, message);
	}

	public void caught(Channel channel, Throwable exception)
	{
		handlerDispatcher.caught(channel, exception);
	}

	public String telnet(Channel channel, String message)
		throws RemotingException
	{
		return telnetHandler.telnet(channel, message);
	}
}
