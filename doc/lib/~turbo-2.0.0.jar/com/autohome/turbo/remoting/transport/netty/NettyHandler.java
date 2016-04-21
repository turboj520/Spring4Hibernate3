// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyHandler.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.ChannelHandler;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

// Referenced classes of package com.autohome.turbo.remoting.transport.netty:
//			NettyChannel

public class NettyHandler extends SimpleChannelHandler
{

	private final Map channels = new ConcurrentHashMap();
	private final URL url;
	private final ChannelHandler handler;

	public NettyHandler(URL url, ChannelHandler handler)
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		if (handler == null)
		{
			throw new IllegalArgumentException("handler == null");
		} else
		{
			this.url = url;
			this.handler = handler;
			return;
		}
	}

	public Map getChannels()
	{
		return channels;
	}

	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
		throws Exception
	{
		NettyChannel channel = NettyChannel.getOrAddChannel(ctx.getChannel(), url, handler);
		if (channel != null)
			channels.put(NetUtils.toAddressString((InetSocketAddress)ctx.getChannel().getRemoteAddress()), channel);
		handler.connected(channel);
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		break MISSING_BLOCK_LABEL_86;
		Exception exception;
		exception;
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		throw exception;
	}

	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
		throws Exception
	{
		NettyChannel channel = NettyChannel.getOrAddChannel(ctx.getChannel(), url, handler);
		channels.remove(NetUtils.toAddressString((InetSocketAddress)ctx.getChannel().getRemoteAddress()));
		handler.disconnected(channel);
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		break MISSING_BLOCK_LABEL_81;
		Exception exception;
		exception;
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		throw exception;
	}

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception
	{
		NettyChannel channel = NettyChannel.getOrAddChannel(ctx.getChannel(), url, handler);
		handler.received(channel, e.getMessage());
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		break MISSING_BLOCK_LABEL_60;
		Exception exception;
		exception;
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		throw exception;
	}

	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
		throws Exception
	{
		NettyChannel channel;
		super.writeRequested(ctx, e);
		channel = NettyChannel.getOrAddChannel(ctx.getChannel(), url, handler);
		handler.sent(channel, e.getMessage());
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		break MISSING_BLOCK_LABEL_66;
		Exception exception;
		exception;
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		throw exception;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
		throws Exception
	{
		NettyChannel channel = NettyChannel.getOrAddChannel(ctx.getChannel(), url, handler);
		handler.caught(channel, e.getCause());
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		break MISSING_BLOCK_LABEL_60;
		Exception exception;
		exception;
		NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
		throw exception;
	}
}
