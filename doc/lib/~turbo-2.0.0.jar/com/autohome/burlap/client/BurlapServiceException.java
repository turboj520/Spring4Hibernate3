// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapServiceException.java

package com.autohome.burlap.client;


public class BurlapServiceException extends Exception
{

	private String code;
	private Object detail;

	public BurlapServiceException()
	{
	}

	public BurlapServiceException(String message, String code, Object detail)
	{
		super(message);
		this.code = code;
		this.detail = detail;
	}

	public String getCode()
	{
		return code;
	}

	public Object getDetail()
	{
		return detail;
	}
}
