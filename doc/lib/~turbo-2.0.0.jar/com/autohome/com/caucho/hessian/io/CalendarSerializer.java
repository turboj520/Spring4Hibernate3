// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CalendarSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Calendar;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializer, CalendarHandle, AbstractHessianOutput

public class CalendarSerializer extends AbstractSerializer
{

	private static CalendarSerializer SERIALIZER = new CalendarSerializer();

	public CalendarSerializer()
	{
	}

	public static CalendarSerializer create()
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
			Calendar cal = (Calendar)obj;
			out.writeObject(new CalendarHandle(cal.getClass(), cal.getTimeInMillis()));
		}
	}

}
