// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelDelegate.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import java.net.InetSocketAddress;

public class ChannelDelegate
	implements Channel
{

	private transient Channel channel;

	public ChannelDelegate()
	{
	}

	public ChannelDelegate(Channel channel)
	{
		setChannel(channel);
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		if (channel == null)
		{
			throw new IllegalArgumentException("channel == null");
		} else
		{
			this.channel = channel;
			return;
		}
	}

	public URL getUrl()
	{
		return channel.getUrl();
	}

	public InetSocketAddress getRemoteAddress()
	{
		return channel.getRemoteAddress();
	}

	public ChannelHandler getChannelHandler()
	{
		return channel.getChannelHandler();
	}

	public boolean isConnected()
	{
		return channel.isConnected();
	}

	public InetSocketAddress getLocalAddress()
	{
		return channel.getLocalAddress();
	}

	public boolean hasAttribute(String key)
	{
		return channel.hasAttribute(key);
	}

	public void send(Object message)
		throws RemotingException
	{
		channel.send(message);
	}

	public Object getAttribute(String key)
	{
		return channel.getAttribute(key);
	}

	public void setAttribute(String key, Object value)
	{
		channel.setAttribute(key, value);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		channel.send(message, sent);
	}

	public void removeAttribute(String key)
	{
		channel.removeAttribute(key);
	}

	public void close()
	{
		channel.close();
	}

	public void close(int timeout)
	{
		channel.close(timeout);
	}

	public boolean isClosed()
	{
		return channel.isClosed();
	}
}
