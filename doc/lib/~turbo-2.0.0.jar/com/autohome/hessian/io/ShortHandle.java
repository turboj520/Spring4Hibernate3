// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ShortHandle.java

package com.autohome.hessian.io;

import java.io.Serializable;

public class ShortHandle
	implements Serializable
{

	private short _value;

	private ShortHandle()
	{
	}

	public ShortHandle(short value)
	{
		_value = value;
	}

	public short getValue()
	{
		return _value;
	}

	public Object readResolve()
	{
		return new Short(_value);
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getSimpleName()).append("[").append(_value).append("]").toString();
	}
}
