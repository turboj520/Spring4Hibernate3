// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ValueDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public abstract class ValueDeserializer extends AbstractDeserializer
{

	public ValueDeserializer()
	{
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String initValue = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if (key.equals("value"))
				initValue = in.readString();
			else
				in.readObject();
		}
		in.readMapEnd();
		return create(initValue);
	}

	public Object readObject(AbstractHessianInput in, String fieldNames[])
		throws IOException
	{
		String initValue = null;
		for (int i = 0; i < fieldNames.length; i++)
			if ("value".equals(fieldNames[i]))
				initValue = in.readString();
			else
				in.readObject();

		return create(initValue);
	}

	abstract Object create(String s)
		throws IOException;
}
