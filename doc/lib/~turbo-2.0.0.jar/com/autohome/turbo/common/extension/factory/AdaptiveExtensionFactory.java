// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AdaptiveExtensionFactory.java

package com.autohome.turbo.common.extension.factory;

import com.autohome.turbo.common.extension.ExtensionFactory;
import com.autohome.turbo.common.extension.ExtensionLoader;
import java.util.*;

public class AdaptiveExtensionFactory
	implements ExtensionFactory
{

	private final List factories;

	public AdaptiveExtensionFactory()
	{
		ExtensionLoader loader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/extension/ExtensionFactory);
		List list = new ArrayList();
		String name;
		for (Iterator i$ = loader.getSupportedExtensions().iterator(); i$.hasNext(); list.add(loader.getExtension(name)))
			name = (String)i$.next();

		factories = Collections.unmodifiableList(list);
	}

	public Object getExtension(Class type, String name)
	{
		for (Iterator i$ = factories.iterator(); i$.hasNext();)
		{
			ExtensionFactory factory = (ExtensionFactory)i$.next();
			Object extension = factory.getExtension(type, name);
			if (extension != null)
				return extension;
		}

		return null;
	}
}
