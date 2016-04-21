// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClassHelper.java

package com.autohome.turbo.common.utils;

import java.lang.reflect.Array;
import java.util.*;

public class ClassHelper
{

	public static final String ARRAY_SUFFIX = "[]";
	private static final String INTERNAL_ARRAY_PREFIX = "[L";
	private static final Map primitiveTypeNameMap;
	private static final Map primitiveWrapperTypeMap;

	public ClassHelper()
	{
	}

	public static Class forNameWithThreadContextClassLoader(String name)
		throws ClassNotFoundException
	{
		return forName(name, Thread.currentThread().getContextClassLoader());
	}

	public static Class forNameWithCallerClassLoader(String name, Class caller)
		throws ClassNotFoundException
	{
		return forName(name, caller.getClassLoader());
	}

	public static ClassLoader getCallerClassLoader(Class caller)
	{
		return caller.getClassLoader();
	}

	public static ClassLoader getClassLoader(Class cls)
	{
		ClassLoader cl = null;
		try
		{
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) { }
		if (cl == null)
			cl = cls.getClassLoader();
		return cl;
	}

	public static ClassLoader getClassLoader()
	{
		return getClassLoader(com/autohome/turbo/common/utils/ClassHelper);
	}

	public static Class forName(String name)
		throws ClassNotFoundException
	{
		return forName(name, getClassLoader());
	}

	public static Class forName(String name, ClassLoader classLoader)
		throws ClassNotFoundException, LinkageError
	{
		Class clazz = resolvePrimitiveClassName(name);
		if (clazz != null)
			return clazz;
		if (name.endsWith("[]"))
		{
			String elementClassName = name.substring(0, name.length() - "[]".length());
			Class elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}
		int internalArrayMarker = name.indexOf("[L");
		if (internalArrayMarker != -1 && name.endsWith(";"))
		{
			String elementClassName = null;
			if (internalArrayMarker == 0)
				elementClassName = name.substring("[L".length(), name.length() - 1);
			else
			if (name.startsWith("["))
				elementClassName = name.substring(1);
			Class elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null)
			classLoaderToUse = getClassLoader();
		return classLoaderToUse.loadClass(name);
	}

	public static Class resolvePrimitiveClassName(String name)
	{
		Class result = null;
		if (name != null && name.length() <= 8)
			result = (Class)primitiveTypeNameMap.get(name);
		return result;
	}

	public static String toShortString(Object obj)
	{
		if (obj == null)
			return "null";
		else
			return (new StringBuilder()).append(obj.getClass().getSimpleName()).append("@").append(System.identityHashCode(obj)).toString();
	}

	static 
	{
		primitiveTypeNameMap = new HashMap(16);
		primitiveWrapperTypeMap = new HashMap(8);
		primitiveWrapperTypeMap.put(java/lang/Boolean, Boolean.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Byte, Byte.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Character, Character.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Double, Double.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Float, Float.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Integer, Integer.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Long, Long.TYPE);
		primitiveWrapperTypeMap.put(java/lang/Short, Short.TYPE);
		Set primitiveTypeNames = new HashSet(16);
		primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
		primitiveTypeNames.addAll(Arrays.asList(new Class[] {
			[Z, [B, [C, [D, [F, [I, [J, [S
		}));
		Class primitiveClass;
		for (Iterator it = primitiveTypeNames.iterator(); it.hasNext(); primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass))
			primitiveClass = (Class)it.next();

	}
}
