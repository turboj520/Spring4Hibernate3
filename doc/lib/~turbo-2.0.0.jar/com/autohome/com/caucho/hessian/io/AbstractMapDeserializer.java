// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractMapDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.HashMap;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public class AbstractMapDeserializer extends AbstractDeserializer
{

	public AbstractMapDeserializer()
	{
	}

	public Class getType()
	{
		return java/util/HashMap;
	}

	public Object readObject(AbstractHessianInput in)
		throws IOException
	{
		Object obj = in.readObject();
		if (obj != null)
			throw error((new StringBuilder()).append("expected map/object at ").append(obj.getClass().getName()).append(" (").append(obj).append(")").toString());
		else
			throw error("expected map/object at null");
	}
}
