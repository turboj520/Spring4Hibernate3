// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CalendarHandle.java

package com.autohome.hessian.io;

import java.io.Serializable;
import java.util.*;

// Referenced classes of package com.autohome.hessian.io:
//			HessianHandle

public class CalendarHandle
	implements Serializable, HessianHandle
{

	private Class type;
	private Date date;

	public CalendarHandle()
	{
	}

	public CalendarHandle(Class type, long time)
	{
		if (!java/util/GregorianCalendar.equals(type))
			this.type = type;
		date = new Date(time);
	}

	private Object readResolve()
	{
		Calendar cal;
		if (type != null)
			cal = (Calendar)type.newInstance();
		else
			cal = new GregorianCalendar();
		cal.setTimeInMillis(date.getTime());
		return cal;
		RuntimeException e;
		e;
		throw e;
		e;
		throw new RuntimeException(e);
	}
}
