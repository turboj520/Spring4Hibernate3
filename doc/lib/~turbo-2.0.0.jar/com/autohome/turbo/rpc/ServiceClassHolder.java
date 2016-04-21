// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceClassHolder.java

package com.autohome.turbo.rpc;


public class ServiceClassHolder
{

	private static final ServiceClassHolder INSTANCE = new ServiceClassHolder();
	private final ThreadLocal holder = new ThreadLocal();

	public static ServiceClassHolder getInstance()
	{
		return INSTANCE;
	}

	private ServiceClassHolder()
	{
	}

	public Class popServiceClass()
	{
		Class clazz = (Class)holder.get();
		holder.remove();
		return clazz;
	}

	public void pushServiceClass(Class clazz)
	{
		holder.set(clazz);
	}

}
