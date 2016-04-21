// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClassDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.util.HashMap;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractMapDeserializer, IOExceptionWrapper, AbstractHessianInput

public class ClassDeserializer extends AbstractMapDeserializer
{

	private static final HashMap _primClasses;
	private ClassLoader _loader;

	public ClassDeserializer(ClassLoader loader)
	{
		_loader = loader;
	}

	public Class getType()
	{
		return java/lang/Class;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		int ref = in.addRef(null);
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
		Object value = create(name);
		in.setRef(ref, value);
		return value;
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		String fieldNames[] = (String[])(String[])fields;
		int ref = in.addRef(null);
		String name = null;
		for (int i = 0; i < fieldNames.length; i++)
			if ("name".equals(fieldNames[i]))
				name = in.readString();
			else
				in.readObject();

		Object value = create(name);
		in.setRef(ref, value);
		return value;
	}

	Object create(String name)
		throws IOException
	{
		if (name == null)
			throw new IOException("Serialized Class expects name.");
		Class cl = (Class)_primClasses.get(name);
		if (cl != null)
			return cl;
		if (_loader != null)
			return Class.forName(name, false, _loader);
		return Class.forName(name);
		Exception e;
		e;
		throw new IOExceptionWrapper(e);
	}

	static 
	{
		_primClasses = new HashMap();
		_primClasses.put("void", Void.TYPE);
		_primClasses.put("boolean", Boolean.TYPE);
		_primClasses.put("java.lang.Boolean", java/lang/Boolean);
		_primClasses.put("byte", Byte.TYPE);
		_primClasses.put("java.lang.Byte", java/lang/Byte);
		_primClasses.put("char", Character.TYPE);
		_primClasses.put("java.lang.Character", java/lang/Character);
		_primClasses.put("short", Short.TYPE);
		_primClasses.put("java.lang.Short", java/lang/Short);
		_primClasses.put("int", Integer.TYPE);
		_primClasses.put("java.lang.Integer", java/lang/Integer);
		_primClasses.put("long", Long.TYPE);
		_primClasses.put("java.lang.Long", java/lang/Long);
		_primClasses.put("float", Float.TYPE);
		_primClasses.put("java.lang.Float", java/lang/Float);
		_primClasses.put("double", Double.TYPE);
		_primClasses.put("java.lang.Double", java/lang/Double);
		_primClasses.put("java.lang.String", java/lang/String);
	}
}
