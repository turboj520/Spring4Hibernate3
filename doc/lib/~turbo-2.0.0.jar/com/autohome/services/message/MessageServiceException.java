// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MessageServiceException.java

package com.autohome.services.message;

import java.io.IOException;

public class MessageServiceException extends IOException
{

	private Throwable _rootCause;

	public MessageServiceException()
	{
	}

	public MessageServiceException(String message)
	{
		super(message);
	}

	public MessageServiceException(String message, Throwable rootCause)
	{
		super(message);
		_rootCause = rootCause;
	}

	public MessageServiceException(Throwable rootCause)
	{
		super(String.valueOf(rootCause));
		_rootCause = rootCause;
	}

	public Throwable getRootCause()
	{
		return getCause();
	}

	public Throwable getCause()
	{
		return _rootCause;
	}
}
