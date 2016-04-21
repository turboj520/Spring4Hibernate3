// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NameServiceException.java

package com.autohome.services.name;

import java.io.IOException;

public class NameServiceException extends IOException
{

	private Throwable rootCause;

	public NameServiceException()
	{
	}

	public NameServiceException(String name)
	{
		super(name);
	}

	public NameServiceException(String name, Throwable rootCause)
	{
		super(name);
		this.rootCause = rootCause;
	}

	public NameServiceException(Throwable rootCause)
	{
		super(String.valueOf(rootCause));
		this.rootCause = rootCause;
	}

	public Throwable getRootCause()
	{
		return rootCause;
	}
}
