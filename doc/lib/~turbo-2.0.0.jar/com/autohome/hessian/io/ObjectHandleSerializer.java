// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ObjectHandleSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, AbstractHessianOutput, Serializer

public class ObjectHandleSerializer extends AbstractSerializer
{

	public static final Serializer SER = new ObjectHandleSerializer();

	public ObjectHandleSerializer()
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
			int ref = out.writeObjectBegin("object");
			if (ref < -1)
				out.writeMapEnd();
			else
			if (ref == -1)
			{
				out.writeInt(0);
				out.writeObjectBegin("object");
			}
		}
	}

}
