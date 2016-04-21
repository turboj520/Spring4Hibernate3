// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClassSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class ClassSerializer extends AbstractSerializer
{

	public ClassSerializer()
	{
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		Class cl = (Class)obj;
		if (cl == null)
		{
			out.writeNull();
		} else
		{
			if (out.addRef(obj))
				return;
			int ref = out.writeObjectBegin("java.lang.Class");
			if (ref < -1)
			{
				out.writeString("name");
				out.writeString(cl.getName());
				out.writeMapEnd();
			} else
			{
				if (ref == -1)
				{
					out.writeInt(1);
					out.writeString("name");
					out.writeObjectBegin("java.lang.Class");
				}
				out.writeString(cl.getName());
			}
		}
	}
}
