// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeaderExchangeClient.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support.header:
//			HeaderExchangeChannel, HeartBeatTask

public class HeaderExchangeClient
	implements ExchangeClient
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/support/header/HeaderExchangeClient);
	private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("dubbo-remoting-client-heartbeat", true));
	private ScheduledFuture heatbeatTimer;
	private int heartbeat;
	private int heartbeatTimeout;
	private final Client client;
	private final ExchangeChannel channel;

	public HeaderExchangeClient(Client client)
	{
		if (client == null)
			throw new IllegalArgumentException("client == null");
		this.client = client;
		channel = new HeaderExchangeChannel(client);
		String dubbo = client.getUrl().getParameter("dubbo");
		heartbeat = client.getUrl().getParameter("heartbeat", dubbo == null || !dubbo.startsWith("1.0.") ? 0 : 60000);
		heartbeatTimeout = client.getUrl().getParameter("heartbeat.timeout", heartbeat * 3);
		if (heartbeatTimeout < heartbeat * 2)
		{
			throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
		} else
		{
			startHeatbeatTimer();
			return;
		}
	}

	public ResponseFuture request(Object request)
		throws RemotingException
	{
		return channel.request(request);
	}

	public URL getUrl()
	{
		return channel.getUrl();
	}

	public InetSocketAddress getRemoteAddress()
	{
		return channel.getRemoteAddress();
	}

	public ResponseFuture request(Object request, int timeout)
		throws RemotingException
	{
		return channel.request(request, timeout);
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

	public ExchangeHandler getExchangeHandler()
	{
		return channel.getExchangeHandler();
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

	public boolean isClosed()
	{
		return channel.isClosed();
	}

	public void close()
	{
		doClose();
		channel.close();
	}

	public void close(int timeout)
	{
		doClose();
		channel.close(timeout);
	}

	public void reset(URL url)
	{
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
		client.reconnect();
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

	private void startHeatbeatTimer()
	{
		stopHeartbeatTimer();
		if (heartbeat > 0)
			heatbeatTimer = scheduled.scheduleWithFixedDelay(new HeartBeatTask(new HeartBeatTask.ChannelProvider() {

				final HeaderExchangeClient this$0;

				public Collection getChannels()
				{
					return Collections.singletonList(HeaderExchangeClient.this);
				}

			
			{
				this$0 = HeaderExchangeClient.this;
				super();
			}
			}, heartbeat, heartbeatTimeout), heartbeat, heartbeat, TimeUnit.MILLISECONDS);
	}

	private void stopHeartbeatTimer()
	{
		if (heatbeatTimer != null && !heatbeatTimer.isCancelled())
			try
			{
				heatbeatTimer.cancel(true);
				scheduled.purge();
			}
			catch (Throwable e)
			{
				if (logger.isWarnEnabled())
					logger.warn(e.getMessage(), e);
			}
		heatbeatTimer = null;
	}

	private void doClose()
	{
		stopHeartbeatTimer();
	}

	public String toString()
	{
		return (new StringBuilder()).append("HeaderExchangeClient [channel=").append(channel).append("]").toString();
	}

}
