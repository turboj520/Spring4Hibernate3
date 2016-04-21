// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianSerializerInput.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;

// Referenced classes of package com.autohome.hessian.io:
//			Hessian2Input, IOExceptionWrapper

public class HessianSerializerInput extends Hessian2Input
{

	public HessianSerializerInput(InputStream is)
	{
		super(is);
	}

	public HessianSerializerInput()
	{
		super(null);
	}

	protected Object readObjectImpl(Class cl)
		throws IOException
	{
		Object obj;
		obj = cl.newInstance();
		if (_refs == null)
			_refs = new ArrayList();
		_refs.add(obj);
		HashMap fieldMap = getFieldMap(cl);
		int code;
		for (code = read(); code >= 0 && code != 122; code = read())
		{
			unread();
			Object key = readObject();
			Field field = (Field)fieldMap.get(key);
			Object value;
			if (field != null)
			{
				value = readObject(field.getType());
				field.set(obj, value);
			} else
			{
				value = readObject();
			}
		}

		if (code != 122)
			throw expect("map", code);
		Method method = cl.getMethod("readResolve", new Class[0]);
		return method.invoke(obj, new Object[0]);
		Exception e;
		e;
		return obj;
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper(e);
	}

	protected HashMap getFieldMap(Class cl)
	{
		HashMap fieldMap = new HashMap();
		for (; cl != null; cl = cl.getSuperclass())
		{
			Field fields[] = cl.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
				{
					field.setAccessible(true);
					fieldMap.put(field.getName(), field);
				}
			}

		}

		return fieldMap;
	}
}
