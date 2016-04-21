// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClassUtils.java

package com.autohome.turbo.common.compiler.support;

import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class ClassUtils
{

	public static final String CLASS_EXTENSION = ".class";
	public static final String JAVA_EXTENSION = ".java";
	private static final int JIT_LIMIT = 5120;

	public static Object newInstance(String name)
	{
		return forName(name).newInstance();
		InstantiationException e;
		e;
		throw new IllegalStateException(e.getMessage(), e);
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}

	public static Class forName(String packages[], String className)
	{
		return _forName(className);
		ClassNotFoundException e;
		e;
		if (packages == null || packages.length <= 0) goto _L2; else goto _L1
_L1:
		String arr$[];
		int len$;
		int i$;
		arr$ = packages;
		len$ = arr$.length;
		i$ = 0;
_L3:
		String pkg;
		if (i$ >= len$)
			break; /* Loop/switch isn't completed */
		pkg = arr$[i$];
		return _forName((new StringBuilder()).append(pkg).append(".").append(className).toString());
		ClassNotFoundException e2;
		e2;
		i$++;
		if (true) goto _L3; else goto _L2
_L2:
		throw new IllegalStateException(e.getMessage(), e);
	}

	public static Class forName(String className)
	{
		return _forName(className);
		ClassNotFoundException e;
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}

	public static Class _forName(String className)
		throws ClassNotFoundException
	{
		if ("boolean".equals(className))
			return Boolean.TYPE;
		if ("byte".equals(className))
			return Byte.TYPE;
		if ("char".equals(className))
			return Character.TYPE;
		if ("short".equals(className))
			return Short.TYPE;
		if ("int".equals(className))
			return Integer.TYPE;
		if ("long".equals(className))
			return Long.TYPE;
		if ("float".equals(className))
			return Float.TYPE;
		if ("double".equals(className))
			return Double.TYPE;
		if ("boolean[]".equals(className))
			return [Z;
		if ("byte[]".equals(className))
			return [B;
		if ("char[]".equals(className))
			return [C;
		if ("short[]".equals(className))
			return [S;
		if ("int[]".equals(className))
			return [I;
		if ("long[]".equals(className))
			return [J;
		if ("float[]".equals(className))
			return [F;
		if ("double[]".equals(className))
			return [D;
		return arrayForName(className);
		ClassNotFoundException e;
		e;
		if (className.indexOf('.') != -1)
			break MISSING_BLOCK_LABEL_248;
		return arrayForName((new StringBuilder()).append("java.lang.").append(className).toString());
		ClassNotFoundException e2;
		e2;
		throw e;
	}

	private static Class arrayForName(String className)
		throws ClassNotFoundException
	{
		return Class.forName(className.endsWith("[]") ? (new StringBuilder()).append("[L").append(className.substring(0, className.length() - 2)).append(";").toString() : className, true, Thread.currentThread().getContextClassLoader());
	}

	public static Class getBoxedClass(Class type)
	{
		if (type == Boolean.TYPE)
			return java/lang/Boolean;
		if (type == Character.TYPE)
			return java/lang/Character;
		if (type == Byte.TYPE)
			return java/lang/Byte;
		if (type == Short.TYPE)
			return java/lang/Short;
		if (type == Integer.TYPE)
			return java/lang/Integer;
		if (type == Long.TYPE)
			return java/lang/Long;
		if (type == Float.TYPE)
			return java/lang/Float;
		if (type == Double.TYPE)
			return java/lang/Double;
		else
			return type;
	}

	public static Boolean boxed(boolean v)
	{
		return Boolean.valueOf(v);
	}

	public static Character boxed(char v)
	{
		return Character.valueOf(v);
	}

	public static Byte boxed(byte v)
	{
		return Byte.valueOf(v);
	}

	public static Short boxed(short v)
	{
		return Short.valueOf(v);
	}

	public static Integer boxed(int v)
	{
		return Integer.valueOf(v);
	}

	public static Long boxed(long v)
	{
		return Long.valueOf(v);
	}

	public static Float boxed(float v)
	{
		return Float.valueOf(v);
	}

	public static Double boxed(double v)
	{
		return Double.valueOf(v);
	}

	public static Object boxed(Object v)
	{
		return v;
	}

	public static boolean unboxed(Boolean v)
	{
		return v != null ? v.booleanValue() : false;
	}

	public static char unboxed(Character v)
	{
		return v != null ? v.charValue() : '\0';
	}

	public static byte unboxed(Byte v)
	{
		return v != null ? v.byteValue() : 0;
	}

	public static short unboxed(Short v)
	{
		return v != null ? v.shortValue() : 0;
	}

	public static int unboxed(Integer v)
	{
		return v != null ? v.intValue() : 0;
	}

	public static long unboxed(Long v)
	{
		return v != null ? v.longValue() : 0L;
	}

	public static float unboxed(Float v)
	{
		return v != null ? v.floatValue() : 0.0F;
	}

	public static double unboxed(Double v)
	{
		return v != null ? v.doubleValue() : 0.0D;
	}

	public static Object unboxed(Object v)
	{
		return v;
	}

	public static boolean isNotEmpty(Object object)
	{
		return getSize(object) > 0;
	}

	public static int getSize(Object object)
	{
		if (object == null)
			return 0;
		if (object instanceof Collection)
			return ((Collection)object).size();
		if (object instanceof Map)
			return ((Map)object).size();
		if (object.getClass().isArray())
			return Array.getLength(object);
		else
			return -1;
	}

	public static URI toURI(String name)
	{
		return new URI(name);
		URISyntaxException e;
		e;
		throw new RuntimeException(e);
	}

	public static Class getGenericClass(Class cls)
	{
		return getGenericClass(cls, 0);
	}

	public static Class getGenericClass(Class cls, int i)
	{
		Object genericClass;
		ParameterizedType parameterizedType = (ParameterizedType)cls.getGenericInterfaces()[0];
		genericClass = parameterizedType.getActualTypeArguments()[i];
		if (genericClass instanceof ParameterizedType)
			return (Class)((ParameterizedType)genericClass).getRawType();
		if (genericClass instanceof GenericArrayType)
			return (Class)((GenericArrayType)genericClass).getGenericComponentType();
		if (genericClass != null)
			return (Class)genericClass;
		break MISSING_BLOCK_LABEL_72;
		Throwable e;
		e;
		if (cls.getSuperclass() != null)
			return getGenericClass(cls.getSuperclass(), i);
		else
			throw new IllegalArgumentException((new StringBuilder()).append(cls.getName()).append(" generic type undefined!").toString());
	}

	public static boolean isBeforeJava5(String javaVersion)
	{
		return javaVersion == null || javaVersion.length() == 0 || "1.0".equals(javaVersion) || "1.1".equals(javaVersion) || "1.2".equals(javaVersion) || "1.3".equals(javaVersion) || "1.4".equals(javaVersion);
	}

	public static boolean isBeforeJava6(String javaVersion)
	{
		return isBeforeJava5(javaVersion) || "1.5".equals(javaVersion);
	}

	public static String toString(Throwable e)
	{
		StringWriter w;
		PrintWriter p;
		w = new StringWriter();
		p = new PrintWriter(w);
		p.print((new StringBuilder()).append(e.getClass().getName()).append(": ").toString());
		if (e.getMessage() != null)
			p.print((new StringBuilder()).append(e.getMessage()).append("\n").toString());
		p.println();
		String s;
		e.printStackTrace(p);
		s = w.toString();
		p.close();
		return s;
		Exception exception;
		exception;
		p.close();
		throw exception;
	}

	public static void checkBytecode(String name, byte bytecode[])
	{
		if (bytecode.length > 5120)
			System.err.println((new StringBuilder()).append("The template bytecode too long, may be affect the JIT compiler. template class: ").append(name).toString());
	}

	public static String getSizeMethod(Class cls)
	{
		return (new StringBuilder()).append(cls.getMethod("size", new Class[0]).getName()).append("()").toString();
		NoSuchMethodException e;
		e;
		return (new StringBuilder()).append(cls.getMethod("length", new Class[0]).getName()).append("()").toString();
		NoSuchMethodException e2;
		e2;
		return (new StringBuilder()).append(cls.getMethod("getSize", new Class[0]).getName()).append("()").toString();
		NoSuchMethodException e3;
		e3;
		return (new StringBuilder()).append(cls.getMethod("getLength", new Class[0]).getName()).append("()").toString();
		NoSuchMethodException e4;
		e4;
		return null;
	}

	public static String getMethodName(Method method, Class parameterClasses[], String rightCode)
	{
		if (method.getParameterTypes().length > parameterClasses.length)
		{
			Class types[] = method.getParameterTypes();
			StringBuilder buf = new StringBuilder(rightCode);
			for (int i = parameterClasses.length; i < types.length; i++)
			{
				if (buf.length() > 0)
					buf.append(",");
				Class type = types[i];
				String def;
				if (type == Boolean.TYPE)
					def = "false";
				else
				if (type == Character.TYPE)
					def = "'\\0'";
				else
				if (type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE || type == Float.TYPE || type == Double.TYPE)
					def = "0";
				else
					def = "null";
				buf.append(def);
			}

		}
		return (new StringBuilder()).append(method.getName()).append("(").append(rightCode).append(")").toString();
	}

	public static Method searchMethod(Class currentClass, String name, Class parameterTypes[])
		throws NoSuchMethodException
	{
		if (currentClass == null)
			throw new NoSuchMethodException("class == null");
		return currentClass.getMethod(name, parameterTypes);
		NoSuchMethodException e;
		e;
		Method arr$[] = currentClass.getMethods();
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method method = arr$[i$];
			if (!method.getName().equals(name) || parameterTypes.length != method.getParameterTypes().length || !Modifier.isPublic(method.getModifiers()))
				continue;
			if (parameterTypes.length > 0)
			{
				Class types[] = method.getParameterTypes();
				boolean match = true;
				int i = 0;
				do
				{
					if (i >= parameterTypes.length)
						break;
					if (!types[i].isAssignableFrom(parameterTypes[i]))
					{
						match = false;
						break;
					}
					i++;
				} while (true);
				if (!match)
					continue;
			}
			return method;
		}

		throw e;
	}

	public static String getInitCode(Class type)
	{
		if (Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type) || Long.TYPE.equals(type) || Float.TYPE.equals(type) || Double.TYPE.equals(type))
			return "0";
		if (Character.TYPE.equals(type))
			return "'\\0'";
		if (Boolean.TYPE.equals(type))
			return "false";
		else
			return "null";
	}

	public static Map toMap(java.util.Map.Entry entries[])
	{
		Map map = new HashMap();
		if (entries != null && entries.length > 0)
		{
			java.util.Map.Entry arr$[] = entries;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				java.util.Map.Entry enrty = arr$[i$];
				map.put(enrty.getKey(), enrty.getValue());
			}

		}
		return map;
	}

	private ClassUtils()
	{
	}
}
