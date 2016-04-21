// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MenuComparator.java

package com.autohome.turbo.container.page;

import java.io.Serializable;
import java.util.Comparator;

// Referenced classes of package com.autohome.turbo.container.page:
//			Menu, PageHandler

public class MenuComparator
	implements Comparator, Serializable
{

	private static final long serialVersionUID = 0xd42000014866f4b3L;

	public MenuComparator()
	{
	}

	public int compare(PageHandler o1, PageHandler o2)
	{
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		else
			return o1.equals(o2) ? 0 : ((Menu)o1.getClass().getAnnotation(com/autohome/turbo/container/page/Menu)).order() <= ((Menu)o2.getClass().getAnnotation(com/autohome/turbo/container/page/Menu)).order() ? -1 : 1;
	}

	public volatile int compare(Object x0, Object x1)
	{
		return compare((PageHandler)x0, (PageHandler)x1);
	}
}
