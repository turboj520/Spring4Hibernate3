// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReflectionUtils.java

package com.autohome.turbo.common.serialize.support.kryo;


public abstract class ReflectionUtils
{

	public ReflectionUtils()
	{
	}

	public static boolean checkZeroArgConstructor(Class clazz)
	{
		clazz.getDeclaredConstructor(new Class[0]);
		return true;
		NoSuchMethodException e;
		e;
		return false;
	}
}
