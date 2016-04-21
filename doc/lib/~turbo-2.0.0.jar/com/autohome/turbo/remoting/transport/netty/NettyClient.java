// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyClient.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Codec2;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.transport.AbstractClient;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

// Referenced classes of package com.autohome.turbo.remoting.transport.netty:
//			NettyHandler, NettyHelper, NettyChannel, NettyCodecAdapter

public class NettyClient extends AbstractClient
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/netty/NettyClient);
	private static final ChannelFactory channelFactory;
	private ClientBootstrap bootstrap;
	private volatile Channel channel;

	public NettyClient(URL url, ChannelHandler handler)
		throws RemotingException
	{
		super(url, wrapChannelHandler(url, handler));
	}

	protected void doOpen()
		throws Throwable
	{
		NettyHelper.setNettyLoggerFactory();
		bootstrap = new ClientBootstrap(channelFactory);
		bootstrap.setOption("keepAlive", Boolean.valueOf(true));
		bootstrap.setOption("tcpNoDelay", Boolean.valueOf(true));
		bootstrap.setOption("connectTimeoutMillis", Integer.valueOf(getTimeout()));
		final NettyHandler nettyHandler = new NettyHandler(getUrl(), this);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			final NettyHandler val$nettyHandler;
			final NettyClient this$0;

			public ChannelPipeline getPipeline()
			{
				NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyClient.this);
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", adapter.getDecoder());
				pipeline.addLast("encoder", adapter.getEncoder());
				pipeline.addLast("handler", nettyHandler);
				return pipeline;
			}

			
			{
				this$0 = NettyClient.this;
				nettyHandler = nettyhandler;
				super();
			}
		});
	}

	protected void doConnect()
		throws Throwable
	{
		long start;
		ChannelFuture future;
		start = System.currentTimeMillis();
		future = bootstrap.connect(getConnectAddress());
		Channel newChannel;
		boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
		if (!ret || !future.isSuccess())
			break MISSING_BLOCK_LABEL_352;
		newChannel = future.getChannel();
		newChannel.setInterestOps(5);
		Channel oldChannel;
		oldChannel = channel;
		if (oldChannel == null)
			break MISSING_BLOCK_LABEL_149;
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Close old netty channel ").append(oldChannel).append(" on create new netty channel ").append(newChannel).toString());
		oldChannel.close();
		NettyChannel.removeChannelIfDisconnected(oldChannel);
		break MISSING_BLOCK_LABEL_149;
		Exception exception;
		exception;
		NettyChannel.removeChannelIfDisconnected(oldChannel);
		throw exception;
		if (!isClosed())
			break MISSING_BLOCK_LABEL_239;
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Close new netty channel ").append(newChannel).append(", because the client closed.").toString());
		newChannel.close();
		channel = null;
		NettyChannel.removeChannelIfDisconnected(newChannel);
		break MISSING_BLOCK_LABEL_517;
		Exception exception1;
		exception1;
		channel = null;
		NettyChannel.removeChannelIfDisconnected(newChannel);
		throw exception1;
		channel = newChannel;
		break MISSING_BLOCK_LABEL_517;
		Exception exception2;
		exception2;
		if (!isClosed())
			break MISSING_BLOCK_LABEL_340;
		if (logger.isInfoEnabled())
			logger.info((new StringBuilder()).append("Close new netty channel ").append(newChannel).append(", because the client closed.").toString());
		newChannel.close();
		channel = null;
		NettyChannel.removeChannelIfDisconnected(newChannel);
		break MISSING_BLOCK_LABEL_346;
		Exception exception3;
		exception3;
		channel = null;
		NettyChannel.removeChannelIfDisconnected(newChannel);
		throw exception3;
		channel = newChannel;
		throw exception2;
		if (future.getCause() != null)
			throw new RemotingException(this, (new StringBuilder()).append("client(url: ").append(getUrl()).append(") failed to connect to server ").append(getRemoteAddress()).append(", error message is:").append(future.getCause().getMessage()).toString(), future.getCause());
		else
			throw new RemotingException(this, (new StringBuilder()).append("client(url: ").append(getUrl()).append(") failed to connect to server ").append(getRemoteAddress()).append(" client-side timeout ").append(getConnectTimeout()).append("ms (elapsed: ").append(System.currentTimeMillis() - start).append("ms) from netty client ").append(NetUtils.getLocalHost()).append(" using dubbo version ").append(Version.getVersion()).toString());
		if (!isConnected())
			future.cancel();
		break MISSING_BLOCK_LABEL_553;
		Exception exception4;
		exception4;
		if (!isConnected())
			future.cancel();
		throw exception4;
	}

	protected void doDisConnect()
		throws Throwable
	{
		try
		{
			NettyChannel.removeChannelIfDisconnected(channel);
		}
		catch (Throwable t)
		{
			logger.warn(t.getMessage());
		}
	}

	protected void doClose()
		throws Throwable
	{
	}

	protected com.autohome.turbo.remoting.Channel getChannel()
	{
		Channel c = channel;
		if (c == null || !c.isConnected())
			return null;
		else
			return NettyChannel.getOrAddChannel(c, getUrl(), this);
	}

	static 
	{
		channelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(new NamedThreadFactory("NettyClientBoss", true)), Executors.newCachedThreadPool(new NamedThreadFactory("NettyClientWorker", true)), Constants.DEFAULT_IO_THREADS);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run()
			{
				if (NettyClient.logger.isInfoEnabled())
					NettyClient.logger.info("Run shutdown hook of netty client now.");
				try
				{
					NettyClient.channelFactory.releaseExternalResources();
				}
				catch (Throwable t)
				{
					NettyClient.logger.warn(t.getMessage());
				}
			}

		}, "DubboShutdownHook-NettyClient"));
	}



}
