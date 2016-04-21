// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   A0.java

package com.autohome.hessian.test;

import java.io.Serializable;

public class A0
	implements Serializable
{

	public A0()
	{
	}

	public boolean equals(Object v)
	{
		return v != null && getClass().equals(v.getClass());
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getName()).append("[]").toString();
	}
}
