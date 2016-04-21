// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractChannelBuffer.java

package com.autohome.turbo.remoting.buffer;

import java.io.*;
import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBuffer, ChannelBuffers, ChannelBufferFactory

public abstract class AbstractChannelBuffer
	implements ChannelBuffer
{

	private int readerIndex;
	private int writerIndex;
	private int markedReaderIndex;
	private int markedWriterIndex;

	public AbstractChannelBuffer()
	{
	}

	public int readerIndex()
	{
		return readerIndex;
	}

	public void readerIndex(int readerIndex)
	{
		if (readerIndex < 0 || readerIndex > writerIndex)
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			this.readerIndex = readerIndex;
			return;
		}
	}

	public int writerIndex()
	{
		return writerIndex;
	}

	public void writerIndex(int writerIndex)
	{
		if (writerIndex < readerIndex || writerIndex > capacity())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			this.writerIndex = writerIndex;
			return;
		}
	}

	public void setIndex(int readerIndex, int writerIndex)
	{
		if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			this.readerIndex = readerIndex;
			this.writerIndex = writerIndex;
			return;
		}
	}

	public void clear()
	{
		readerIndex = writerIndex = 0;
	}

	public boolean readable()
	{
		return readableBytes() > 0;
	}

	public boolean writable()
	{
		return writableBytes() > 0;
	}

	public int readableBytes()
	{
		return writerIndex - readerIndex;
	}

	public int writableBytes()
	{
		return capacity() - writerIndex;
	}

	public void markReaderIndex()
	{
		markedReaderIndex = readerIndex;
	}

	public void resetReaderIndex()
	{
		readerIndex(markedReaderIndex);
	}

	public void markWriterIndex()
	{
		markedWriterIndex = writerIndex;
	}

	public void resetWriterIndex()
	{
		writerIndex = markedWriterIndex;
	}

	public void discardReadBytes()
	{
		if (readerIndex == 0)
		{
			return;
		} else
		{
			setBytes(0, this, readerIndex, writerIndex - readerIndex);
			writerIndex -= readerIndex;
			markedReaderIndex = Math.max(markedReaderIndex - readerIndex, 0);
			markedWriterIndex = Math.max(markedWriterIndex - readerIndex, 0);
			readerIndex = 0;
			return;
		}
	}

	public void ensureWritableBytes(int writableBytes)
	{
		if (writableBytes > writableBytes())
			throw new IndexOutOfBoundsException();
		else
			return;
	}

	public void getBytes(int index, byte dst[])
	{
		getBytes(index, dst, 0, dst.length);
	}

	public void getBytes(int index, ChannelBuffer dst)
	{
		getBytes(index, dst, dst.writableBytes());
	}

	public void getBytes(int index, ChannelBuffer dst, int length)
	{
		if (length > dst.writableBytes())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			getBytes(index, dst, dst.writerIndex(), length);
			dst.writerIndex(dst.writerIndex() + length);
			return;
		}
	}

	public void setBytes(int index, byte src[])
	{
		setBytes(index, src, 0, src.length);
	}

	public void setBytes(int index, ChannelBuffer src)
	{
		setBytes(index, src, src.readableBytes());
	}

	public void setBytes(int index, ChannelBuffer src, int length)
	{
		if (length > src.readableBytes())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			setBytes(index, src, src.readerIndex(), length);
			src.readerIndex(src.readerIndex() + length);
			return;
		}
	}

	public byte readByte()
	{
		if (readerIndex == writerIndex)
			throw new IndexOutOfBoundsException();
		else
			return getByte(readerIndex++);
	}

	public ChannelBuffer readBytes(int length)
	{
		checkReadableBytes(length);
		if (length == 0)
		{
			return ChannelBuffers.EMPTY_BUFFER;
		} else
		{
			ChannelBuffer buf = factory().getBuffer(length);
			buf.writeBytes(this, readerIndex, length);
			readerIndex += length;
			return buf;
		}
	}

	public void readBytes(byte dst[], int dstIndex, int length)
	{
		checkReadableBytes(length);
		getBytes(readerIndex, dst, dstIndex, length);
		readerIndex += length;
	}

	public void readBytes(byte dst[])
	{
		readBytes(dst, 0, dst.length);
	}

	public void readBytes(ChannelBuffer dst)
	{
		readBytes(dst, dst.writableBytes());
	}

	public void readBytes(ChannelBuffer dst, int length)
	{
		if (length > dst.writableBytes())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			readBytes(dst, dst.writerIndex(), length);
			dst.writerIndex(dst.writerIndex() + length);
			return;
		}
	}

	public void readBytes(ChannelBuffer dst, int dstIndex, int length)
	{
		checkReadableBytes(length);
		getBytes(readerIndex, dst, dstIndex, length);
		readerIndex += length;
	}

	public void readBytes(ByteBuffer dst)
	{
		int length = dst.remaining();
		checkReadableBytes(length);
		getBytes(readerIndex, dst);
		readerIndex += length;
	}

	public void readBytes(OutputStream out, int length)
		throws IOException
	{
		checkReadableBytes(length);
		getBytes(readerIndex, out, length);
		readerIndex += length;
	}

	public void skipBytes(int length)
	{
		int newReaderIndex = readerIndex + length;
		if (newReaderIndex > writerIndex)
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			readerIndex = newReaderIndex;
			return;
		}
	}

	public void writeByte(int value)
	{
		setByte(writerIndex++, value);
	}

	public void writeBytes(byte src[], int srcIndex, int length)
	{
		setBytes(writerIndex, src, srcIndex, length);
		writerIndex += length;
	}

	public void writeBytes(byte src[])
	{
		writeBytes(src, 0, src.length);
	}

	public void writeBytes(ChannelBuffer src)
	{
		writeBytes(src, src.readableBytes());
	}

	public void writeBytes(ChannelBuffer src, int length)
	{
		if (length > src.readableBytes())
		{
			throw new IndexOutOfBoundsException();
		} else
		{
			writeBytes(src, src.readerIndex(), length);
			src.readerIndex(src.readerIndex() + length);
			return;
		}
	}

	public void writeBytes(ChannelBuffer src, int srcIndex, int length)
	{
		setBytes(writerIndex, src, srcIndex, length);
		writerIndex += length;
	}

	public void writeBytes(ByteBuffer src)
	{
		int length = src.remaining();
		setBytes(writerIndex, src);
		writerIndex += length;
	}

	public int writeBytes(InputStream in, int length)
		throws IOException
	{
		int writtenBytes = setBytes(writerIndex, in, length);
		if (writtenBytes > 0)
			writerIndex += writtenBytes;
		return writtenBytes;
	}

	public ChannelBuffer copy()
	{
		return copy(readerIndex, readableBytes());
	}

	public ByteBuffer toByteBuffer()
	{
		return toByteBuffer(readerIndex, readableBytes());
	}

	public boolean equals(Object o)
	{
		return (o instanceof ChannelBuffer) && ChannelBuffers.equals(this, (ChannelBuffer)o);
	}

	public int compareTo(ChannelBuffer that)
	{
		return ChannelBuffers.compare(this, that);
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getSimpleName()).append('(').append("ridx=").append(readerIndex).append(", ").append("widx=").append(writerIndex).append(", ").append("cap=").append(capacity()).append(')').toString();
	}

	protected void checkReadableBytes(int minimumReadableBytes)
	{
		if (readableBytes() < minimumReadableBytes)
			throw new IndexOutOfBoundsException();
		else
			return;
	}

	public volatile int compareTo(Object x0)
	{
		return compareTo((ChannelBuffer)x0);
	}
}
