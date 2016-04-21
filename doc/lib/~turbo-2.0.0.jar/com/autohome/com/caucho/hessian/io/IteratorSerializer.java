// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IteratorSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Iterator;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class IteratorSerializer extends AbstractSerializer
{

	private static IteratorSerializer _serializer;

	public IteratorSerializer()
	{
	}

	public static IteratorSerializer create()
	{
		if (_serializer == null)
			_serializer = new IteratorSerializer();
		return _serializer;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		Iterator iter = (Iterator)obj;
		boolean hasEnd = out.writeListBegin(-1, null);
		Object value;
		for (; iter.hasNext(); out.writeObject(value))
			value = iter.next();

		if (hasEnd)
			out.writeListEnd();
	}
}
