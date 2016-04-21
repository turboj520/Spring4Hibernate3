// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StringValueDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractDeserializer, IOExceptionWrapper, AbstractHessianInput

public class StringValueDeserializer extends AbstractDeserializer
{

	private Class _cl;
	private Constructor _constructor;

	public StringValueDeserializer(Class cl)
	{
		try
		{
			_cl = cl;
			_constructor = cl.getConstructor(new Class[] {
				java/lang/String
			});
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public Class getType()
	{
		return _cl;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String value = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if (key.equals("value"))
				value = in.readString();
			else
				in.readObject();
		}
		in.readMapEnd();
		Object object = create(value);
		in.addRef(object);
		return object;
	}

	public Object readObject(AbstractHessianInput in, String fieldNames[])
		throws IOException
	{
		String value = null;
		for (int i = 0; i < fieldNames.length; i++)
			if ("value".equals(fieldNames[i]))
				value = in.readString();
			else
				in.readObject();

		Object object = create(value);
		in.addRef(object);
		return object;
	}

	private Object create(String value)
		throws IOException
	{
		if (value == null)
			throw new IOException((new StringBuilder()).append(_cl.getName()).append(" expects name.").toString());
		return _constructor.newInstance(new Object[] {
			value
		});
		Exception e;
		e;
		throw new IOExceptionWrapper(e);
	}
}
