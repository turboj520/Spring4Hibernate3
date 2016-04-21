// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AnnotationInvocationHandler.java

package com.autohome.hessian.io;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationInvocationHandler
	implements InvocationHandler
{

	private Class _annType;
	private HashMap _valueMap;

	public AnnotationInvocationHandler(Class annType, HashMap valueMap)
	{
		_annType = annType;
		_valueMap = valueMap;
	}

	public Object invoke(Object proxy, Method method, Object args[])
		throws Throwable
	{
		String name = method.getName();
		if (args != null && args.length != 0)
			return null;
		if (name.equals("annotationType"))
			return _annType;
		if (name.equals("toString"))
			return toString();
		else
			return _valueMap.get(method.getName());
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("@");
		sb.append(_annType.getName());
		sb.append("[");
		boolean isFirst = true;
		for (Iterator i$ = _valueMap.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			if (!isFirst)
				sb.append(", ");
			isFirst = false;
			sb.append(entry.getKey());
			sb.append("=");
			if (entry.getValue() instanceof String)
				sb.append('"').append(entry.getValue()).append('"');
			else
				sb.append(entry.getValue());
		}

		sb.append("]");
		return sb.toString();
	}
}
