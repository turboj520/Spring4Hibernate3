// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractSkeleton.java

package com.autohome.services.server;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class AbstractSkeleton
{

	private Class _apiClass;
	private Class _homeClass;
	private Class _objectClass;
	private HashMap _methodMap;

	protected AbstractSkeleton(Class apiClass)
	{
		_methodMap = new HashMap();
		_apiClass = apiClass;
		Method methodList[] = apiClass.getMethods();
		for (int i = 0; i < methodList.length; i++)
		{
			Method method = methodList[i];
			if (_methodMap.get(method.getName()) == null)
				_methodMap.put(method.getName(), methodList[i]);
			Class param[] = method.getParameterTypes();
			String mangledName = (new StringBuilder()).append(method.getName()).append("__").append(param.length).toString();
			_methodMap.put(mangledName, methodList[i]);
			_methodMap.put(mangleName(method, false), methodList[i]);
		}

	}

	public String getAPIClassName()
	{
		return _apiClass.getName();
	}

	public String getHomeClassName()
	{
		if (_homeClass != null)
			return _homeClass.getName();
		else
			return getAPIClassName();
	}

	public void setHomeClass(Class homeAPI)
	{
		_homeClass = homeAPI;
	}

	public String getObjectClassName()
	{
		if (_objectClass != null)
			return _objectClass.getName();
		else
			return getAPIClassName();
	}

	public void setObjectClass(Class objectAPI)
	{
		_objectClass = objectAPI;
	}

	protected Method getMethod(String mangledName)
	{
		return (Method)_methodMap.get(mangledName);
	}

	public static String mangleName(Method method, boolean isFull)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(method.getName());
		Class params[] = method.getParameterTypes();
		for (int i = 0; i < params.length; i++)
		{
			sb.append('_');
			sb.append(mangleClass(params[i], isFull));
		}

		return sb.toString();
	}

	public static String mangleClass(Class cl, boolean isFull)
	{
		String name = cl.getName();
		if (name.equals("boolean") || name.equals("java.lang.Boolean"))
			return "boolean";
		if (name.equals("int") || name.equals("java.lang.Integer") || name.equals("short") || name.equals("java.lang.Short") || name.equals("byte") || name.equals("java.lang.Byte"))
			return "int";
		if (name.equals("long") || name.equals("java.lang.Long"))
			return "long";
		if (name.equals("float") || name.equals("java.lang.Float") || name.equals("double") || name.equals("java.lang.Double"))
			return "double";
		if (name.equals("java.lang.String") || name.equals("com.autohome.util.CharBuffer") || name.equals("char") || name.equals("java.lang.Character") || name.equals("java.io.Reader"))
			return "string";
		if (name.equals("java.util.Date") || name.equals("com.autohome.util.QDate"))
			return "date";
		if (java/io/InputStream.isAssignableFrom(cl) || name.equals("[B"))
			return "binary";
		if (cl.isArray())
			return (new StringBuilder()).append("[").append(mangleClass(cl.getComponentType(), isFull)).toString();
		if (name.equals("org.w3c.dom.Node") || name.equals("org.w3c.dom.Element") || name.equals("org.w3c.dom.Document"))
			return "xml";
		if (isFull)
			return name;
		int p = name.lastIndexOf('.');
		if (p > 0)
			return name.substring(p + 1);
		else
			return name;
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getSimpleName()).append("[").append(_apiClass.getName()).append("]").toString();
	}
}
