// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UnsafeByteArrayInputStream.java

package com.autohome.turbo.common.io;

import java.io.IOException;
import java.io.InputStream;

public class UnsafeByteArrayInputStream extends InputStream
{

	protected byte mData[];
	protected int mPosition;
	protected int mLimit;
	protected int mMark;

	public UnsafeByteArrayInputStream(byte buf[])
	{
		this(buf, 0, buf.length);
	}

	public UnsafeByteArrayInputStream(byte buf[], int offset)
	{
		this(buf, offset, buf.length - offset);
	}

	public UnsafeByteArrayInputStream(byte buf[], int offset, int length)
	{
		mMark = 0;
		mData = buf;
		mPosition = mMark = offset;
		mLimit = Math.min(offset + length, buf.length);
	}

	public int read()
	{
		return mPosition >= mLimit ? -1 : mData[mPosition++] & 0xff;
	}

	public int read(byte b[], int off, int len)
	{
		if (b == null)
			throw new NullPointerException();
		if (off < 0 || len < 0 || len > b.length - off)
			throw new IndexOutOfBoundsException();
		if (mPosition >= mLimit)
			return -1;
		if (mPosition + len > mLimit)
			len = mLimit - mPosition;
		if (len <= 0)
		{
			return 0;
		} else
		{
			System.arraycopy(mData, mPosition, b, off, len);
			mPosition += len;
			return len;
		}
	}

	public long skip(long len)
	{
		if ((long)mPosition + len > (long)mLimit)
			len = mLimit - mPosition;
		if (len <= 0L)
		{
			return 0L;
		} else
		{
			mPosition += len;
			return len;
		}
	}

	public int available()
	{
		return mLimit - mPosition;
	}

	public boolean markSupported()
	{
		return true;
	}

	public void mark(int readAheadLimit)
	{
		mMark = mPosition;
	}

	public void reset()
	{
		mPosition = mMark;
	}

	public void close()
		throws IOException
	{
	}

	public int position()
	{
		return mPosition;
	}

	public void position(int newPosition)
	{
		mPosition = newPosition;
	}

	public int size()
	{
		return mData != null ? mData.length : 0;
	}
}
