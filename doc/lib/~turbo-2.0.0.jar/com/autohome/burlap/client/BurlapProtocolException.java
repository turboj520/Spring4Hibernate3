// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapProtocolException.java

package com.autohome.burlap.client;

import java.io.IOException;

public class BurlapProtocolException extends IOException
{

	private Throwable rootCause;

	public BurlapProtocolException()
	{
	}

	public BurlapProtocolException(String message)
	{
		super(message);
	}

	public BurlapProtocolException(String message, Throwable rootCause)
	{
		super(message);
		this.rootCause = rootCause;
	}

	public BurlapProtocolException(Throwable rootCause)
	{
		super(String.valueOf(rootCause));
		this.rootCause = rootCause;
	}

	public Throwable getRootCause()
	{
		return rootCause;
	}
}
