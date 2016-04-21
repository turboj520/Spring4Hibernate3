// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianException.java

package com.autohome.hessian;


public class HessianException extends RuntimeException
{

	public HessianException()
	{
	}

	public HessianException(String message)
	{
		super(message);
	}

	public HessianException(String message, Throwable rootCause)
	{
		super(message, rootCause);
	}

	public HessianException(Throwable rootCause)
	{
		super(rootCause);
	}
}
