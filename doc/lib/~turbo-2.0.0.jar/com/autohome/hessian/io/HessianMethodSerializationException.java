// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianMethodSerializationException.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;

public class HessianMethodSerializationException extends HessianException
{

	public HessianMethodSerializationException()
	{
	}

	public HessianMethodSerializationException(String message)
	{
		super(message);
	}

	public HessianMethodSerializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public HessianMethodSerializationException(Throwable cause)
	{
		super(cause);
	}
}
