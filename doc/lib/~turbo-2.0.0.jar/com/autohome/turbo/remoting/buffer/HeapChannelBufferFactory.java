// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeapChannelBufferFactory.java

package com.autohome.turbo.remoting.buffer;

import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBufferFactory, ChannelBuffers, ChannelBuffer

public class HeapChannelBufferFactory
	implements ChannelBufferFactory
{

	private static final HeapChannelBufferFactory INSTANCE = new HeapChannelBufferFactory();

	public static ChannelBufferFactory getInstance()
	{
		return INSTANCE;
	}

	public HeapChannelBufferFactory()
	{
	}

	public ChannelBuffer getBuffer(int capacity)
	{
		return ChannelBuffers.buffer(capacity);
	}

	public ChannelBuffer getBuffer(byte array[], int offset, int length)
	{
		return ChannelBuffers.wrappedBuffer(array, offset, length);
	}

	public ChannelBuffer getBuffer(ByteBuffer nioBuffer)
	{
		if (nioBuffer.hasArray())
		{
			return ChannelBuffers.wrappedBuffer(nioBuffer);
		} else
		{
			ChannelBuffer buf = getBuffer(nioBuffer.remaining());
			int pos = nioBuffer.position();
			buf.writeBytes(nioBuffer);
			nioBuffer.position(pos);
			return buf;
		}
	}

}
