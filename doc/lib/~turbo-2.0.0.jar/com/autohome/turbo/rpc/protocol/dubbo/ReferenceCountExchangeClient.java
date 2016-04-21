// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReferenceCountExchangeClient.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			LazyConnectExchangeClient

final class ReferenceCountExchangeClient
	implements ExchangeClient
{

	private ExchangeClient client;
	private final URL url;
	private final AtomicInteger refenceCount = new AtomicInteger(0);
	private final ConcurrentMap ghostClientMap;

	public ReferenceCountExchangeClient(ExchangeClient client, ConcurrentMap ghostClientMap)
	{
		this.client = client;
		refenceCount.incrementAndGet();
		url = client.getUrl();
		if (ghostClientMap == null)
		{
			throw new IllegalStateException((new StringBuilder()).append("ghostClientMap can not be null, url: ").append(url).toString());
		} else
		{
			this.ghostClientMap = ghostClientMap;
			return;
		}
	}

	public void reset(URL url)
	{
		client.reset(url);
	}

	public ResponseFuture request(Object request)
		throws RemotingException
	{
		return client.request(request);
	}

	public URL getUrl()
	{
		return client.getUrl();
	}

	public InetSocketAddress getRemoteAddress()
	{
		return client.getRemoteAddress();
	}

	public ChannelHandler getChannelHandler()
	{
		return client.getChannelHandler();
	}

	public ResponseFuture request(Object request, int timeout)
		throws RemotingException
	{
		return client.request(request, timeout);
	}

	public boolean isConnected()
	{
		return client.isConnected();
	}

	public void reconnect()
		throws RemotingException
	{
		client.reconnect();
	}

	public InetSocketAddress getLocalAddress()
	{
		return client.getLocalAddress();
	}

	public boolean hasAttribute(String key)
	{
		return client.hasAttribute(key);
	}

	public void reset(Parameters parameters)
	{
		client.reset(parameters);
	}

	public void send(Object message)
		throws RemotingException
	{
		client.send(message);
	}

	public ExchangeHandler getExchangeHandler()
	{
		return client.getExchangeHandler();
	}

	public Object getAttribute(String key)
	{
		return client.getAttribute(key);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		client.send(message, sent);
	}

	public void setAttribute(String key, Object value)
	{
		client.setAttribute(key, value);
	}

	public void removeAttribute(String key)
	{
		client.removeAttribute(key);
	}

	public void close()
	{
		close(0);
	}

	public void close(int timeout)
	{
		if (refenceCount.decrementAndGet() <= 0)
		{
			if (timeout == 0)
				client.close();
			else
				client.close(timeout);
			client = replaceWithLazyClient();
		}
	}

	private LazyConnectExchangeClient replaceWithLazyClient()
	{
		URL lazyUrl = url.addParameter("connect.lazy.initial.state", Boolean.FALSE.booleanValue()).addParameter("reconnect", Boolean.FALSE.booleanValue()).addParameter("send.reconnect", Boolean.TRUE.toString()).addParameter("warning", Boolean.TRUE.toString()).addParameter("lazyclient_request_with_warning", true).addParameter("_client_memo", "referencecounthandler.replacewithlazyclient");
		String key = url.getAddress();
		LazyConnectExchangeClient gclient = (LazyConnectExchangeClient)ghostClientMap.get(key);
		if (gclient == null || gclient.isClosed())
		{
			gclient = new LazyConnectExchangeClient(lazyUrl, client.getExchangeHandler());
			ghostClientMap.put(key, gclient);
		}
		return gclient;
	}

	public boolean isClosed()
	{
		return client.isClosed();
	}

	public void incrementAndGetCount()
	{
		refenceCount.incrementAndGet();
	}
}
