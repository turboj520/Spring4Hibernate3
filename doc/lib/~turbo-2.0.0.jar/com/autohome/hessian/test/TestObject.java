// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TestObject.java

package com.autohome.hessian.test;

import java.io.Serializable;

public class TestObject
	implements Serializable
{

	private Object _value;

	public TestObject()
	{
	}

	public TestObject(Object value)
	{
		_value = value;
	}

	public Object getValue()
	{
		return _value;
	}

	public int hashCode()
	{
		if (_value != null)
			return _value.hashCode();
		else
			return 0;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof TestObject))
			return false;
		TestObject obj = (TestObject)o;
		if (_value != null)
			return _value.equals(obj._value);
		else
			return _value == obj._value;
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getName()).append("[").append(_value).append("]").toString();
	}
}
