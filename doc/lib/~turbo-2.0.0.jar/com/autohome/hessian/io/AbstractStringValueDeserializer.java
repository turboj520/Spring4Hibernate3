// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractStringValueDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public abstract class AbstractStringValueDeserializer extends AbstractDeserializer
{

	public AbstractStringValueDeserializer()
	{
	}

	protected abstract Object create(String s)
		throws IOException;

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

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		String fieldNames[] = (String[])(String[])fields;
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
}
