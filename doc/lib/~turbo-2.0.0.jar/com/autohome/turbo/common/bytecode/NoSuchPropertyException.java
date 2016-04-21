// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NoSuchPropertyException.java

package com.autohome.turbo.common.bytecode;


public class NoSuchPropertyException extends RuntimeException
{

	private static final long serialVersionUID = 0xda2d8fa8683b9662L;

	public NoSuchPropertyException()
	{
	}

	public NoSuchPropertyException(String msg)
	{
		super(msg);
	}
}
