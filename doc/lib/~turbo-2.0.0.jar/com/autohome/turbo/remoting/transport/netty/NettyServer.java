// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyServer.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ExecutorUtil;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Codec2;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.Server;
import com.autohome.turbo.remoting.transport.AbstractServer;
import com.autohome.turbo.remoting.transport.dispatcher.ChannelHandlers;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

// Referenced classes of package com.autohome.turbo.remoting.transport.netty:
//			NettyHandler, NettyHelper, NettyCodecAdapter

public class NettyServer extends AbstractServer
	implements Server
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/netty/NettyServer);
	private Map channels;
	private ServerBootstrap bootstrap;
	private org.jboss.netty.channel.Channel channel;

	public NettyServer(URL url, ChannelHandler handler)
		throws RemotingException
	{
		super(url, ChannelHandlers.wrap(handler, ExecutorUtil.setThreadName(url, "DubboServerHandler")));
	}

	protected void doOpen()
		throws Throwable
	{
		NettyHelper.setNettyLoggerFactory();
		java.util.concurrent.ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerBoss", true));
		java.util.concurrent.ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerWorker", true));
		org.jboss.netty.channel.ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker, getUrl().getPositiveParameter("iothreads", Constants.DEFAULT_IO_THREADS));
		bootstrap = new ServerBootstrap(channelFactory);
		final NettyHandler nettyHandler = new NettyHandler(getUrl(), this);
		channels = nettyHandler.getChannels();
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			final NettyHandler val$nettyHandler;
			final NettyServer this$0;

			public ChannelPipeline getPipeline()
			{
				NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyServer.this);
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", adapter.getDecoder());
				pipeline.addLast("encoder", adapter.getEncoder());
				pipeline.addLast("handler", nettyHandler);
				return pipeline;
			}

			
			{
				this$0 = NettyServer.this;
				nettyHandler = nettyhandler;
				super();
			}
		});
		channel = bootstrap.bind(getBindAddress());
	}

	protected void doClose()
		throws Throwable
	{
		try
		{
			if (this.channel != null)
				this.channel.close();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			Collection channels = getChannels();
			if (channels != null && channels.size() > 0)
			{
				for (Iterator i$ = channels.iterator(); i$.hasNext();)
				{
					Channel channel = (Channel)i$.next();
					try
					{
						channel.close();
					}
					catch (Throwable e)
					{
						logger.warn(e.getMessage(), e);
					}
				}

			}
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			if (bootstrap != null)
				bootstrap.releaseExternalResources();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
		try
		{
			if (this.channels != null)
				this.channels.clear();
		}
		catch (Throwable e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

	public Collection getChannels()
	{
		Collection chs = new HashSet();
		for (Iterator i$ = channels.values().iterator(); i$.hasNext();)
		{
			Channel channel = (Channel)i$.next();
			if (channel.isConnected())
				chs.add(channel);
			else
				channels.remove(NetUtils.toAddressString(channel.getRemoteAddress()));
		}

		return chs;
	}

	public Channel getChannel(InetSocketAddress remoteAddress)
	{
		return (Channel)channels.get(NetUtils.toAddressString(remoteAddress));
	}

	public boolean isBound()
	{
		return channel.isBound();
	}


}
