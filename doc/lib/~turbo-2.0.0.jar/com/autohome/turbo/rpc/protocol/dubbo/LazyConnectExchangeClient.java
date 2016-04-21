// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LazyConnectExchangeClient.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.*;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class LazyConnectExchangeClient
	implements ExchangeClient
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/LazyConnectExchangeClient);
	private final URL url;
	private final ExchangeHandler requestHandler;
	private volatile ExchangeClient client;
	private final Lock connectLock = new ReentrantLock();
	private final boolean initialState;
	protected final boolean requestWithWarning;
	static final String REQUEST_WITH_WARNING_KEY = "lazyclient_request_with_warning";
	private AtomicLong warningcount;

	public LazyConnectExchangeClient(URL url, ExchangeHandler requestHandler)
	{
		warningcount = new AtomicLong(0L);
		this.url = url.addParameter("send.reconnect", Boolean.TRUE.toString());
		this.requestHandler = requestHandler;
		initialState = url.getParameter("connect.lazy.initial.state", true);
		requestWithWarning = url.getParameter("lazyclient_request_with_warning", false);
	}

	private void initClient()
		throws RemotingException
	{
		if (client != null)
			return;
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Lazy connect to ").append(url).toString());
		connectLock.lock();
		if (client != null)
		{
			connectLock.unlock();
			return;
		}
		client = Exchangers.connect(url, requestHandler);
		connectLock.unlock();
		break MISSING_BLOCK_LABEL_114;
		Exception exception;
		exception;
		connectLock.unlock();
		throw exception;
	}

	public ResponseFuture request(Object request)
		throws RemotingException
	{
		warning(request);
		initClient();
		return client.request(request);
	}

	public URL getUrl()
	{
		return url;
	}

	public InetSocketAddress getRemoteAddress()
	{
		if (client == null)
			return InetSocketAddress.createUnresolved(url.getHost(), url.getPort());
		else
			return client.getRemoteAddress();
	}

	public ResponseFuture request(Object request, int timeout)
		throws RemotingException
	{
		warning(request);
		initClient();
		return client.request(request, timeout);
	}

	private void warning(Object request)
	{
		if (requestWithWarning)
		{
			if (warningcount.get() % 5000L == 0L)
				logger.warn(new IllegalStateException("safe guard client , should not be called ,must have a bug."));
			warningcount.incrementAndGet();
		}
	}

	public ChannelHandler getChannelHandler()
	{
		checkClient();
		return client.getChannelHandler();
	}

	public boolean isConnected()
	{
		if (client == null)
			return initialState;
		else
			return client.isConnected();
	}

	public InetSocketAddress getLocalAddress()
	{
		if (client == null)
			return InetSocketAddress.createUnresolved(NetUtils.getLocalHost(), 0);
		else
			return client.getLocalAddress();
	}

	public ExchangeHandler getExchangeHandler()
	{
		return requestHandler;
	}

	public void send(Object message)
		throws RemotingException
	{
		initClient();
		client.send(message);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		initClient();
		client.send(message, sent);
	}

	public boolean isClosed()
	{
		if (client != null)
			return client.isClosed();
		else
			return true;
	}

	public void close()
	{
		if (client != null)
			client.close();
	}

	public void close(int timeout)
	{
		if (client != null)
			client.close(timeout);
	}

	public void reset(URL url)
	{
		checkClient();
		client.reset(url);
	}

	/**
	 * @deprecated Method reset is deprecated
	 */

	public void reset(Parameters parameters)
	{
		reset(getUrl().addParameters(parameters.getParameters()));
	}

	public void reconnect()
		throws RemotingException
	{
		checkClient();
		client.reconnect();
	}

	public Object getAttribute(String key)
	{
		if (client == null)
			return null;
		else
			return client.getAttribute(key);
	}

	public void setAttribute(String key, Object value)
	{
		checkClient();
		client.setAttribute(key, value);
	}

	public void removeAttribute(String key)
	{
		checkClient();
		client.removeAttribute(key);
	}

	public boolean hasAttribute(String key)
	{
		if (client == null)
			return false;
		else
			return client.hasAttribute(key);
	}

	private void checkClient()
	{
		if (client == null)
			throw new IllegalStateException((new StringBuilder()).append("LazyConnectExchangeClient state error. the client has not be init .url:").append(url).toString());
		else
			return;
	}

}
