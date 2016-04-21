// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ActivateComparator.java

package com.autohome.turbo.common.extension.support;

import com.autohome.turbo.common.extension.*;
import java.util.Comparator;

public class ActivateComparator
	implements Comparator
{

	public static final Comparator COMPARATOR = new ActivateComparator();

	public ActivateComparator()
	{
	}

	public int compare(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		if (o1.equals(o2))
			return 0;
		Activate a1 = (Activate)o1.getClass().getAnnotation(com/autohome/turbo/common/extension/Activate);
		Activate a2 = (Activate)o2.getClass().getAnnotation(com/autohome/turbo/common/extension/Activate);
		if ((a1.before().length > 0 || a1.after().length > 0 || a2.before().length > 0 || a2.after().length > 0) && o1.getClass().getInterfaces().length > 0 && o1.getClass().getInterfaces()[0].isAnnotationPresent(com/autohome/turbo/common/extension/SPI))
		{
			ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(o1.getClass().getInterfaces()[0]);
			if (a1.before().length > 0 || a1.after().length > 0)
			{
				String n2 = extensionLoader.getExtensionName(o2.getClass());
				String arr$[] = a1.before();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String before = arr$[i$];
					if (before.equals(n2))
						return -1;
				}

				arr$ = a1.after();
				len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String after = arr$[i$];
					if (after.equals(n2))
						return 1;
				}

			}
			if (a2.before().length > 0 || a2.after().length > 0)
			{
				String n1 = extensionLoader.getExtensionName(o1.getClass());
				String arr$[] = a2.before();
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String before = arr$[i$];
					if (before.equals(n1))
						return 1;
				}

				arr$ = a2.after();
				len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					String after = arr$[i$];
					if (after.equals(n1))
						return -1;
				}

			}
		}
		int n1 = a1 != null ? a1.order() : 0;
		int n2 = a2 != null ? a2.order() : 0;
		return n1 <= n2 ? -1 : 1;
	}

}
