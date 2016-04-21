// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StringValueDeserializer.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import java.io.IOException;
import java.lang.reflect.Constructor;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractStringValueDeserializer

public class StringValueDeserializer extends AbstractStringValueDeserializer
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

	protected Object create(String value)
		throws IOException
	{
		if (value == null)
			throw new IOException((new StringBuilder()).append(_cl.getName()).append(" expects name.").toString());
		return _constructor.newInstance(new Object[] {
			value
		});
		Exception e;
		e;
		throw new HessianException((new StringBuilder()).append(_cl.getName()).append(": value=").append(value).append("\n").append(e).toString(), e);
	}
}
