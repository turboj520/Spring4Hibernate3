// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelWrappedInvoker.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.ExchangeClient;
import com.autohome.turbo.remoting.exchange.ResponseFuture;
import com.autohome.turbo.remoting.exchange.support.header.HeaderExchangeClient;
import com.autohome.turbo.remoting.transport.ClientDelegate;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.AbstractInvoker;
import java.net.InetSocketAddress;

class ChannelWrappedInvoker extends AbstractInvoker
{
	public static class ChannelWrapper extends ClientDelegate
	{

		private final Channel channel;
		private final URL url;

		public URL getUrl()
		{
			return url;
		}

		public ChannelHandler getChannelHandler()
		{
			return channel.getChannelHandler();
		}

		public InetSocketAddress getLocalAddress()
		{
			return channel.getLocalAddress();
		}

		public void close()
		{
			channel.close();
		}

		public boolean isClosed()
		{
			return channel != null ? channel.isClosed() : true;
		}

		public void reset(URL url)
		{
			throw new RpcException("ChannelInvoker can not reset.");
		}

		public InetSocketAddress getRemoteAddress()
		{
			return channel.getLocalAddress();
		}

		public boolean isConnected()
		{
			return channel != null ? channel.isConnected() : false;
		}

		public boolean hasAttribute(String key)
		{
			return channel.hasAttribute(key);
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

		public void reconnect()
			throws RemotingException
		{
		}

		public void send(Object message)
			throws RemotingException
		{
			channel.send(message);
		}

		public void send(Object message, boolean sent)
			throws RemotingException
		{
			channel.send(message, sent);
		}

		public ChannelWrapper(Channel channel)
		{
			this.channel = channel;
			url = channel.getUrl().addParameter("codec", "dubbo");
		}
	}


	private final Channel channel;
	private final String serviceKey;

	public ChannelWrappedInvoker(Class serviceType, Channel channel, URL url, String serviceKey)
	{
		super(serviceType, url, new String[] {
			"group", "token", "timeout"
		});
		this.channel = channel;
		this.serviceKey = serviceKey;
	}

	protected Result doInvoke(Invocation invocation)
		throws Throwable
	{
		RpcInvocation inv;
		ExchangeClient currentClient;
		inv = (RpcInvocation)invocation;
		inv.setAttachment("path", getInterface().getName());
		inv.setAttachment("callback.service.instid", serviceKey);
		currentClient = new HeaderExchangeClient(new ChannelWrapper(channel));
		if (!getUrl().getMethodParameter(invocation.getMethodName(), "async", false))
			break MISSING_BLOCK_LABEL_97;
		currentClient.send(inv, getUrl().getMethodParameter(invocation.getMethodName(), "sent", false));
		return new RpcResult();
		int timeout = getUrl().getMethodParameter(invocation.getMethodName(), "timeout", 1000);
		if (timeout > 0)
			return (Result)currentClient.request(inv, timeout).get();
		return (Result)currentClient.request(inv).get();
		RpcException e;
		e;
		throw e;
		e;
		throw new RpcException(2, e.getMessage(), e);
		e;
		throw new RpcException(1, e.getMessage(), e);
		e;
		throw new RpcException(e.getMessage(), e);
	}

	public void destroy()
	{
	}
}
