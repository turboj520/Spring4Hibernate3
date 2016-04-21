// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			HessianProtocolException, Deserializer, AbstractHessianInput

public class AbstractDeserializer
	implements Deserializer
{
	static final class NullDeserializer extends AbstractDeserializer
	{

		NullDeserializer()
		{
		}
	}


	public static final NullDeserializer NULL = new NullDeserializer();

	public AbstractDeserializer()
	{
	}

	public Class getType()
	{
		return java/lang/Object;
	}

	public boolean isReadResolve()
	{
		return false;
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

	public Object[] createFields(int len)
	{
		return new String[len];
	}

	public Object createField(String name)
	{
		return name;
	}

	public Object readObject(AbstractHessianInput in, String fieldNames[])
		throws IOException
	{
		return readObject(in, (Object[])fieldNames);
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		throw new UnsupportedOperationException(toString());
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
