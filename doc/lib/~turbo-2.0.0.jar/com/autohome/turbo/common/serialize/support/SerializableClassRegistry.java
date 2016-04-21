// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SerializableClassRegistry.java

package com.autohome.turbo.common.serialize.support;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SerializableClassRegistry
{

	private static final Set registrations = new LinkedHashSet();

	public SerializableClassRegistry()
	{
	}

	public static void registerClass(Class clazz)
	{
		registrations.add(clazz);
	}

	public static Set getRegisteredClasses()
	{
		return registrations;
	}

}
