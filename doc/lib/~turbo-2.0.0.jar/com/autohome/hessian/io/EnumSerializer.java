// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EnumSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.lang.reflect.Method;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class EnumSerializer extends AbstractSerializer
{

	private Method _name;

	public EnumSerializer(Class cl)
	{
		if (!cl.isEnum() && cl.getSuperclass().isEnum())
			cl = cl.getSuperclass();
		try
		{
			_name = cl.getMethod("name", new Class[0]);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Class cl = obj.getClass();
		if (!cl.isEnum() && cl.getSuperclass().isEnum())
			cl = cl.getSuperclass();
		String name = null;
		try
		{
			name = (String)_name.invoke(obj, (Object[])null);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		int ref = out.writeObjectBegin(cl.getName());
		if (ref < -1)
		{
			out.writeString("name");
			out.writeString(name);
			out.writeMapEnd();
		} else
		{
			if (ref == -1)
			{
				out.writeClassFieldLength(1);
				out.writeString("name");
				out.writeObjectBegin(cl.getName());
			}
			out.writeString(name);
		}
	}
}
