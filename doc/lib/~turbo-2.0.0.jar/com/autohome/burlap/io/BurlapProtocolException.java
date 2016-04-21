// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapProtocolException.java

package com.autohome.burlap.io;

import com.autohome.hessian.io.HessianProtocolException;

public class BurlapProtocolException extends HessianProtocolException
{

	public BurlapProtocolException()
	{
	}

	public BurlapProtocolException(String message)
	{
		super(message);
	}

	public BurlapProtocolException(String message, Throwable rootCause)
	{
		super(message, rootCause);
	}

	public BurlapProtocolException(Throwable rootCause)
	{
		super(rootCause);
	}
}
