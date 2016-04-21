// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractHessianOutput.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			SerializerFactory

public abstract class AbstractHessianOutput
{

	protected SerializerFactory _serializerFactory;

	public AbstractHessianOutput()
	{
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		return _serializerFactory;
	}

	public final SerializerFactory findSerializerFactory()
	{
		SerializerFactory factory = _serializerFactory;
		if (factory == null)
			_serializerFactory = factory = new SerializerFactory();
		return factory;
	}

	public void init(OutputStream outputstream)
	{
	}

	public void call(String method, Object args[])
		throws IOException
	{
		int length = args == null ? 0 : args.length;
		startCall(method, length);
		for (int i = 0; i < length; i++)
			writeObject(args[i]);

		completeCall();
	}

	public abstract void startCall()
		throws IOException;

	public abstract void startCall(String s, int i)
		throws IOException;

	/**
	 * @deprecated Method writeHeader is deprecated
	 */

	public void writeHeader(String name)
		throws IOException
	{
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	public abstract void writeMethod(String s)
		throws IOException;

	public abstract void completeCall()
		throws IOException;

	public abstract void writeBoolean(boolean flag)
		throws IOException;

	public abstract void writeInt(int i)
		throws IOException;

	public abstract void writeLong(long l)
		throws IOException;

	public abstract void writeDouble(double d)
		throws IOException;

	public abstract void writeUTCDate(long l)
		throws IOException;

	public abstract void writeNull()
		throws IOException;

	public abstract void writeString(String s)
		throws IOException;

	public abstract void writeString(char ac[], int i, int j)
		throws IOException;

	public abstract void writeBytes(byte abyte0[])
		throws IOException;

	public abstract void writeBytes(byte abyte0[], int i, int j)
		throws IOException;

	public abstract void writeByteBufferStart()
		throws IOException;

	public abstract void writeByteBufferPart(byte abyte0[], int i, int j)
		throws IOException;

	public abstract void writeByteBufferEnd(byte abyte0[], int i, int j)
		throws IOException;

	protected abstract void writeRef(int i)
		throws IOException;

	public abstract boolean removeRef(Object obj)
		throws IOException;

	public abstract boolean replaceRef(Object obj, Object obj1)
		throws IOException;

	public abstract boolean addRef(Object obj)
		throws IOException;

	public void resetReferences()
	{
	}

	public abstract void writeObject(Object obj)
		throws IOException;

	public abstract boolean writeListBegin(int i, String s)
		throws IOException;

	public abstract void writeListEnd()
		throws IOException;

	public abstract void writeMapBegin(String s)
		throws IOException;

	public abstract void writeMapEnd()
		throws IOException;

	public int writeObjectBegin(String type)
		throws IOException
	{
		writeMapBegin(type);
		return -2;
	}

	public void writeClassFieldLength(int i)
		throws IOException
	{
	}

	public void writeObjectEnd()
		throws IOException
	{
	}

	public void writeReply(Object o)
		throws IOException
	{
		startReply();
		writeObject(o);
		completeReply();
	}

	public void startReply()
		throws IOException
	{
	}

	public void completeReply()
		throws IOException
	{
	}

	public void writeFault(String s, String s1, Object obj)
		throws IOException
	{
	}

	public void flush()
		throws IOException
	{
	}

	public void close()
		throws IOException
	{
	}
}
