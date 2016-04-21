// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EnumerationDeserializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.util.Vector;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractListDeserializer, AbstractHessianInput

public class EnumerationDeserializer extends AbstractListDeserializer
{

	private static EnumerationDeserializer _deserializer;

	public EnumerationDeserializer()
	{
	}

	public static EnumerationDeserializer create()
	{
		if (_deserializer == null)
			_deserializer = new EnumerationDeserializer();
		return _deserializer;
	}

	public Object readList(AbstractHessianInput in, int length)
		throws IOException
	{
		Vector list = new Vector();
		in.addRef(list);
		for (; !in.isEnd(); list.add(in.readObject()));
		in.readEnd();
		return list.elements();
	}
}
