// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SqlDateSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Date;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class SqlDateSerializer extends AbstractSerializer
{

	public SqlDateSerializer()
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
			Class cl = obj.getClass();
			if (out.addRef(obj))
				return;
			int ref = out.writeObjectBegin(cl.getName());
			if (ref < -1)
			{
				out.writeString("value");
				out.writeUTCDate(((Date)obj).getTime());
				out.writeMapEnd();
			} else
			{
				if (ref == -1)
				{
					out.writeInt(1);
					out.writeString("value");
					out.writeObjectBegin(cl.getName());
				}
				out.writeUTCDate(((Date)obj).getTime());
			}
		}
	}
}
