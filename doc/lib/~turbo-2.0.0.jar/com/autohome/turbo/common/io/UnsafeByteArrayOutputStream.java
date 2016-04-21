// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UnsafeByteArrayOutputStream.java

package com.autohome.turbo.common.io;

import java.io.*;
import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.common.io:
//			Bytes

public class UnsafeByteArrayOutputStream extends OutputStream
{

	protected byte mBuffer[];
	protected int mCount;

	public UnsafeByteArrayOutputStream()
	{
		this(32);
	}

	public UnsafeByteArrayOutputStream(int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("Negative initial size: ").append(size).toString());
		} else
		{
			mBuffer = new byte[size];
			return;
		}
	}

	public void write(int b)
	{
		int newcount = mCount + 1;
		if (newcount > mBuffer.length)
			mBuffer = Bytes.copyOf(mBuffer, Math.max(mBuffer.length << 1, newcount));
		mBuffer[mCount] = (byte)b;
		mCount = newcount;
	}

	public void write(byte b[], int off, int len)
	{
		if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0)
			throw new IndexOutOfBoundsException();
		if (len == 0)
			return;
		int newcount = mCount + len;
		if (newcount > mBuffer.length)
			mBuffer = Bytes.copyOf(mBuffer, Math.max(mBuffer.length << 1, newcount));
		System.arraycopy(b, off, mBuffer, mCount, len);
		mCount = newcount;
	}

	public int size()
	{
		return mCount;
	}

	public void reset()
	{
		mCount = 0;
	}

	public byte[] toByteArray()
	{
		return Bytes.copyOf(mBuffer, mCount);
	}

	public ByteBuffer toByteBuffer()
	{
		return ByteBuffer.wrap(mBuffer, 0, mCount);
	}

	public void writeTo(OutputStream out)
		throws IOException
	{
		out.write(mBuffer, 0, mCount);
	}

	public String toString()
	{
		return new String(mBuffer, 0, mCount);
	}

	public String toString(String charset)
		throws UnsupportedEncodingException
	{
		return new String(mBuffer, 0, mCount, charset);
	}

	public void close()
		throws IOException
	{
	}
}
