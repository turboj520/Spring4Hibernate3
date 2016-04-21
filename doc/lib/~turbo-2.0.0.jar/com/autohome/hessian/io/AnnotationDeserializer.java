// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AnnotationDeserializer.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractMapDeserializer, AnnotationInvocationHandler, IOExceptionWrapper, AbstractHessianInput

public class AnnotationDeserializer extends AbstractMapDeserializer
{

	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/AnnotationDeserializer.getName());
	private Class _annType;

	public AnnotationDeserializer(Class annType)
	{
		_annType = annType;
	}

	public Class getType()
	{
		return _annType;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		HashMap valueMap;
		int ref = in.addRef(null);
		valueMap = new HashMap(8);
		String key;
		Object value;
		for (; !in.isEnd(); valueMap.put(key, value))
		{
			key = in.readString();
			value = in.readObject();
		}

		in.readMapEnd();
		return Proxy.newProxyInstance(_annType.getClassLoader(), new Class[] {
			_annType
		}, new AnnotationInvocationHandler(_annType, valueMap));
		IOException e;
		e;
		throw e;
		e;
		throw new IOExceptionWrapper(e);
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		String fieldNames[] = (String[])(String[])fields;
		HashMap valueMap;
		int ref = in.addRef(null);
		valueMap = new HashMap(8);
		for (int i = 0; i < fieldNames.length; i++)
		{
			String name = fieldNames[i];
			valueMap.put(name, in.readObject());
		}

		return Proxy.newProxyInstance(_annType.getClassLoader(), new Class[] {
			_annType
		}, new AnnotationInvocationHandler(_annType, valueMap));
		IOException e;
		e;
		throw e;
		e;
		throw new HessianException((new StringBuilder()).append(_annType.getName()).append(":").append(e).toString(), e);
	}

}
