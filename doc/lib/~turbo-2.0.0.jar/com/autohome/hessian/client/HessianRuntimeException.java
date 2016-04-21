// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianRuntimeException.java

package com.autohome.hessian.client;


public class HessianRuntimeException extends RuntimeException
{

	private Throwable rootCause;

	public HessianRuntimeException()
	{
	}

	public HessianRuntimeException(String message)
	{
		super(message);
	}

	public HessianRuntimeException(String message, Throwable rootCause)
	{
		super(message);
		this.rootCause = rootCause;
	}

	public HessianRuntimeException(Throwable rootCause)
	{
		super(String.valueOf(rootCause));
		this.rootCause = rootCause;
	}

	public Throwable getRootCause()
	{
		return rootCause;
	}

	public Throwable getCause()
	{
		return getRootCause();
	}
}
