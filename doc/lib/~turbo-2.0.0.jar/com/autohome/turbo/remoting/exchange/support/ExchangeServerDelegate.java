// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeServerDelegate.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;
import com.autohome.turbo.remoting.exchange.ExchangeServer;
import java.net.InetSocketAddress;
import java.util.Collection;

public class ExchangeServerDelegate
	implements ExchangeServer
{

	private transient ExchangeServer server;

	public ExchangeServerDelegate()
	{
	}

	public ExchangeServerDelegate(ExchangeServer server)
	{
		setServer(server);
	}

	public ExchangeServer getServer()
	{
		return server;
	}

	public void setServer(ExchangeServer server)
	{
		this.server = server;
	}

	public boolean isBound()
	{
		return server.isBound();
	}

	public void reset(URL url)
	{
		server.reset(url);
	}

	/**
	 * @deprecated Method reset is deprecated
	 */

	public void reset(Parameters parameters)
	{
		reset(getUrl().addParameters(parameters.getParameters()));
	}

	public Collection getChannels()
	{
		return server.getChannels();
	}

	public Channel getChannel(InetSocketAddress remoteAddress)
	{
		return server.getChannel(remoteAddress);
	}

	public URL getUrl()
	{
		return server.getUrl();
	}

	public ChannelHandler getChannelHandler()
	{
		return server.getChannelHandler();
	}

	public InetSocketAddress getLocalAddress()
	{
		return server.getLocalAddress();
	}

	public void send(Object message)
		throws RemotingException
	{
		server.send(message);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		server.send(message, sent);
	}

	public void close()
	{
		server.close();
	}

	public boolean isClosed()
	{
		return server.isClosed();
	}

	public Collection getExchangeChannels()
	{
		return server.getExchangeChannels();
	}

	public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress)
	{
		return server.getExchangeChannel(remoteAddress);
	}

	public void close(int timeout)
	{
		server.close(timeout);
	}
}
