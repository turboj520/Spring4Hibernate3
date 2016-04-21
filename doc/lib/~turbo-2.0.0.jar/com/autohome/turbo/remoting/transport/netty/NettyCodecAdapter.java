// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyCodecAdapter.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.Codec2;
import com.autohome.turbo.remoting.buffer.ChannelBuffers;
import com.autohome.turbo.remoting.buffer.DynamicChannelBuffer;
import java.io.IOException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

// Referenced classes of package com.autohome.turbo.remoting.transport.netty:
//			NettyChannel

final class NettyCodecAdapter
{
	private class InternalDecoder extends SimpleChannelUpstreamHandler
	{

		private com.autohome.turbo.remoting.buffer.ChannelBuffer buffer;
		final NettyCodecAdapter this$0;

		public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
			throws Exception
		{
			com.autohome.turbo.remoting.buffer.ChannelBuffer message;
			NettyChannel channel;
			Object o = event.getMessage();
			if (!(o instanceof ChannelBuffer))
			{
				ctx.sendUpstream(event);
				return;
			}
			ChannelBuffer input = (ChannelBuffer)o;
			int readable = input.readableBytes();
			if (readable <= 0)
				return;
			if (buffer.readable())
			{
				if (buffer instanceof DynamicChannelBuffer)
				{
					buffer.writeBytes(input.toByteBuffer());
					message = buffer;
				} else
				{
					int size = buffer.readableBytes() + input.readableBytes();
					message = ChannelBuffers.dynamicBuffer(size <= bufferSize ? bufferSize : size);
					message.writeBytes(buffer, buffer.readableBytes());
					message.writeBytes(input.toByteBuffer());
				}
			} else
			{
				message = ChannelBuffers.wrappedBuffer(input.toByteBuffer());
			}
			channel = NettyChannel.getOrAddChannel(ctx.getChannel(), url, handler);
			do
			{
				int saveReaderIndex = message.readerIndex();
				Object msg;
				try
				{
					msg = codec.decode(channel, message);
				}
				catch (IOException e)
				{
					buffer = ChannelBuffers.EMPTY_BUFFER;
					throw e;
				}
				if (msg == com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT)
				{
					message.readerIndex(saveReaderIndex);
					break;
				}
				if (saveReaderIndex == message.readerIndex())
				{
					buffer = ChannelBuffers.EMPTY_BUFFER;
					throw new IOException("Decode without read data.");
				}
				if (msg != null)
					Channels.fireMessageReceived(ctx, msg, event.getRemoteAddress());
			} while (message.readable());
			if (message.readable())
			{
				message.discardReadBytes();
				buffer = message;
			} else
			{
				buffer = ChannelBuffers.EMPTY_BUFFER;
			}
			NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
			break MISSING_BLOCK_LABEL_422;
			Exception exception;
			exception;
			if (message.readable())
			{
				message.discardReadBytes();
				buffer = message;
			} else
			{
				buffer = ChannelBuffers.EMPTY_BUFFER;
			}
			NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
			throw exception;
		}

		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception
		{
			ctx.sendUpstream(e);
		}

		private InternalDecoder()
		{
			this$0 = NettyCodecAdapter.this;
			super();
			buffer = ChannelBuffers.EMPTY_BUFFER;
		}

	}

	private class InternalEncoder extends OneToOneEncoder
	{

		final NettyCodecAdapter this$0;

		protected Object encode(ChannelHandlerContext ctx, Channel ch, Object msg)
			throws Exception
		{
			com.autohome.turbo.remoting.buffer.ChannelBuffer buffer;
			NettyChannel channel;
			buffer = ChannelBuffers.dynamicBuffer(1024);
			channel = NettyChannel.getOrAddChannel(ch, url, handler);
			codec.encode(channel, buffer, msg);
			NettyChannel.removeChannelIfDisconnected(ch);
			break MISSING_BLOCK_LABEL_61;
			Exception exception;
			exception;
			NettyChannel.removeChannelIfDisconnected(ch);
			throw exception;
			return org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer(buffer.toByteBuffer());
		}

		private InternalEncoder()
		{
			this$0 = NettyCodecAdapter.this;
			super();
		}

	}


	private final org.jboss.netty.channel.ChannelHandler encoder = new InternalEncoder();
	private final org.jboss.netty.channel.ChannelHandler decoder = new InternalDecoder();
	private final Codec2 codec;
	private final URL url;
	private final int bufferSize;
	private final ChannelHandler handler;

	public NettyCodecAdapter(Codec2 codec, URL url, ChannelHandler handler)
	{
		this.codec = codec;
		this.url = url;
		this.handler = handler;
		int b = url.getPositiveParameter("buffer", 8192);
		bufferSize = b < 1024 || b > 16384 ? 8192 : b;
	}

	public org.jboss.netty.channel.ChannelHandler getEncoder()
	{
		return encoder;
	}

	public org.jboss.netty.channel.ChannelHandler getDecoder()
	{
		return decoder;
	}




}
