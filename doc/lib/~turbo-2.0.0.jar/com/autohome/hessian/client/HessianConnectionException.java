// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianConnectionException.java

package com.autohome.hessian.client;

import com.autohome.hessian.HessianException;

public class HessianConnectionException extends HessianException
{

	public HessianConnectionException()
	{
	}

	public HessianConnectionException(String message)
	{
		super(message);
	}

	public HessianConnectionException(String message, Throwable rootCause)
	{
		super(message, rootCause);
	}

	public HessianConnectionException(Throwable rootCause)
	{
		super(rootCause);
	}
}
