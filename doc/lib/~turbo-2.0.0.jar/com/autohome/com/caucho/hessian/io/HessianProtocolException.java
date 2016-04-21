// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianProtocolException.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

public class HessianProtocolException extends IOException
{

	private Throwable rootCause;

	public HessianProtocolException()
	{
	}

	public HessianProtocolException(String message)
	{
		super(message);
	}

	public HessianProtocolException(String message, Throwable rootCause)
	{
		super(message);
		this.rootCause = rootCause;
	}

	public HessianProtocolException(Throwable rootCause)
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
