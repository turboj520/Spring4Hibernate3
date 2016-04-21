// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelBufferInputStream.java

package com.autohome.turbo.remoting.buffer;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBuffer

public class ChannelBufferInputStream extends InputStream
{

	private final ChannelBuffer buffer;
	private final int startIndex;
	private final int endIndex;

	public ChannelBufferInputStream(ChannelBuffer buffer)
	{
		this(buffer, buffer.readableBytes());
	}

	public ChannelBufferInputStream(ChannelBuffer buffer, int length)
	{
		if (buffer == null)
			throw new NullPointerException("buffer");
		if (length < 0)
			throw new IllegalArgumentException((new StringBuilder()).append("length: ").append(length).toString());
		if (length > buffer.readableBytes())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			this.buffer = buffer;
			startIndex = buffer.readerIndex();
			endIndex = startIndex + length;
			buffer.markReaderIndex();
			return;
		}
	}

	public int readBytes()
	{
		return buffer.readerIndex() - startIndex;
	}

	public int available()
		throws IOException
	{
		return endIndex - buffer.readerIndex();
	}

	public void mark(int readlimit)
	{
		buffer.markReaderIndex();
	}

	public boolean markSupported()
	{
		return true;
	}

	public int read()
		throws IOException
	{
		if (!buffer.readable())
			return -1;
		else
			return buffer.readByte() & 0xff;
	}

	public int read(byte b[], int off, int len)
		throws IOException
	{
		int available = available();
		if (available == 0)
		{
			return -1;
		} else
		{
			len = Math.min(available, len);
			buffer.readBytes(b, off, len);
			return len;
		}
	}

	public void reset()
		throws IOException
	{
		buffer.resetReaderIndex();
	}

	public long skip(long n)
		throws IOException
	{
		if (n > 0x7fffffffL)
			return (long)skipBytes(0x7fffffff);
		else
			return (long)skipBytes((int)n);
	}

	private int skipBytes(int n)
		throws IOException
	{
		int nBytes = Math.min(available(), n);
		buffer.skipBytes(nBytes);
		return nBytes;
	}
}
