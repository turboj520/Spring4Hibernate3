// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelBuffer.java

package com.autohome.turbo.remoting.buffer;

import java.io.*;
import java.nio.ByteBuffer;

// Referenced classes of package com.autohome.turbo.remoting.buffer:
//			ChannelBufferFactory

public interface ChannelBuffer
	extends Comparable
{

	public abstract int capacity();

	public abstract void clear();

	public abstract ChannelBuffer copy();

	public abstract ChannelBuffer copy(int i, int j);

	public abstract void discardReadBytes();

	public abstract void ensureWritableBytes(int i);

	public abstract boolean equals(Object obj);

	public abstract ChannelBufferFactory factory();

	public abstract byte getByte(int i);

	public abstract void getBytes(int i, byte abyte0[]);

	public abstract void getBytes(int i, byte abyte0[], int j, int k);

	public abstract void getBytes(int i, ByteBuffer bytebuffer);

	public abstract void getBytes(int i, ChannelBuffer channelbuffer);

	public abstract void getBytes(int i, ChannelBuffer channelbuffer, int j);

	public abstract void getBytes(int i, ChannelBuffer channelbuffer, int j, int k);

	public abstract void getBytes(int i, OutputStream outputstream, int j)
		throws IOException;

	public abstract boolean isDirect();

	public abstract void markReaderIndex();

	public abstract void markWriterIndex();

	public abstract boolean readable();

	public abstract int readableBytes();

	public abstract byte readByte();

	public abstract void readBytes(byte abyte0[]);

	public abstract void readBytes(byte abyte0[], int i, int j);

	public abstract void readBytes(ByteBuffer bytebuffer);

	public abstract void readBytes(ChannelBuffer channelbuffer);

	public abstract void readBytes(ChannelBuffer channelbuffer, int i);

	public abstract void readBytes(ChannelBuffer channelbuffer, int i, int j);

	public abstract ChannelBuffer readBytes(int i);

	public abstract void resetReaderIndex();

	public abstract void resetWriterIndex();

	public abstract int readerIndex();

	public abstract void readerIndex(int i);

	public abstract void readBytes(OutputStream outputstream, int i)
		throws IOException;

	public abstract void setByte(int i, int j);

	public abstract void setBytes(int i, byte abyte0[]);

	public abstract void setBytes(int i, byte abyte0[], int j, int k);

	public abstract void setBytes(int i, ByteBuffer bytebuffer);

	public abstract void setBytes(int i, ChannelBuffer channelbuffer);

	public abstract void setBytes(int i, ChannelBuffer channelbuffer, int j);

	public abstract void setBytes(int i, ChannelBuffer channelbuffer, int j, int k);

	public abstract int setBytes(int i, InputStream inputstream, int j)
		throws IOException;

	public abstract void setIndex(int i, int j);

	public abstract void skipBytes(int i);

	public abstract ByteBuffer toByteBuffer();

	public abstract ByteBuffer toByteBuffer(int i, int j);

	public abstract boolean writable();

	public abstract int writableBytes();

	public abstract void writeByte(int i);

	public abstract void writeBytes(byte abyte0[]);

	public abstract void writeBytes(byte abyte0[], int i, int j);

	public abstract void writeBytes(ByteBuffer bytebuffer);

	public abstract void writeBytes(ChannelBuffer channelbuffer);

	public abstract void writeBytes(ChannelBuffer channelbuffer, int i);

	public abstract void writeBytes(ChannelBuffer channelbuffer, int i, int j);

	public abstract int writeBytes(InputStream inputstream, int i)
		throws IOException;

	public abstract int writerIndex();

	public abstract void writerIndex(int i);

	public abstract byte[] array();

	public abstract boolean hasArray();

	public abstract int arrayOffset();
}
