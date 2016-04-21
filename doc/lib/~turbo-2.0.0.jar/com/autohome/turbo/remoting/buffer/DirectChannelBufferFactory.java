// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DirectChannelBufferFactory.java

package com.autohome.turbo.remoting.buffer;

import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBufferFactory, ChannelBuffers, ChannelBuffer

public class DirectChannelBufferFactory
	implements ChannelBufferFactory
{

	private static final DirectChannelBufferFactory INSTANCE = new DirectChannelBufferFactory();

	public static ChannelBufferFactory getInstance()
	{
		return INSTANCE;
	}

	public DirectChannelBufferFactory()
	{
	}

	public ChannelBuffer getBuffer(int capacity)
	{
		if (capacity < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("capacity: ").append(capacity).toString());
		if (capacity == 0)
			return ChannelBuffers.EMPTY_BUFFER;
		else
			return ChannelBuffers.directBuffer(capacity);
	}

	public ChannelBuffer getBuffer(byte array[], int offset, int length)
	{
		if (array == null)
			throw new NullPointerException("array");
		if (offset < 0)
			throw new IndexOutOfBoundsException((new StringBuilder()).append("offset: ").append(offset).toString());
		if (length == 0)
			return ChannelBuffers.EMPTY_BUFFER;
		if (offset + length > array.length)
		{
			throw new IndexOutOfBoundsException((new StringBuilder()).append("length: ").append(length).toString());
		} else
		{
			ChannelBuffer buf = getBuffer(length);
			buf.writeBytes(array, offset, length);
			return buf;
		}
	}

	public ChannelBuffer getBuffer(ByteBuffer nioBuffer)
	{
		if (!nioBuffer.isReadOnly() && nioBuffer.isDirect())
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
