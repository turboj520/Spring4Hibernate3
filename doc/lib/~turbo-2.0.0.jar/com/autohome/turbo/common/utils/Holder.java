// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Holder.java

package com.autohome.turbo.common.utils;


public class Holder
{

	private volatile Object value;

	public Holder()
	{
	}

	public void set(Object value)
	{
		this.value = value;
	}

	public Object get()
	{
		return value;
	}
}
