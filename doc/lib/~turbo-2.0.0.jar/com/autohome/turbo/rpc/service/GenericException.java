// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericException.java

package com.autohome.turbo.rpc.service;

import com.autohome.turbo.common.utils.StringUtils;

public class GenericException extends RuntimeException
{

	private static final long serialVersionUID = 0xef97a0a11e14e9e6L;
	private String exceptionClass;
	private String exceptionMessage;

	public GenericException()
	{
	}

	public GenericException(String exceptionClass, String exceptionMessage)
	{
		super(exceptionMessage);
		this.exceptionClass = exceptionClass;
		this.exceptionMessage = exceptionMessage;
	}

	public GenericException(Throwable cause)
	{
		super(StringUtils.toString(cause));
		exceptionClass = cause.getClass().getName();
		exceptionMessage = cause.getMessage();
	}

	public String getExceptionClass()
	{
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass)
	{
		this.exceptionClass = exceptionClass;
	}

	public String getExceptionMessage()
	{
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage)
	{
		this.exceptionMessage = exceptionMessage;
	}
}
