// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IOExceptionWrapper.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

public class IOExceptionWrapper extends IOException
{

	private Throwable _cause;

	public IOExceptionWrapper(Throwable cause)
	{
		super(cause.toString());
		_cause = cause;
	}

	public IOExceptionWrapper(String msg, Throwable cause)
	{
		super(msg);
		_cause = cause;
	}

	public Throwable getCause()
	{
		return _cause;
	}
}
