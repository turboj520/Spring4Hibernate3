// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DynamicChannelBuffer.java

package com.autohome.turbo.remoting.buffer;

import java.io.*;
import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			AbstractChannelBuffer, ChannelBufferFactory, HeapChannelBufferFactory, ChannelBuffer

public class DynamicChannelBuffer extends AbstractChannelBuffer
{

	private final ChannelBufferFactory factory;
	private ChannelBuffer buffer;

	public DynamicChannelBuffer(int estimatedLength)
	{
		this(estimatedLength, HeapChannelBufferFactory.getInstance());
	}

	public DynamicChannelBuffer(int estimatedLength, ChannelBufferFactory factory)
	{
		if (estimatedLength < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("estimatedLength: ").append(estimatedLength).toString());
		if (factory == null)
		{
			throw new NullPointerException("factory");
		} else
		{
			this.factory = factory;
			buffer = factory.getBuffer(estimatedLength);
			return;
		}
	}

	public void ensureWritableBytes(int minWritableBytes)
	{
		if (minWritableBytes <= writableBytes())
			return;
		int newCapacity;
		if (capacity() == 0)
			newCapacity = 1;
		else
			newCapacity = capacity();
		for (int minNewCapacity = writerIndex() + minWritableBytes; newCapacity < minNewCapacity; newCapacity <<= 1);
		ChannelBuffer newBuffer = factory().getBuffer(newCapacity);
		newBuffer.writeBytes(buffer, 0, writerIndex());
		buffer = newBuffer;
	}

	public int capacity()
	{
		return buffer.capacity();
	}

	public ChannelBuffer copy(int index, int length)
	{
		DynamicChannelBuffer copiedBuffer = new DynamicChannelBuffer(Math.max(length, 64), factory());
		copiedBuffer.buffer = buffer.copy(index, length);
		copiedBuffer.setIndex(0, length);
		return copiedBuffer;
	}

	public ChannelBufferFactory factory()
	{
		return factory;
	}

	public byte getByte(int index)
	{
		return buffer.getByte(index);
	}

	public void getBytes(int index, byte dst[], int dstIndex, int length)
	{
		buffer.getBytes(index, dst, dstIndex, length);
	}

	public void getBytes(int index, ByteBuffer dst)
	{
		buffer.getBytes(index, dst);
	}

	public void getBytes(int index, ChannelBuffer dst, int dstIndex, int length)
	{
		buffer.getBytes(index, dst, dstIndex, length);
	}

	public void getBytes(int index, OutputStream dst, int length)
		throws IOException
	{
		buffer.getBytes(index, dst, length);
	}

	public boolean isDirect()
	{
		return buffer.isDirect();
	}

	public void setByte(int index, int value)
	{
		buffer.setByte(index, value);
	}

	public void setBytes(int index, byte src[], int srcIndex, int length)
	{
		buffer.setBytes(index, src, srcIndex, length);
	}

	public void setBytes(int index, ByteBuffer src)
	{
		buffer.setBytes(index, src);
	}

	public void setBytes(int index, ChannelBuffer src, int srcIndex, int length)
	{
		buffer.setBytes(index, src, srcIndex, length);
	}

	public int setBytes(int index, InputStream src, int length)
		throws IOException
	{
		return buffer.setBytes(index, src, length);
	}

	public ByteBuffer toByteBuffer(int index, int length)
	{
		return buffer.toByteBuffer(index, length);
	}

	public void writeByte(int value)
	{
		ensureWritableBytes(1);
		super.writeByte(value);
	}

	public void writeBytes(byte src[], int srcIndex, int length)
	{
		ensureWritableBytes(length);
		super.writeBytes(src, srcIndex, length);
	}

	public void writeBytes(ChannelBuffer src, int srcIndex, int length)
	{
		ensureWritableBytes(length);
		super.writeBytes(src, srcIndex, length);
	}

	public void writeBytes(ByteBuffer src)
	{
		ensureWritableBytes(src.remaining());
		super.writeBytes(src);
	}

	public int writeBytes(InputStream in, int length)
		throws IOException
	{
		ensureWritableBytes(length);
		return super.writeBytes(in, length);
	}

	public byte[] array()
	{
		return buffer.array();
	}

	public boolean hasArray()
	{
		return buffer.hasArray();
	}

	public int arrayOffset()
	{
		return buffer.arrayOffset();
	}
}
