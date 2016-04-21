// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ByteBufferBackedChannelBuffer.java

package com.autohome.turbo.remoting.buffer;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			AbstractChannelBuffer, DirectChannelBufferFactory, HeapChannelBufferFactory, ChannelBuffer, 
//			ChannelBufferFactory

public class ByteBufferBackedChannelBuffer extends AbstractChannelBuffer
{

	private final ByteBuffer buffer;
	private final int capacity;

	public ByteBufferBackedChannelBuffer(ByteBuffer buffer)
	{
		if (buffer == null)
		{
			throw new NullPointerException("buffer");
		} else
		{
			this.buffer = buffer.slice();
			capacity = buffer.remaining();
			writerIndex(capacity);
			return;
		}
	}

	private ByteBufferBackedChannelBuffer(ByteBufferBackedChannelBuffer buffer)
	{
		this.buffer = buffer.buffer;
		capacity = buffer.capacity;
		setIndex(buffer.readerIndex(), buffer.writerIndex());
	}

	public ChannelBufferFactory factory()
	{
		if (buffer.isDirect())
			return DirectChannelBufferFactory.getInstance();
		else
			return HeapChannelBufferFactory.getInstance();
	}

	public int capacity()
	{
		return capacity;
	}

	public ChannelBuffer copy(int index, int length)
	{
		ByteBuffer src;
		try
		{
			src = (ByteBuffer)buffer.duplicate().position(index).limit(index + length);
		}
		catch (IllegalArgumentException e)
		{
			throw new IndexOutOfBoundsException();
		}
		ByteBuffer dst = buffer.isDirect() ? ByteBuffer.allocateDirect(length) : ByteBuffer.allocate(length);
		dst.put(src);
		dst.clear();
		return new ByteBufferBackedChannelBuffer(dst);
	}

	public byte getByte(int index)
	{
		return buffer.get(index);
	}

	public void getBytes(int index, byte dst[], int dstIndex, int length)
	{
		ByteBuffer data = buffer.duplicate();
		try
		{
			data.limit(index + length).position(index);
		}
		catch (IllegalArgumentException e)
		{
			throw new IndexOutOfBoundsException();
		}
		data.get(dst, dstIndex, length);
	}

	public void getBytes(int index, ByteBuffer dst)
	{
		ByteBuffer data = buffer.duplicate();
		int bytesToCopy = Math.min(capacity() - index, dst.remaining());
		try
		{
			data.limit(index + bytesToCopy).position(index);
		}
		catch (IllegalArgumentException e)
		{
			throw new IndexOutOfBoundsException();
		}
		dst.put(data);
	}

	public void getBytes(int index, ChannelBuffer dst, int dstIndex, int length)
	{
		if (dst instanceof ByteBufferBackedChannelBuffer)
		{
			ByteBufferBackedChannelBuffer bbdst = (ByteBufferBackedChannelBuffer)dst;
			ByteBuffer data = bbdst.buffer.duplicate();
			data.limit(dstIndex + length).position(dstIndex);
			getBytes(index, data);
		} else
		if (buffer.hasArray())
			dst.setBytes(dstIndex, buffer.array(), index + buffer.arrayOffset(), length);
		else
			dst.setBytes(dstIndex, this, index, length);
	}

	public void getBytes(int index, OutputStream out, int length)
		throws IOException
	{
		if (length == 0)
			return;
		if (buffer.hasArray())
		{
			out.write(buffer.array(), index + buffer.arrayOffset(), length);
		} else
		{
			byte tmp[] = new byte[length];
			((ByteBuffer)buffer.duplicate().position(index)).get(tmp);
			out.write(tmp);
		}
	}

	public boolean isDirect()
	{
		return buffer.isDirect();
	}

	public void setByte(int index, int value)
	{
		buffer.put(index, (byte)value);
	}

	public void setBytes(int index, byte src[], int srcIndex, int length)
	{
		ByteBuffer data = buffer.duplicate();
		data.limit(index + length).position(index);
		data.put(src, srcIndex, length);
	}

	public void setBytes(int index, ByteBuffer src)
	{
		ByteBuffer data = buffer.duplicate();
		data.limit(index + src.remaining()).position(index);
		data.put(src);
	}

	public void setBytes(int index, ChannelBuffer src, int srcIndex, int length)
	{
		if (src instanceof ByteBufferBackedChannelBuffer)
		{
			ByteBufferBackedChannelBuffer bbsrc = (ByteBufferBackedChannelBuffer)src;
			ByteBuffer data = bbsrc.buffer.duplicate();
			data.limit(srcIndex + length).position(srcIndex);
			setBytes(index, data);
		} else
		if (buffer.hasArray())
			src.getBytes(srcIndex, buffer.array(), index + buffer.arrayOffset(), length);
		else
			src.getBytes(srcIndex, this, index, length);
	}

	public ByteBuffer toByteBuffer(int index, int length)
	{
		if (index == 0 && length == capacity())
			return buffer.duplicate();
		else
			return ((ByteBuffer)buffer.duplicate().position(index).limit(index + length)).slice();
	}

	public int setBytes(int index, InputStream in, int length)
		throws IOException
	{
		int readBytes = 0;
		if (buffer.hasArray())
		{
			index += buffer.arrayOffset();
			do
			{
				int localReadBytes = in.read(buffer.array(), index, length);
				if (localReadBytes < 0)
				{
					if (readBytes == 0)
						return -1;
					break;
				}
				readBytes += localReadBytes;
				index += localReadBytes;
				length -= localReadBytes;
			} while (length > 0);
		} else
		{
			byte tmp[] = new byte[length];
			int i = 0;
			do
			{
				int localReadBytes = in.read(tmp, i, tmp.length - i);
				if (localReadBytes < 0)
				{
					if (readBytes == 0)
						return -1;
					break;
				}
				readBytes += localReadBytes;
				i += readBytes;
			} while (i < tmp.length);
			((ByteBuffer)buffer.duplicate().position(index)).put(tmp);
		}
		return readBytes;
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
