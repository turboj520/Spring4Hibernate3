// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EnumDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Method;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractDeserializer, IOExceptionWrapper, AbstractHessianInput

public class EnumDeserializer extends AbstractDeserializer
{

	private Class _enumType;
	private Method _valueOf;

	public EnumDeserializer(Class cl)
	{
		if (cl.isEnum())
			_enumType = cl;
		else
		if (cl.getSuperclass().isEnum())
			_enumType = cl.getSuperclass();
		else
			throw new RuntimeException((new StringBuilder()).append("Class ").append(cl.getName()).append(" is not an enum").toString());
		try
		{
			_valueOf = _enumType.getMethod("valueOf", new Class[] {
				java/lang/Class, java/lang/String
			});
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public Class getType()
	{
		return _enumType;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String name = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if (key.equals("name"))
				name = in.readString();
			else
				in.readObject();
		}
		in.readMapEnd();
		Object obj = create(name);
		in.addRef(obj);
		return obj;
	}

	public Object readObject(AbstractHessianInput in, String fieldNames[])
		throws IOException
	{
		String name = null;
		for (int i = 0; i < fieldNames.length; i++)
			if ("name".equals(fieldNames[i]))
				name = in.readString();
			else
				in.readObject();

		Object obj = create(name);
		in.addRef(obj);
		return obj;
	}

	private Object create(String name)
		throws IOException
	{
		if (name == null)
			throw new IOException((new StringBuilder()).append(_enumType.getName()).append(" expects name.").toString());
		return _valueOf.invoke(null, new Object[] {
			_enumType, name
		});
		Exception e;
		e;
		throw new IOExceptionWrapper(e);
	}
}
