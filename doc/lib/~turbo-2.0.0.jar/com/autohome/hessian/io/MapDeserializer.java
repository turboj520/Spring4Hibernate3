// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MapDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractMapDeserializer, IOExceptionWrapper, AbstractHessianInput

public class MapDeserializer extends AbstractMapDeserializer
{

	private Class _type;
	private Constructor _ctor;

	public MapDeserializer(Class type)
	{
		if (type == null)
			type = java/util/HashMap;
		_type = type;
		Constructor ctors[] = type.getConstructors();
		for (int i = 0; i < ctors.length; i++)
			if (ctors[i].getParameterTypes().length == 0)
				_ctor = ctors[i];

		if (_ctor == null)
			try
			{
				_ctor = java/util/HashMap.getConstructor(new Class[0]);
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
	}

	public Class getType()
	{
		if (_type != null)
			return _type;
		else
			return java/util/HashMap;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		Map map;
		if (_type == null)
			map = new HashMap();
		else
		if (_type.equals(java/util/Map))
			map = new HashMap();
		else
		if (_type.equals(java/util/SortedMap))
			map = new TreeMap();
		else
			try
			{
				map = (Map)_ctor.newInstance(new Object[0]);
			}
			catch (Exception e)
			{
				throw new IOExceptionWrapper(e);
			}
		in.addRef(map);
		for (; !in.isEnd(); map.put(in.readObject(), in.readObject()));
		in.readEnd();
		return map;
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		String fieldNames[] = (String[])(String[])fields;
		Map map = createMap();
		int ref = in.addRef(map);
		for (int i = 0; i < fieldNames.length; i++)
		{
			String name = fieldNames[i];
			map.put(name, in.readObject());
		}

		return map;
	}

	private Map createMap()
		throws IOException
	{
		if (_type == null)
			return new HashMap();
		if (_type.equals(java/util/Map))
			return new HashMap();
		if (_type.equals(java/util/SortedMap))
			return new TreeMap();
		return (Map)_ctor.newInstance(new Object[0]);
		Exception e;
		e;
		throw new IOExceptionWrapper(e);
	}
}
