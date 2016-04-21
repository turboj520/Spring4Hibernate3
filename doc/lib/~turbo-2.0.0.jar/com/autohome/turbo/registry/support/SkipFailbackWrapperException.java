// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SkipFailbackWrapperException.java

package com.autohome.turbo.registry.support;


public class SkipFailbackWrapperException extends RuntimeException
{

	public SkipFailbackWrapperException(Throwable cause)
	{
		super(cause);
	}

	public synchronized Throwable fillInStackTrace()
	{
		return null;
	}
}
