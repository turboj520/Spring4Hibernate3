// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeaderExchangeChannel.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import com.autohome.turbo.remoting.exchange.support.DefaultFuture;
import java.net.InetSocketAddress;

final class HeaderExchangeChannel
	implements ExchangeChannel
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/support/header/HeaderExchangeChannel);
	private static final String CHANNEL_KEY = (new StringBuilder()).append(com/autohome/turbo/remoting/exchange/support/header/HeaderExchangeChannel.getName()).append(".CHANNEL").toString();
	private final Channel channel;
	private volatile boolean closed;

	HeaderExchangeChannel(Channel channel)
	{
		closed = false;
		if (channel == null)
		{
			throw new IllegalArgumentException("channel == null");
		} else
		{
			this.channel = channel;
			return;
		}
	}

	static HeaderExchangeChannel getOrAddChannel(Channel ch)
	{
		if (ch == null)
			return null;
		HeaderExchangeChannel ret = (HeaderExchangeChannel)ch.getAttribute(CHANNEL_KEY);
		if (ret == null)
		{
			ret = new HeaderExchangeChannel(ch);
			if (ch.isConnected())
				ch.setAttribute(CHANNEL_KEY, ret);
		}
		return ret;
	}

	static void removeChannelIfDisconnected(Channel ch)
	{
		if (ch != null && !ch.isConnected())
			ch.removeAttribute(CHANNEL_KEY);
	}

	public void send(Object message)
		throws RemotingException
	{
		send(message, getUrl().getParameter("sent", false));
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		if (closed)
			throw new RemotingException(getLocalAddress(), null, (new StringBuilder()).append("Failed to send message ").append(message).append(", cause: The channel ").append(this).append(" is closed!").toString());
		if ((message instanceof Request) || (message instanceof Response) || (message instanceof String))
		{
			channel.send(message, sent);
		} else
		{
			Request request = new Request();
			request.setVersion("2.0.0");
			request.setTwoWay(false);
			request.setData(message);
			channel.send(request, sent);
		}
	}

	public ResponseFuture request(Object request)
		throws RemotingException
	{
		return request(request, channel.getUrl().getPositiveParameter("timeout", 1000));
	}

	public ResponseFuture request(Object request, int timeout)
		throws RemotingException
	{
		if (closed)
			throw new RemotingException(getLocalAddress(), null, (new StringBuilder()).append("Failed to send request ").append(request).append(", cause: The channel ").append(this).append(" is closed!").toString());
		Request req = new Request();
		req.setVersion("2.0.0");
		req.setTwoWay(true);
		req.setData(request);
		DefaultFuture future = new DefaultFuture(channel, req, timeout);
		try
		{
			channel.send(req);
		}
		catch (RemotingException e)
		{
			future.cancel();
			throw e;
		}
		return future;
	}

	public boolean isClosed()
	{
		return closed;
	}

	public void close()
	{
		try
		{
			channel.close();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

	public void close(int timeout)
	{
		if (closed)
			return;
		closed = true;
		if (timeout > 0)
		{
			for (long start = System.currentTimeMillis(); DefaultFuture.hasFuture(this) && System.currentTimeMillis() - start < (long)timeout;)
				try
				{
					Thread.sleep(10L);
				}
				catch (InterruptedException e)
				{
					logger.warn(e.getMessage(), e);
				}

		}
		close();
	}

	public InetSocketAddress getLocalAddress()
	{
		return channel.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress()
	{
		return channel.getRemoteAddress();
	}

	public URL getUrl()
	{
		return channel.getUrl();
	}

	public boolean isConnected()
	{
		return channel.isConnected();
	}

	public ChannelHandler getChannelHandler()
	{
		return channel.getChannelHandler();
	}

	public ExchangeHandler getExchangeHandler()
	{
		return (ExchangeHandler)channel.getChannelHandler();
	}

	public Object getAttribute(String key)
	{
		return channel.getAttribute(key);
	}

	public void setAttribute(String key, Object value)
	{
		channel.setAttribute(key, value);
	}

	public void removeAttribute(String key)
	{
		channel.removeAttribute(key);
	}

	public boolean hasAttribute(String key)
	{
		return channel.hasAttribute(key);
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
		HeaderExchangeChannel other = (HeaderExchangeChannel)obj;
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
		return channel.toString();
	}

}
