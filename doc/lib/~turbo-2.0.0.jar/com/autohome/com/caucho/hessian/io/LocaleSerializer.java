// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LocaleSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Locale;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, LocaleHandle, AbstractHessianOutput

public class LocaleSerializer extends AbstractSerializer
{

	private static LocaleSerializer SERIALIZER = new LocaleSerializer();

	public LocaleSerializer()
	{
	}

	public static LocaleSerializer create()
	{
		return SERIALIZER;
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		if (obj == null)
		{
			out.writeNull();
		} else
		{
			Locale locale = (Locale)obj;
			out.writeObject(new LocaleHandle(locale.toString()));
		}
	}

}
