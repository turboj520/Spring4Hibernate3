// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ObjectDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public class ObjectDeserializer extends AbstractDeserializer
{

	private Class _cl;

	public ObjectDeserializer(Class cl)
	{
		_cl = cl;
	}

	public Class getType()
	{
		return _cl;
	}

	public Object readObject(AbstractHessianInput in)
		throws IOException
	{
		return in.readObject();
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	public Object readList(AbstractHessianInput in, int length)
		throws IOException
	{
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	public Object readLengthList(AbstractHessianInput in, int length)
		throws IOException
	{
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getSimpleName()).append("[").append(_cl).append("]").toString();
	}
}
