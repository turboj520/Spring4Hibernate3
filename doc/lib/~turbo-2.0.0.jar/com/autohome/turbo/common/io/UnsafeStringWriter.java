// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UnsafeStringWriter.java

package com.autohome.turbo.common.io;

import java.io.IOException;
import java.io.Writer;

public class UnsafeStringWriter extends Writer
{

	private StringBuilder mBuffer;

	public UnsafeStringWriter()
	{
		lock = mBuffer = new StringBuilder();
	}

	public UnsafeStringWriter(int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException("Negative buffer size");
		} else
		{
			lock = mBuffer = new StringBuilder();
			return;
		}
	}

	public void write(int c)
	{
		mBuffer.append((char)c);
	}

	public void write(char cs[])
		throws IOException
	{
		mBuffer.append(cs, 0, cs.length);
	}

	public void write(char cs[], int off, int len)
		throws IOException
	{
		if (off < 0 || off > cs.length || len < 0 || off + len > cs.length || off + len < 0)
			throw new IndexOutOfBoundsException();
		if (len > 0)
			mBuffer.append(cs, off, len);
	}

	public void write(String str)
	{
		mBuffer.append(str);
	}

	public void write(String str, int off, int len)
	{
		mBuffer.append(str.substring(off, off + len));
	}

	public Writer append(CharSequence csq)
	{
		if (csq == null)
			write("null");
		else
			write(csq.toString());
		return this;
	}

	public Writer append(CharSequence csq, int start, int end)
	{
		CharSequence cs = ((CharSequence) (csq != null ? csq : "null"));
		write(cs.subSequence(start, end).toString());
		return this;
	}

	public Writer append(char c)
	{
		mBuffer.append(c);
		return this;
	}

	public void close()
	{
	}

	public void flush()
	{
	}

	public String toString()
	{
		return mBuffer.toString();
	}

	public volatile Appendable append(char x0)
		throws IOException
	{
		return append(x0);
	}

	public volatile Appendable append(CharSequence x0, int x1, int x2)
		throws IOException
	{
		return append(x0, x1, x2);
	}

	public volatile Appendable append(CharSequence x0)
		throws IOException
	{
		return append(x0);
	}
}
