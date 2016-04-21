// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CollectionUtils.java

package com.autohome.turbo.common.utils;

import java.util.*;

public class CollectionUtils
{

	private static final Comparator SIMPLE_NAME_COMPARATOR = new Comparator() {

		public int compare(String s1, String s2)
		{
			if (s1 == null && s2 == null)
				return 0;
			if (s1 == null)
				return -1;
			if (s2 == null)
				return 1;
			int i1 = s1.lastIndexOf('.');
			if (i1 >= 0)
				s1 = s1.substring(i1 + 1);
			int i2 = s2.lastIndexOf('.');
			if (i2 >= 0)
				s2 = s2.substring(i2 + 1);
			return s1.compareToIgnoreCase(s2);
		}

		public volatile int compare(Object x0, Object x1)
		{
			return compare((String)x0, (String)x1);
		}

	};

	public static List sort(List list)
	{
		if (list != null && list.size() > 0)
			Collections.sort(list);
		return list;
	}

	public static List sortSimpleName(List list)
	{
		if (list != null && list.size() > 0)
			Collections.sort(list, SIMPLE_NAME_COMPARATOR);
		return list;
	}

	public static Map splitAll(Map list, String separator)
	{
		if (list == null)
			return null;
		Map result = new HashMap();
		java.util.Map.Entry entry;
		for (Iterator i$ = list.entrySet().iterator(); i$.hasNext(); result.put(entry.getKey(), split((List)entry.getValue(), separator)))
			entry = (java.util.Map.Entry)i$.next();

		return result;
	}

	public static Map joinAll(Map map, String separator)
	{
		if (map == null)
			return null;
		Map result = new HashMap();
		java.util.Map.Entry entry;
		for (Iterator i$ = map.entrySet().iterator(); i$.hasNext(); result.put(entry.getKey(), join((Map)entry.getValue(), separator)))
			entry = (java.util.Map.Entry)i$.next();

		return result;
	}

	public static Map split(List list, String separator)
	{
		if (list == null)
			return null;
		Map map = new HashMap();
		if (list == null || list.size() == 0)
			return map;
		for (Iterator i$ = list.iterator(); i$.hasNext();)
		{
			String item = (String)i$.next();
			int index = item.indexOf(separator);
			if (index == -1)
				map.put(item, "");
			else
				map.put(item.substring(0, index), item.substring(index + 1));
		}

		return map;
	}

	public static List join(Map map, String separator)
	{
		if (map == null)
			return null;
		List list = new ArrayList();
		if (map == null || map.size() == 0)
			return list;
		for (Iterator i$ = map.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (value == null || value.length() == 0)
				list.add(key);
			else
				list.add((new StringBuilder()).append(key).append(separator).append(value).toString());
		}

		return list;
	}

	public static String join(List list, String separator)
	{
		StringBuilder sb = new StringBuilder();
		String ele;
		for (Iterator i$ = list.iterator(); i$.hasNext(); sb.append(ele))
		{
			ele = (String)i$.next();
			if (sb.length() > 0)
				sb.append(separator);
		}

		return sb.toString();
	}

	public static boolean mapEquals(Map map1, Map map2)
	{
		if (map1 == null && map2 == null)
			return true;
		if (map1 == null || map2 == null)
			return false;
		if (map1.size() != map2.size())
			return false;
		for (Iterator i$ = map1.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			Object key = entry.getKey();
			Object value1 = entry.getValue();
			Object value2 = map2.get(key);
			if (!objectEquals(value1, value2))
				return false;
		}

		return true;
	}

	private static boolean objectEquals(Object obj1, Object obj2)
	{
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 == null || obj2 == null)
			return false;
		else
			return obj1.equals(obj2);
	}

	public static transient Map toStringMap(String pairs[])
	{
		Map parameters = new HashMap();
		if (pairs.length > 0)
		{
			if (pairs.length % 2 != 0)
				throw new IllegalArgumentException("pairs must be even.");
			for (int i = 0; i < pairs.length; i += 2)
				parameters.put(pairs[i], pairs[i + 1]);

		}
		return parameters;
	}

	public static transient Map toMap(Object pairs[])
	{
		Map ret = new HashMap();
		if (pairs == null || pairs.length == 0)
			return ret;
		if (pairs.length % 2 != 0)
			throw new IllegalArgumentException("Map pairs can not be odd number.");
		int len = pairs.length / 2;
		for (int i = 0; i < len; i++)
			ret.put(pairs[2 * i], pairs[2 * i + 1]);

		return ret;
	}

	public static boolean isEmpty(Collection collection)
	{
		return collection == null || collection.size() == 0;
	}

	public static boolean isNotEmpty(Collection collection)
	{
		return collection != null && collection.size() > 0;
	}

	private CollectionUtils()
	{
	}

}
