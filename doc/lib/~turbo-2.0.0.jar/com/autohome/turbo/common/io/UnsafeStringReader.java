// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UnsafeStringReader.java

package com.autohome.turbo.common.io;

import java.io.IOException;
import java.io.Reader;

public class UnsafeStringReader extends Reader
{

	private String mString;
	private int mPosition;
	private int mLimit;
	private int mMark;

	public UnsafeStringReader(String str)
	{
		mString = str;
		mLimit = str.length();
		mPosition = mMark = 0;
	}

	public int read()
		throws IOException
	{
		ensureOpen();
		if (mPosition >= mLimit)
			return -1;
		else
			return mString.charAt(mPosition++);
	}

	public int read(char cs[], int off, int len)
		throws IOException
	{
		ensureOpen();
		if (off < 0 || off > cs.length || len < 0 || off + len > cs.length || off + len < 0)
			throw new IndexOutOfBoundsException();
		if (len == 0)
			return 0;
		if (mPosition >= mLimit)
		{
			return -1;
		} else
		{
			int n = Math.min(mLimit - mPosition, len);
			mString.getChars(mPosition, mPosition + n, cs, off);
			mPosition += n;
			return n;
		}
	}

	public long skip(long ns)
		throws IOException
	{
		ensureOpen();
		if (mPosition >= mLimit)
		{
			return 0L;
		} else
		{
			long n = Math.min(mLimit - mPosition, ns);
			n = Math.max(-mPosition, n);
			mPosition += n;
			return n;
		}
	}

	public boolean ready()
		throws IOException
	{
		ensureOpen();
		return true;
	}

	public boolean markSupported()
	{
		return true;
	}

	public void mark(int readAheadLimit)
		throws IOException
	{
		if (readAheadLimit < 0)
		{
			throw new IllegalArgumentException("Read-ahead limit < 0");
		} else
		{
			ensureOpen();
			mMark = mPosition;
			return;
		}
	}

	public void reset()
		throws IOException
	{
		ensureOpen();
		mPosition = mMark;
	}

	public void close()
		throws IOException
	{
		mString = null;
	}

	private void ensureOpen()
		throws IOException
	{
		if (mString == null)
			throw new IOException("Stream closed");
		else
			return;
	}
}
