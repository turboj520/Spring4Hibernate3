// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EnumerationSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.util.Enumeration;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class EnumerationSerializer extends AbstractSerializer
{

	private static EnumerationSerializer _serializer;

	public EnumerationSerializer()
	{
	}

	public static EnumerationSerializer create()
	{
		if (_serializer == null)
			_serializer = new EnumerationSerializer();
		return _serializer;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		Enumeration iter = (Enumeration)obj;
		boolean hasEnd = out.writeListBegin(-1, null);
		Object value;
		for (; iter.hasMoreElements(); out.writeObject(value))
			value = iter.nextElement();

		if (hasEnd)
			out.writeListEnd();
	}
}
