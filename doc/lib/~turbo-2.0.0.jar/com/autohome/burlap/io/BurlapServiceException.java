// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapServiceException.java

package com.autohome.burlap.io;

import com.autohome.hessian.io.HessianServiceException;

public class BurlapServiceException extends HessianServiceException
{

	public BurlapServiceException()
	{
	}

	public BurlapServiceException(String message, String code, Object detail)
	{
		super(message, code, detail);
	}
}
