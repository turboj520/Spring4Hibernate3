// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CalendarSerializer.java

package com.autohome.hessian.io;

import java.util.Calendar;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, CalendarHandle, Serializer

public class CalendarSerializer extends AbstractSerializer
{

	public static final Serializer SER = new CalendarSerializer();

	public CalendarSerializer()
	{
	}

	public Object writeReplace(Object obj)
	{
		Calendar cal = (Calendar)obj;
		return new CalendarHandle(cal.getClass(), cal.getTimeInMillis());
	}

}
