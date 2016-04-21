// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			HessianProtocolException, Deserializer, AbstractHessianInput

public abstract class AbstractDeserializer
	implements Deserializer
{

	public AbstractDeserializer()
	{
	}

	public Class getType()
	{
		return java/lang/Object;
	}

	public Object readObject(AbstractHessianInput in)
		throws IOException
	{
		Object obj = in.readObject();
		String className = getClass().getName();
		if (obj != null)
			throw error((new StringBuilder()).append(className).append(": unexpected object ").append(obj.getClass().getName()).append(" (").append(obj).append(")").toString());
		else
			throw error((new StringBuilder()).append(className).append(": unexpected null value").toString());
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

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		Object obj = in.readObject();
		String className = getClass().getName();
		if (obj != null)
			throw error((new StringBuilder()).append(className).append(": unexpected object ").append(obj.getClass().getName()).append(" (").append(obj).append(")").toString());
		else
			throw error((new StringBuilder()).append(className).append(": unexpected null value").toString());
	}

	public Object readObject(AbstractHessianInput in, String fieldNames[])
		throws IOException
	{
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	protected HessianProtocolException error(String msg)
	{
		return new HessianProtocolException(msg);
	}

	protected String codeName(int ch)
	{
		if (ch < 0)
			return "end of file";
		else
			return (new StringBuilder()).append("0x").append(Integer.toHexString(ch & 0xff)).toString();
	}
}
