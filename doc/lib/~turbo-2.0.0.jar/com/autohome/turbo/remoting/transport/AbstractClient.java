// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractClient.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.store.DataStore;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.dispatcher.ChannelHandlers;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			AbstractEndpoint

public abstract class AbstractClient extends AbstractEndpoint
	implements Client
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/AbstractClient);
	protected static final String CLIENT_THREAD_POOL_NAME = "DubboClientHandler";
	private static final AtomicInteger CLIENT_THREAD_POOL_ID = new AtomicInteger();
	private final Lock connectLock = new ReentrantLock();
	private static final ScheduledThreadPoolExecutor reconnectExecutorService = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("DubboClientReconnectTimer", true));
	private volatile ScheduledFuture reconnectExecutorFuture;
	protected volatile ExecutorService executor;
	private final boolean send_reconnect;
	private final AtomicInteger reconnect_count = new AtomicInteger(0);
	private final AtomicBoolean reconnect_error_log_flag = new AtomicBoolean(false);
	private final int reconnect_warning_period;
	private long lastConnectedTime;
	private final long shutdown_timeout;

	public AbstractClient(URL url, ChannelHandler handler)
		throws RemotingException
	{
		super(url, handler);
		reconnectExecutorFuture = null;
		lastConnectedTime = System.currentTimeMillis();
		send_reconnect = url.getParameter("send.reconnect", false);
		shutdown_timeout = url.getParameter("shutdown.timeout", 0xdbba0);
		reconnect_warning_period = url.getParameter("reconnect.waring.period", 1800);
		try
		{
			doOpen();
		}
		catch (Throwable t)
		{
			close();
			throw new RemotingException(url.toInetSocketAddress(), null, (new StringBuilder()).append("Failed to start ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalAddress()).append(" connect to the server ").append(getRemoteAddress()).append(", cause: ").append(t.getMessage()).toString(), t);
		}
		try
		{
			connect();
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Start ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalAddress()).append(" connect to the server ").append(getRemoteAddress()).toString());
		}
		catch (RemotingException t)
		{
			if (url.getParameter("check", true))
			{
				close();
				throw t;
			}
			logger.warn((new StringBuilder()).append("Failed to start ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalAddress()).append(" connect to the server ").append(getRemoteAddress()).append(" (check == false, ignore and retry later!), cause: ").append(t.getMessage()).toString(), t);
		}
		catch (Throwable t)
		{
			close();
			throw new RemotingException(url.toInetSocketAddress(), null, (new StringBuilder()).append("Failed to start ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalAddress()).append(" connect to the server ").append(getRemoteAddress()).append(", cause: ").append(t.getMessage()).toString(), t);
		}
		executor = (ExecutorService)((DataStore)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/store/DataStore).getDefaultExtension()).get("consumer", Integer.toString(url.getPort()));
		((DataStore)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/store/DataStore).getDefaultExtension()).remove("consumer", Integer.toString(url.getPort()));
	}

	protected static ChannelHandler wrapChannelHandler(URL url, ChannelHandler handler)
	{
		url = ExecutorUtil.setThreadName(url, "DubboClientHandler");
		url = url.addParameterIfAbsent("threadpool", "cached");
		return ChannelHandlers.wrap(handler, url);
	}

	private synchronized void initConnectStatusCheckCommand()
	{
		int reconnect = getReconnectParam(getUrl());
		if (reconnect > 0 && (reconnectExecutorFuture == null || reconnectExecutorFuture.isCancelled()))
		{
			Runnable connectStatusCheckCommand = new Runnable() {

				final AbstractClient this$0;

				public void run()
				{
					try
					{
						if (!isConnected())
							connect();
						else
							lastConnectedTime = System.currentTimeMillis();
					}
					catch (Throwable t)
					{
						String errorMsg = (new StringBuilder()).append("client reconnect to ").append(getUrl().getAddress()).append(" find error . url: ").append(getUrl()).toString();
						if (System.currentTimeMillis() - lastConnectedTime > shutdown_timeout && !reconnect_error_log_flag.get())
						{
							reconnect_error_log_flag.set(true);
							AbstractClient.logger.error(errorMsg, t);
							return;
						}
						if (reconnect_count.getAndIncrement() % reconnect_warning_period == 0)
							AbstractClient.logger.warn(errorMsg, t);
					}
				}

			
			{
				this$0 = AbstractClient.this;
				super();
			}
			};
			reconnectExecutorFuture = reconnectExecutorService.scheduleWithFixedDelay(connectStatusCheckCommand, reconnect, reconnect, TimeUnit.MILLISECONDS);
		}
	}

	private static int getReconnectParam(URL url)
	{
		String param = url.getParameter("reconnect");
		int reconnect;
		if (param == null || param.length() == 0 || "true".equalsIgnoreCase(param))
			reconnect = 2000;
		else
		if ("false".equalsIgnoreCase(param))
		{
			reconnect = 0;
		} else
		{
			try
			{
				reconnect = Integer.parseInt(param);
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException((new StringBuilder()).append("reconnect param must be nonnegative integer or false/true. input is:").append(param).toString());
			}
			if (reconnect < 0)
				throw new IllegalArgumentException((new StringBuilder()).append("reconnect param must be nonnegative integer or false/true. input is:").append(param).toString());
		}
		return reconnect;
	}

	private synchronized void destroyConnectStatusCheckCommand()
	{
		try
		{
			if (reconnectExecutorFuture != null && !reconnectExecutorFuture.isDone())
			{
				reconnectExecutorFuture.cancel(true);
				reconnectExecutorService.purge();
			}
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

	protected ExecutorService createExecutor()
	{
		return Executors.newCachedThreadPool(new NamedThreadFactory((new StringBuilder()).append("DubboClientHandler").append(CLIENT_THREAD_POOL_ID.incrementAndGet()).append("-").append(getUrl().getAddress()).toString(), true));
	}

	public InetSocketAddress getConnectAddress()
	{
		return new InetSocketAddress(NetUtils.filterLocalHost(getUrl().getHost()), getUrl().getPort());
	}

	public InetSocketAddress getRemoteAddress()
	{
		Channel channel = getChannel();
		if (channel == null)
			return getUrl().toInetSocketAddress();
		else
			return channel.getRemoteAddress();
	}

	public InetSocketAddress getLocalAddress()
	{
		Channel channel = getChannel();
		if (channel == null)
			return InetSocketAddress.createUnresolved(NetUtils.getLocalHost(), 0);
		else
			return channel.getLocalAddress();
	}

	public boolean isConnected()
	{
		Channel channel = getChannel();
		if (channel == null)
			return false;
		else
			return channel.isConnected();
	}

	public Object getAttribute(String key)
	{
		Channel channel = getChannel();
		if (channel == null)
			return null;
		else
			return channel.getAttribute(key);
	}

	public void setAttribute(String key, Object value)
	{
		Channel channel = getChannel();
		if (channel == null)
		{
			return;
		} else
		{
			channel.setAttribute(key, value);
			return;
		}
	}

	public void removeAttribute(String key)
	{
		Channel channel = getChannel();
		if (channel == null)
		{
			return;
		} else
		{
			channel.removeAttribute(key);
			return;
		}
	}

	public boolean hasAttribute(String key)
	{
		Channel channel = getChannel();
		if (channel == null)
			return false;
		else
			return channel.hasAttribute(key);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		if (send_reconnect && !isConnected())
			connect();
		Channel channel = getChannel();
		if (channel == null || !channel.isConnected())
		{
			throw new RemotingException(this, (new StringBuilder()).append("message can not send, because channel is closed . url:").append(getUrl()).toString());
		} else
		{
			channel.send(message, sent);
			return;
		}
	}

	protected void connect()
		throws RemotingException
	{
		connectLock.lock();
		Exception exception;
		if (isConnected())
		{
			connectLock.unlock();
			return;
		}
		try
		{
			initConnectStatusCheckCommand();
			doConnect();
			if (!isConnected())
				throw new RemotingException(this, (new StringBuilder()).append("Failed connect to server ").append(getRemoteAddress()).append(" from ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalHost()).append(" using dubbo version ").append(Version.getVersion()).append(", cause: Connect wait timeout: ").append(getTimeout()).append("ms.").toString());
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Successed connect to server ").append(getRemoteAddress()).append(" from ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalHost()).append(" using dubbo version ").append(Version.getVersion()).append(", channel is ").append(getChannel()).toString());
			reconnect_count.set(0);
			reconnect_error_log_flag.set(false);
		}
		catch (RemotingException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new RemotingException(this, (new StringBuilder()).append("Failed connect to server ").append(getRemoteAddress()).append(" from ").append(getClass().getSimpleName()).append(" ").append(NetUtils.getLocalHost()).append(" using dubbo version ").append(Version.getVersion()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
		finally
		{
			connectLock.unlock();
		}
		connectLock.unlock();
		break MISSING_BLOCK_LABEL_341;
		throw exception;
	}

	public void disconnect()
	{
		connectLock.lock();
		destroyConnectStatusCheckCommand();
		try
		{
			Channel channel = getChannel();
			if (channel != null)
				channel.close();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			doDisConnect();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		connectLock.unlock();
		break MISSING_BLOCK_LABEL_90;
		Exception exception;
		exception;
		connectLock.unlock();
		throw exception;
	}

	public void reconnect()
		throws RemotingException
	{
		disconnect();
		connect();
	}

	public void close()
	{
		try
		{
			if (executor != null)
				ExecutorUtil.shutdownNow(executor, 100);
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			super.close();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			disconnect();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			doClose();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

	public void close(int timeout)
	{
		ExecutorUtil.gracefulShutdown(executor, timeout);
		close();
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getName()).append(" [").append(getLocalAddress()).append(" -> ").append(getRemoteAddress()).append("]").toString();
	}

	protected abstract void doOpen()
		throws Throwable;

	protected abstract void doClose()
		throws Throwable;

	protected abstract void doConnect()
		throws Throwable;

	protected abstract void doDisConnect()
		throws Throwable;

	protected abstract Channel getChannel();








}
