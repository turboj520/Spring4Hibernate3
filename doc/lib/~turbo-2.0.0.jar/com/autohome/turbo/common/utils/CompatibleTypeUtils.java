// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CompatibleTypeUtils.java

package com.autohome.turbo.common.utils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Referenced classes of package com.autohome.turbo.common.utils:
//			ReflectUtils

public class CompatibleTypeUtils
{

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private CompatibleTypeUtils()
	{
	}

	public static Object compatibleTypeConvert(Object value, Class type)
	{
		if (value == null || type == null || type.isAssignableFrom(value.getClass()))
			return value;
		if (!(value instanceof String))
			break MISSING_BLOCK_LABEL_404;
		String string = (String)value;
		if (Character.TYPE.equals(type) || java/lang/Character.equals(type))
			if (string.length() != 1)
				throw new IllegalArgumentException(String.format("CAN NOT convert String(%s) to char! when convert String to char, the String MUST only 1 char.", new Object[] {
					string
				}));
			else
				return Character.valueOf(string.charAt(0));
		if (type.isEnum())
			return Enum.valueOf(type, string);
		if (type == java/math/BigInteger)
			return new BigInteger(string);
		if (type == java/math/BigDecimal)
			return new BigDecimal(string);
		if (type == java/lang/Short || type == Short.TYPE)
			return new Short(string);
		if (type == java/lang/Integer || type == Integer.TYPE)
			return new Integer(string);
		if (type == java/lang/Long || type == Long.TYPE)
			return new Long(string);
		if (type == java/lang/Double || type == Double.TYPE)
			return new Double(string);
		if (type == java/lang/Float || type == Float.TYPE)
			return new Float(string);
		if (type == java/lang/Byte || type == Byte.TYPE)
			return new Byte(string);
		if (type == java/lang/Boolean || type == Boolean.TYPE)
			return new Boolean(string);
		if (type != java/util/Date)
			break MISSING_BLOCK_LABEL_372;
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse((String)value);
		ParseException e;
		e;
		throw new IllegalStateException((new StringBuilder()).append("Failed to parse date ").append(value).append(" by format ").append("yyyy-MM-dd HH:mm:ss").append(", cause: ").append(e.getMessage()).toString(), e);
		if (type != java/lang/Class)
			break MISSING_BLOCK_LABEL_859;
		return ReflectUtils.name2class((String)value);
		e;
		throw new RuntimeException(e.getMessage(), e);
		Collection collection;
		if (value instanceof Number)
		{
			Number number = (Number)value;
			if (type == Byte.TYPE || type == java/lang/Byte)
				return Byte.valueOf(number.byteValue());
			if (type == Short.TYPE || type == java/lang/Short)
				return Short.valueOf(number.shortValue());
			if (type == Integer.TYPE || type == java/lang/Integer)
				return Integer.valueOf(number.intValue());
			if (type == Long.TYPE || type == java/lang/Long)
				return Long.valueOf(number.longValue());
			if (type == Float.TYPE || type == java/lang/Float)
				return Float.valueOf(number.floatValue());
			if (type == Double.TYPE || type == java/lang/Double)
				return Double.valueOf(number.doubleValue());
			if (type == java/math/BigInteger)
				return BigInteger.valueOf(number.longValue());
			if (type == java/math/BigDecimal)
				return BigDecimal.valueOf(number.doubleValue());
			if (type == java/util/Date)
				return new Date(number.longValue());
			break MISSING_BLOCK_LABEL_859;
		}
		if (!(value instanceof Collection))
			break MISSING_BLOCK_LABEL_748;
		collection = (Collection)value;
		if (type.isArray())
		{
			int length = collection.size();
			Object array = Array.newInstance(type.getComponentType(), length);
			int i = 0;
			Object item;
			for (Iterator i$ = collection.iterator(); i$.hasNext(); Array.set(array, i++, item))
				item = i$.next();

			return array;
		}
		if (type.isInterface())
			break MISSING_BLOCK_LABEL_713;
		Collection result;
		result = (Collection)type.newInstance();
		result.addAll(collection);
		return result;
		result;
		break MISSING_BLOCK_LABEL_859;
		if (type == java/util/List)
			return new ArrayList(collection);
		if (type == java/util/Set)
			return new HashSet(collection);
		break MISSING_BLOCK_LABEL_859;
		if (value.getClass().isArray() && java/util/Collection.isAssignableFrom(type))
		{
			if (!type.isInterface())
				try
				{
					collection = (Collection)type.newInstance();
				}
				// Misplaced declaration of an exception variable
				catch (Collection result)
				{
					collection = new ArrayList();
				}
			else
			if (type == java/util/Set)
				collection = new HashSet();
			else
				collection = new ArrayList();
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++)
				collection.add(Array.get(value, i));

			return collection;
		}
		return value;
	}
}
