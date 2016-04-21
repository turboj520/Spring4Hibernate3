// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SqlDateDeserializer.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import java.io.IOException;
import java.lang.reflect.Constructor;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractDeserializer, IOExceptionWrapper, AbstractHessianInput

public class SqlDateDeserializer extends AbstractDeserializer
{

	private Class _cl;
	private Constructor _constructor;

	public SqlDateDeserializer(Class cl)
	{
		try
		{
			_cl = cl;
			_constructor = cl.getConstructor(new Class[] {
				Long.TYPE
			});
		}
		catch (NoSuchMethodException e)
		{
			throw new HessianException(e);
		}
	}

	public Class getType()
	{
		return _cl;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		int ref = in.addRef(null);
		long initValue = 0x8000000000000000L;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if (key.equals("value"))
				initValue = in.readUTCDate();
			else
				in.readString();
		}
		in.readMapEnd();
		Object value = create(initValue);
		in.setRef(ref, value);
		return value;
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		String fieldNames[] = (String[])(String[])fields;
		int ref = in.addRef(null);
		long initValue = 0x8000000000000000L;
		for (int i = 0; i < fieldNames.length; i++)
		{
			String key = fieldNames[i];
			if (key.equals("value"))
				initValue = in.readUTCDate();
			else
				in.readObject();
		}

		Object value = create(initValue);
		in.setRef(ref, value);
		return value;
	}

	private Object create(long initValue)
		throws IOException
	{
		if (initValue == 0x8000000000000000L)
			throw new IOException((new StringBuilder()).append(_cl.getName()).append(" expects name.").toString());
		return _constructor.newInstance(new Object[] {
			new Long(initValue)
		});
		Exception e;
		e;
		throw new IOExceptionWrapper(e);
	}
}
