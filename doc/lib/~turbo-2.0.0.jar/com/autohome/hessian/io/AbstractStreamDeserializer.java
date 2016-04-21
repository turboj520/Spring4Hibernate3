// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractStreamDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public abstract class AbstractStreamDeserializer extends AbstractDeserializer
{

	public AbstractStreamDeserializer()
	{
	}

	public abstract Class getType();

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		Object value = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if (key.equals("value"))
				value = readStreamValue(in);
			else
				in.readObject();
		}
		in.readMapEnd();
		return value;
	}

	public Object readObject(AbstractHessianInput in, Object fields[])
		throws IOException
	{
		String fieldNames[] = (String[])(String[])fields;
		Object value = null;
		for (int i = 0; i < fieldNames.length; i++)
			if ("value".equals(fieldNames[i]))
				value = readStreamValue(in);
			else
				in.readObject();

		return value;
	}

	protected abstract Object readStreamValue(AbstractHessianInput abstracthessianinput)
		throws IOException;
}
