// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StringValueSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class StringValueSerializer extends AbstractSerializer
{

	public StringValueSerializer()
	{
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (obj == null)
		{
			out.writeNull();
		} else
		{
			if (out.addRef(obj))
				return;
			Class cl = obj.getClass();
			int ref = out.writeObjectBegin(cl.getName());
			if (ref < -1)
			{
				out.writeString("value");
				out.writeString(obj.toString());
				out.writeMapEnd();
			} else
			{
				if (ref == -1)
				{
					out.writeInt(1);
					out.writeString("value");
					out.writeObjectBegin(cl.getName());
				}
				out.writeString(obj.toString());
			}
		}
	}
}
