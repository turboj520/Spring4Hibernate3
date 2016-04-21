// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapRuntimeException.java

package com.autohome.burlap.client;


public class BurlapRuntimeException extends RuntimeException
{

	private Throwable rootCause;

	public BurlapRuntimeException()
	{
	}

	public BurlapRuntimeException(String message)
	{
		super(message);
	}

	public BurlapRuntimeException(String message, Throwable rootCause)
	{
		super(message);
		this.rootCause = rootCause;
	}

	public BurlapRuntimeException(Throwable rootCause)
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
		return rootCause;
	}
}
