// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StreamUtils.java

package com.autohome.turbo.common.io;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtils
{

	private StreamUtils()
	{
	}

	public static InputStream limitedInputStream(InputStream is, int limit)
		throws IOException
	{
		return new InputStream(limit, is) {

			private int mPosition;
			private int mMark;
			private int mLimit;
			final int val$limit;
			final InputStream val$is;

			public int read()
				throws IOException
			{
				if (mPosition < mLimit)
				{
					mPosition++;
					return is.read();
				} else
				{
					return -1;
				}
			}

			public int read(byte b[], int off, int len)
				throws IOException
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
					is.read(b, off, len);
					mPosition += len;
					return len;
				}
			}

			public long skip(long len)
				throws IOException
			{
				if ((long)mPosition + len > (long)mLimit)
					len = mLimit - mPosition;
				if (len <= 0L)
				{
					return 0L;
				} else
				{
					is.skip(len);
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
				return is.markSupported();
			}

			public void mark(int readlimit)
			{
				is.mark(readlimit);
				mMark = mPosition;
			}

			public void reset()
				throws IOException
			{
				is.reset();
				mPosition = mMark;
			}

			public void close()
				throws IOException
			{
			}

			
				throws IOException
			{
				limit = i;
				is = inputstream;
				super();
				mPosition = 0;
				mMark = 0;
				mLimit = Math.min(limit, is.available());
			}
		};
	}

	public static InputStream markSupportedInputStream(InputStream is, int markBufferSize)
	{
		if (is.markSupported())
			return is;
		else
			return new InputStream(is, markBufferSize) {

				byte mMarkBuffer[];
				boolean mInMarked;
				boolean mInReset;
				private int mPosition;
				private int mCount;
				boolean mDry;
				final InputStream val$is;
				final int val$markBufferSize;

				public int read()
					throws IOException
				{
					if (!mInMarked)
						return is.read();
					if (mPosition < mCount)
					{
						byte b = mMarkBuffer[mPosition++];
						return b & 0xff;
					}
					if (!mInReset)
					{
						if (mDry)
							return -1;
						if (null == mMarkBuffer)
							mMarkBuffer = new byte[markBufferSize];
						if (mPosition >= markBufferSize)
							throw new IOException("Mark buffer is full!");
						int read = is.read();
						if (-1 == read)
						{
							mDry = true;
							return -1;
						} else
						{
							mMarkBuffer[mPosition++] = (byte)read;
							mCount++;
							return read;
						}
					} else
					{
						mInMarked = false;
						mInReset = false;
						mPosition = 0;
						mCount = 0;
						return is.read();
					}
				}

				public synchronized void mark(int readlimit)
				{
					mInMarked = true;
					mInReset = false;
					int count = mCount - mPosition;
					if (count > 0)
					{
						System.arraycopy(mMarkBuffer, mPosition, mMarkBuffer, 0, count);
						mCount = count;
						mPosition = 0;
					}
				}

				public synchronized void reset()
					throws IOException
				{
					if (!mInMarked)
					{
						throw new IOException("should mark befor reset!");
					} else
					{
						mInReset = true;
						mPosition = 0;
						return;
					}
				}

				public boolean markSupported()
				{
					return true;
				}

				public int available()
					throws IOException
				{
					int available = is.available();
					if (mInMarked && mInReset)
						available += mCount - mPosition;
					return available;
				}

			
			{
				is = inputstream;
				markBufferSize = i;
				super();
				mInMarked = false;
				mInReset = false;
				mPosition = 0;
				mCount = 0;
				mDry = false;
			}
			};
	}

	public static InputStream markSupportedInputStream(InputStream is)
	{
		return markSupportedInputStream(is, 1024);
	}

	public static void skipUnusedStream(InputStream is)
		throws IOException
	{
		if (is.available() > 0)
			is.skip(is.available());
	}
}
