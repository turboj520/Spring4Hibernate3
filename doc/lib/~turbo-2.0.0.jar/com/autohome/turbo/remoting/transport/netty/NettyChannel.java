// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyChannel.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.transport.AbstractChannel;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

final class NettyChannel extends AbstractChannel
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/netty/NettyChannel);
	private static final ConcurrentMap channelMap = new ConcurrentHashMap();
	private final Channel channel;
	private final Map attributes = new ConcurrentHashMap();

	private NettyChannel(Channel channel, URL url, ChannelHandler handler)
	{
		super(url, handler);
		if (channel == null)
		{
			throw new IllegalArgumentException("netty channel == null;");
		} else
		{
			this.channel = channel;
			return;
		}
	}

	static NettyChannel getOrAddChannel(Channel ch, URL url, ChannelHandler handler)
	{
		if (ch == null)
			return null;
		NettyChannel ret = (NettyChannel)channelMap.get(ch);
		if (ret == null)
		{
			NettyChannel nc = new NettyChannel(ch, url, handler);
			if (ch.isConnected())
				ret = (NettyChannel)channelMap.putIfAbsent(ch, nc);
			if (ret == null)
				ret = nc;
		}
		return ret;
	}

	static void removeChannelIfDisconnected(Channel ch)
	{
		if (ch != null && !ch.isConnected())
			channelMap.remove(ch);
	}

	public InetSocketAddress getLocalAddress()
	{
		return (InetSocketAddress)channel.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress()
	{
		return (InetSocketAddress)channel.getRemoteAddress();
	}

	public boolean isConnected()
	{
		return channel.isConnected();
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		super.send(message, sent);
		boolean success = true;
		int timeout = 0;
		try
		{
			ChannelFuture future = channel.write(message);
			if (sent)
			{
				timeout = getUrl().getPositiveParameter("timeout", 1000);
				success = future.await(timeout);
			}
			Throwable cause = future.getCause();
			if (cause != null)
				throw cause;
		}
		catch (Throwable e)
		{
			throw new RemotingException(this, (new StringBuilder()).append("Failed to send message ").append(message).append(" to ").append(getRemoteAddress()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
		if (!success)
			throw new RemotingException(this, (new StringBuilder()).append("Failed to send message ").append(message).append(" to ").append(getRemoteAddress()).append("in timeout(").append(timeout).append("ms) limit").toString());
		else
			return;
	}

	public void close()
	{
		try
		{
			super.close();
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			removeChannelIfDisconnected(channel);
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			attributes.clear();
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Close netty channel ").append(channel).toString());
			channel.close();
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

	public boolean hasAttribute(String key)
	{
		return attributes.containsKey(key);
	}

	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public void setAttribute(String key, Object value)
	{
		if (value == null)
			attributes.remove(key);
		else
			attributes.put(key, value);
	}

	public void removeAttribute(String key)
	{
		attributes.remove(key);
	}

	public int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = 31 * result + (channel != null ? channel.hashCode() : 0);
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NettyChannel other = (NettyChannel)obj;
		if (channel == null)
		{
			if (other.channel != null)
				return false;
		} else
		if (!channel.equals(other.channel))
			return false;
		return true;
	}

	public String toString()
	{
		return (new StringBuilder()).append("NettyChannel [channel=").append(channel).append("]").toString();
	}

}
