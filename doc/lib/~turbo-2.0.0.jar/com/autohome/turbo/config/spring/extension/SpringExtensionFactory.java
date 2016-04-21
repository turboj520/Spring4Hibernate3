// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SpringExtensionFactory.java

package com.autohome.turbo.config.spring.extension;

import com.autohome.turbo.common.extension.ExtensionFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.context.ApplicationContext;

public class SpringExtensionFactory
	implements ExtensionFactory
{

	private static final Set contexts = new ConcurrentHashSet();

	public SpringExtensionFactory()
	{
	}

	public static void addApplicationContext(ApplicationContext context)
	{
		contexts.add(context);
	}

	public static void removeApplicationContext(ApplicationContext context)
	{
		contexts.remove(context);
	}

	public Object getExtension(Class type, String name)
	{
		for (Iterator i$ = contexts.iterator(); i$.hasNext();)
		{
			ApplicationContext context = (ApplicationContext)i$.next();
			if (context.containsBean(name))
			{
				Object bean = context.getBean(name);
				if (type.isInstance(bean))
					return bean;
			}
		}

		return null;
	}

}
