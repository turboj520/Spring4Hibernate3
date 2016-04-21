// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyBackedChannelBufferFactory.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.remoting.buffer.ChannelBufferFactory;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

// Referenced classes of package com.autohome.turbo.remoting.transport.netty:
//			NettyBackedChannelBuffer

public class NettyBackedChannelBufferFactory
	implements ChannelBufferFactory
{

	private static final NettyBackedChannelBufferFactory INSTANCE = new NettyBackedChannelBufferFactory();

	public NettyBackedChannelBufferFactory()
	{
	}

	public static ChannelBufferFactory getInstance()
	{
		return INSTANCE;
	}

	public com.autohome.turbo.remoting.buffer.ChannelBuffer getBuffer(int capacity)
	{
		return new NettyBackedChannelBuffer(ChannelBuffers.dynamicBuffer(capacity));
	}

	public com.autohome.turbo.remoting.buffer.ChannelBuffer getBuffer(byte array[], int offset, int length)
	{
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(length);
		buffer.writeBytes(array, offset, length);
		return new NettyBackedChannelBuffer(buffer);
	}

	public com.autohome.turbo.remoting.buffer.ChannelBuffer getBuffer(ByteBuffer nioBuffer)
	{
		return new NettyBackedChannelBuffer(ChannelBuffers.wrappedBuffer(nioBuffer));
	}

}
