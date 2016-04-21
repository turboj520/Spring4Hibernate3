// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractServer.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.store.DataStore;
import com.autohome.turbo.common.utils.ExecutorUtil;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.*;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			AbstractEndpoint

public abstract class AbstractServer extends AbstractEndpoint
	implements Server
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/AbstractServer);
	private InetSocketAddress localAddress;
	private InetSocketAddress bindAddress;
	private int accepts;
	private int idleTimeout;
	protected static final String SERVER_THREAD_POOL_NAME = "DubboServerHandler";
	ExecutorService executor;

	public AbstractServer(URL url, ChannelHandler handler)
		throws RemotingException
	{
		super(url, handler);
		idleTimeout = 600;
		localAddress = getUrl().toInetSocketAddress();
		String host = !url.getParameter("anyhost", false) && !NetUtils.isInvalidLocalHost(getUrl().getHost()) ? getUrl().getHost() : "0.0.0.0";
		bindAddress = new InetSocketAddress(host, getUrl().getPort());
		accepts = url.getParameter("accepts", 0);
		idleTimeout = url.getParameter("idle.timeout", 0x927c0);
		try
		{
			doOpen();
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder()).append("Start ").append(getClass().getSimpleName()).append(" bind ").append(getBindAddress()).append(", export ").append(getLocalAddress()).toString());
		}
		catch (Throwable t)
		{
			throw new RemotingException(url.toInetSocketAddress(), null, (new StringBuilder()).append("Failed to bind ").append(getClass().getSimpleName()).append(" on ").append(getLocalAddress()).append(", cause: ").append(t.getMessage()).toString(), t);
		}
		executor = (ExecutorService)((DataStore)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/store/DataStore).getDefaultExtension()).get(Constants.EXECUTOR_SERVICE_COMPONENT_KEY, Integer.toString(url.getPort()));
	}

	protected abstract void doOpen()
		throws Throwable;

	protected abstract void doClose()
		throws Throwable;

	public void reset(URL url)
	{
		if (url == null)
			return;
		try
		{
			if (url.hasParameter("accepts"))
			{
				int a = url.getParameter("accepts", 0);
				if (a > 0)
					accepts = a;
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
		try
		{
			if (url.hasParameter("idle.timeout"))
			{
				int t = url.getParameter("idle.timeout", 0);
				if (t > 0)
					idleTimeout = t;
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
		try
		{
			if (url.hasParameter("threads") && (executor instanceof ThreadPoolExecutor) && !executor.isShutdown())
			{
				ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executor;
				int threads = url.getParameter("threads", 0);
				int max = threadPoolExecutor.getMaximumPoolSize();
				int core = threadPoolExecutor.getCorePoolSize();
				if (threads > 0 && (threads != max || threads != core))
					if (threads < core)
					{
						threadPoolExecutor.setCorePoolSize(threads);
						if (core == max)
							threadPoolExecutor.setMaximumPoolSize(threads);
					} else
					{
						threadPoolExecutor.setMaximumPoolSize(threads);
						if (core == max)
							threadPoolExecutor.setCorePoolSize(threads);
					}
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
		super.setUrl(getUrl().addParameters(url.getParameters()));
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		Collection channels = getChannels();
		Iterator i$ = channels.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Channel channel = (Channel)i$.next();
			if (channel.isConnected())
				channel.send(message, sent);
		} while (true);
	}

	public void close()
	{
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Close ").append(getClass().getSimpleName()).append(" bind ").append(getBindAddress()).append(", export ").append(getLocalAddress()).toString());
		ExecutorUtil.shutdownNow(executor, 100);
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

	public InetSocketAddress getLocalAddress()
	{
		return localAddress;
	}

	public InetSocketAddress getBindAddress()
	{
		return bindAddress;
	}

	public int getAccepts()
	{
		return accepts;
	}

	public int getIdleTimeout()
	{
		return idleTimeout;
	}

	public void connected(Channel ch)
		throws RemotingException
	{
		Collection channels = getChannels();
		if (accepts > 0 && channels.size() > accepts)
		{
			logger.error((new StringBuilder()).append("Close channel ").append(ch).append(", cause: The server ").append(ch.getLocalAddress()).append(" connections greater than max config ").append(accepts).toString());
			ch.close();
			return;
		} else
		{
			super.connected(ch);
			return;
		}
	}

	public void disconnected(Channel ch)
		throws RemotingException
	{
		Collection channels = getChannels();
		if (channels.size() == 0)
			logger.warn((new StringBuilder()).append("All clients has discontected from ").append(ch.getLocalAddress()).append(". You can graceful shutdown now.").toString());
		super.disconnected(ch);
	}

}
