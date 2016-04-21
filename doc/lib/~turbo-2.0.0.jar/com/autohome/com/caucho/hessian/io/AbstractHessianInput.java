// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractHessianInput.java

package com.autohome.com.caucho.hessian.io;

import java.io.*;
import org.w3c.dom.Node;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			HessianRemoteResolver, SerializerFactory

public abstract class AbstractHessianInput
{

	private HessianRemoteResolver resolver;

	public AbstractHessianInput()
	{
	}

	public void init(InputStream inputstream)
	{
	}

	public abstract String getMethod();

	public void setRemoteResolver(HessianRemoteResolver resolver)
	{
		this.resolver = resolver;
	}

	public HessianRemoteResolver getRemoteResolver()
	{
		return resolver;
	}

	public void setSerializerFactory(SerializerFactory serializerfactory)
	{
	}

	public abstract int readCall()
		throws IOException;

	public void skipOptionalCall()
		throws IOException
	{
	}

	public abstract String readHeader()
		throws IOException;

	public abstract String readMethod()
		throws IOException;

	public int readMethodArgLength()
		throws IOException
	{
		return -1;
	}

	public abstract void startCall()
		throws IOException;

	public abstract void completeCall()
		throws IOException;

	public abstract Object readReply(Class class1)
		throws Throwable;

	public abstract void startReply()
		throws Throwable;

	public abstract void completeReply()
		throws IOException;

	public abstract boolean readBoolean()
		throws IOException;

	public abstract void readNull()
		throws IOException;

	public abstract int readInt()
		throws IOException;

	public abstract long readLong()
		throws IOException;

	public abstract double readDouble()
		throws IOException;

	public abstract long readUTCDate()
		throws IOException;

	public abstract String readString()
		throws IOException;

	public Node readNode()
		throws IOException
	{
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	public abstract Reader getReader()
		throws IOException;

	public abstract InputStream readInputStream()
		throws IOException;

	public abstract byte[] readBytes()
		throws IOException;

	public abstract Object readObject(Class class1)
		throws IOException;

	public abstract Object readObject()
		throws IOException;

	public abstract Object readRemote()
		throws IOException;

	public abstract Object readRef()
		throws IOException;

	public abstract int addRef(Object obj)
		throws IOException;

	public abstract void setRef(int i, Object obj)
		throws IOException;

	public void resetReferences()
	{
	}

	public abstract int readListStart()
		throws IOException;

	public abstract int readLength()
		throws IOException;

	public abstract int readMapStart()
		throws IOException;

	public abstract String readType()
		throws IOException;

	public abstract boolean isEnd()
		throws IOException;

	public abstract void readEnd()
		throws IOException;

	public abstract void readMapEnd()
		throws IOException;

	public abstract void readListEnd()
		throws IOException;

	public void close()
		throws IOException
	{
	}
}
