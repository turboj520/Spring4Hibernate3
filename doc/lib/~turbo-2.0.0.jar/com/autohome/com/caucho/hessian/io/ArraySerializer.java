// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ArraySerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class ArraySerializer extends AbstractSerializer
{

	public ArraySerializer()
	{
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (out.addRef(obj))
			return;
		Object array[] = (Object[])(Object[])obj;
		boolean hasEnd = out.writeListBegin(array.length, getArrayType(obj.getClass()));
		for (int i = 0; i < array.length; i++)
			out.writeObject(array[i]);

		if (hasEnd)
			out.writeListEnd();
	}

	private String getArrayType(Class cl)
	{
		if (cl.isArray())
			return (new StringBuilder()).append('[').append(getArrayType(cl.getComponentType())).toString();
		String name = cl.getName();
		if (name.equals("java.lang.String"))
			return "string";
		if (name.equals("java.lang.Object"))
			return "object";
		if (name.equals("java.util.Date"))
			return "date";
		else
			return name;
	}
}
