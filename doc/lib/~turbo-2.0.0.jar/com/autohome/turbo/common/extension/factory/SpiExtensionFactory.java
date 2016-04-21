// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SpiExtensionFactory.java

package com.autohome.turbo.common.extension.factory;

import com.autohome.turbo.common.extension.*;
import java.util.Set;

public class SpiExtensionFactory
	implements ExtensionFactory
{

	public SpiExtensionFactory()
	{
	}

	public Object getExtension(Class type, String name)
	{
		if (type.isInterface() && type.isAnnotationPresent(com/autohome/turbo/common/extension/SPI))
		{
			ExtensionLoader loader = ExtensionLoader.getExtensionLoader(type);
			if (loader.getSupportedExtensions().size() > 0)
				return loader.getAdaptiveExtension();
		}
		return null;
	}
}
