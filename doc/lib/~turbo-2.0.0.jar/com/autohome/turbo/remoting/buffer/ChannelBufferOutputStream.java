// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelBufferOutputStream.java

package com.autohome.turbo.remoting.buffer;

import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBuffer

public class ChannelBufferOutputStream extends OutputStream
{

	private final ChannelBuffer buffer;
	private final int startIndex;

	public ChannelBufferOutputStream(ChannelBuffer buffer)
	{
		if (buffer == null)
		{
			throw new NullPointerException("buffer");
		} else
		{
			this.buffer = buffer;
			startIndex = buffer.writerIndex();
			return;
		}
	}

	public int writtenBytes()
	{
		return buffer.writerIndex() - startIndex;
	}

	public void write(byte b[], int off, int len)
		throws IOException
	{
		if (len == 0)
		{
			return;
		} else
		{
			buffer.writeBytes(b, off, len);
			return;
		}
	}

	public void write(byte b[])
		throws IOException
	{
		buffer.writeBytes(b);
	}

	public void write(int b)
		throws IOException
	{
		buffer.writeByte((byte)b);
	}

	public ChannelBuffer buffer()
	{
		return buffer;
	}
}
