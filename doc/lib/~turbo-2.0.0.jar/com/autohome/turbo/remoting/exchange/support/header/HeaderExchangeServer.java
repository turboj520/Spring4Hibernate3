// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeaderExchangeServer.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.*;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import com.autohome.turbo.remoting.exchange.support.DefaultFuture;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support.header:
//			HeartBeatTask, HeaderExchangeChannel

public class HeaderExchangeServer
	implements ExchangeServer
{

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1, new NamedThreadFactory("dubbo-remoting-server-heartbeat", true));
	private ScheduledFuture heatbeatTimer;
	private int heartbeat;
	private int heartbeatTimeout;
	private final Server server;
	private volatile boolean closed;

	public HeaderExchangeServer(Server server)
	{
		closed = false;
		if (server == null)
			throw new IllegalArgumentException("server == null");
		this.server = server;
		heartbeat = server.getUrl().getParameter("heartbeat", 0);
		heartbeatTimeout = server.getUrl().getParameter("heartbeat.timeout", heartbeat * 3);
		if (heartbeatTimeout < heartbeat * 2)
		{
			throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
		} else
		{
			startHeatbeatTimer();
			return;
		}
	}

	public Server getServer()
	{
		return server;
	}

	public boolean isClosed()
	{
		return server.isClosed();
	}

	private boolean isRunning()
	{
		Collection channels = getChannels();
		for (Iterator i$ = channels.iterator(); i$.hasNext();)
		{
			Channel channel = (Channel)i$.next();
			if (DefaultFuture.hasFuture(channel))
				return true;
		}

		return false;
	}

	public void close()
	{
		doClose();
		server.close();
	}

	public void close(int timeout)
	{
		if (timeout > 0)
		{
			long max = timeout;
			long start = System.currentTimeMillis();
			if (getUrl().getParameter("channel.readonly.send", false))
				sendChannelReadOnlyEvent();
			while (isRunning() && System.currentTimeMillis() - start < max) 
				try
				{
					Thread.sleep(10L);
				}
				catch (InterruptedException e)
				{
					logger.warn(e.getMessage(), e);
				}
		}
		doClose();
		server.close(timeout);
	}

	private void sendChannelReadOnlyEvent()
	{
		Request request = new Request();
		request.setEvent("R");
		request.setTwoWay(false);
		request.setVersion(Version.getVersion());
		Collection channels = getChannels();
		Iterator i$ = channels.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Channel channel = (Channel)i$.next();
			try
			{
				if (channel.isConnected())
					channel.send(request, getUrl().getParameter("channel.readonly.sent", true));
			}
			catch (RemotingException e)
			{
				logger.warn("send connot write messge error.", e);
			}
		} while (true);
	}

	private void doClose()
	{
		if (closed)
			return;
		closed = true;
		stopHeartbeatTimer();
		try
		{
			scheduled.shutdown();
		}
		catch (Throwable t)
		{
			logger.warn(t.getMessage(), t);
		}
	}

	public Collection getExchangeChannels()
	{
		Collection exchangeChannels = new ArrayList();
		Collection channels = server.getChannels();
		if (channels != null && channels.size() > 0)
		{
			Channel channel;
			for (Iterator i$ = channels.iterator(); i$.hasNext(); exchangeChannels.add(HeaderExchangeChannel.getOrAddChannel(channel)))
				channel = (Channel)i$.next();

		}
		return exchangeChannels;
	}

	public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress)
	{
		Channel channel = server.getChannel(remoteAddress);
		return HeaderExchangeChannel.getOrAddChannel(channel);
	}

	public Collection getChannels()
	{
		return getExchangeChannels();
	}

	public Channel getChannel(InetSocketAddress remoteAddress)
	{
		return getExchangeChannel(remoteAddress);
	}

	public boolean isBound()
	{
		return server.isBound();
	}

	public InetSocketAddress getLocalAddress()
	{
		return server.getLocalAddress();
	}

	public URL getUrl()
	{
		return server.getUrl();
	}

	public ChannelHandler getChannelHandler()
	{
		return server.getChannelHandler();
	}

	public void reset(URL url)
	{
		server.reset(url);
		try
		{
			if (url.hasParameter("heartbeat") || url.hasParameter("heartbeat.timeout"))
			{
				int h = url.getParameter("heartbeat", heartbeat);
				int t = url.getParameter("heartbeat.timeout", h * 3);
				if (t < h * 2)
					throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
				if (h != heartbeat || t != heartbeatTimeout)
				{
					heartbeat = h;
					heartbeatTimeout = t;
					startHeatbeatTimer();
				}
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
	}

	/**
	 * @deprecated Method reset is deprecated
	 */

	public void reset(Parameters parameters)
	{
		reset(getUrl().addParameters(parameters.getParameters()));
	}

	public void send(Object message)
		throws RemotingException
	{
		if (closed)
		{
			throw new RemotingException(getLocalAddress(), null, (new StringBuilder()).append("Failed to send message ").append(message).append(", cause: The server ").append(getLocalAddress()).append(" is closed!").toString());
		} else
		{
			server.send(message);
			return;
		}
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		if (closed)
		{
			throw new RemotingException(getLocalAddress(), null, (new StringBuilder()).append("Failed to send message ").append(message).append(", cause: The server ").append(getLocalAddress()).append(" is closed!").toString());
		} else
		{
			server.send(message, sent);
			return;
		}
	}

	private void startHeatbeatTimer()
	{
		stopHeartbeatTimer();
		if (heartbeat > 0)
			heatbeatTimer = scheduled.scheduleWithFixedDelay(new HeartBeatTask(new HeartBeatTask.ChannelProvider() {

				final HeaderExchangeServer this$0;

				public Collection getChannels()
				{
					return Collections.unmodifiableCollection(HeaderExchangeServer.this.getChannels());
				}

			
			{
				this$0 = HeaderExchangeServer.this;
				super();
			}
			}, heartbeat, heartbeatTimeout), heartbeat, heartbeat, TimeUnit.MILLISECONDS);
	}

	private void stopHeartbeatTimer()
	{
		ScheduledFuture timer = heatbeatTimer;
		if (timer != null && !timer.isCancelled())
			timer.cancel(true);
		heatbeatTimer = null;
		break MISSING_BLOCK_LABEL_65;
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		heatbeatTimer = null;
		break MISSING_BLOCK_LABEL_65;
		Exception exception;
		exception;
		heatbeatTimer = null;
		throw exception;
	}
}
