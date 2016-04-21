// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FloatHandle.java

package com.autohome.hessian.io;

import java.io.Serializable;

public class FloatHandle
	implements Serializable
{

	private float _value;

	private FloatHandle()
	{
	}

	public FloatHandle(float value)
	{
		_value = value;
	}

	public float getValue()
	{
		return _value;
	}

	public Object readResolve()
	{
		return new Float(_value);
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getSimpleName()).append("[").append(_value).append("]").toString();
	}
}
