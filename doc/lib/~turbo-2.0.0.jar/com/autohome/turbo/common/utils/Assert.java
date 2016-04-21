// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Assert.java

package com.autohome.turbo.common.utils;


public abstract class Assert
{

	protected Assert()
	{
	}

	public static void notNull(Object obj, String message)
	{
		if (obj == null)
			throw new IllegalArgumentException(message);
		else
			return;
	}
}
