// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianSerializerOutput.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;

// Referenced classes of package com.autohome.hessian.io:
//			Hessian2Output, IOExceptionWrapper

public class HessianSerializerOutput extends Hessian2Output
{

	public HessianSerializerOutput(OutputStream os)
	{
		super(os);
	}

	public HessianSerializerOutput()
	{
		super(null);
	}

	public void writeObjectImpl(Object obj)
		throws IOException
	{
		Class cl = obj.getClass();
		try
		{
			Method method = cl.getMethod("writeReplace", new Class[0]);
			Object repl = method.invoke(obj, new Object[0]);
			writeObject(repl);
			return;
		}
		catch (Exception e) { }
		try
		{
			writeMapBegin(cl.getName());
			for (; cl != null; cl = cl.getSuperclass())
			{
				Field fields[] = cl.getDeclaredFields();
				for (int i = 0; i < fields.length; i++)
				{
					Field field = fields[i];
					if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
					{
						field.setAccessible(true);
						writeString(field.getName());
						writeObject(field.get(obj));
					}
				}

			}

			writeMapEnd();
		}
		catch (IllegalAccessException e)
		{
			throw new IOExceptionWrapper(e);
		}
	}
}
