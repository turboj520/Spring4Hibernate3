// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StringUtils.java

package com.autohome.turbo.common.utils;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.io.UnsafeStringWriter;
import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.utils:
//			ReflectUtils

public final class StringUtils
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/utils/StringUtils);
	public static final String EMPTY_STRING_ARRAY[] = new String[0];
	private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)");
	private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

	public static boolean isBlank(String str)
	{
		return str == null || str.length() == 0;
	}

	public static boolean isEmpty(String str)
	{
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str)
	{
		return str != null && str.length() > 0;
	}

	public static boolean isEquals(String s1, String s2)
	{
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null || s2 == null)
			return false;
		else
			return s1.equals(s2);
	}

	public static boolean isInteger(String str)
	{
		if (str == null || str.length() == 0)
			return false;
		else
			return INT_PATTERN.matcher(str).matches();
	}

	public static int parseInteger(String str)
	{
		if (!isInteger(str))
			return 0;
		else
			return Integer.parseInt(str);
	}

	public static boolean isJavaIdentifier(String s)
	{
		if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0)))
			return false;
		for (int i = 1; i < s.length(); i++)
			if (!Character.isJavaIdentifierPart(s.charAt(i)))
				return false;

		return true;
	}

	public static boolean isContains(String values, String value)
	{
		if (values == null || values.length() == 0)
			return false;
		else
			return isContains(Constants.COMMA_SPLIT_PATTERN.split(values), value);
	}

	public static boolean isContains(String values[], String value)
	{
		if (value != null && value.length() > 0 && values != null && values.length > 0)
		{
			String arr$[] = values;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String v = arr$[i$];
				if (value.equals(v))
					return true;
			}

		}
		return false;
	}

	public static boolean isNumeric(String str)
	{
		if (str == null)
			return false;
		int sz = str.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isDigit(str.charAt(i)))
				return false;

		return true;
	}

	public static String toString(Throwable e)
	{
		UnsafeStringWriter w;
		PrintWriter p;
		w = new UnsafeStringWriter();
		p = new PrintWriter(w);
		p.print(e.getClass().getName());
		if (e.getMessage() != null)
			p.print((new StringBuilder()).append(": ").append(e.getMessage()).toString());
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

	public static String toString(String msg, Throwable e)
	{
		UnsafeStringWriter w;
		PrintWriter p;
		w = new UnsafeStringWriter();
		w.write((new StringBuilder()).append(msg).append("\n").toString());
		p = new PrintWriter(w);
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

	public static String translat(String src, String from, String to)
	{
		if (isEmpty(src))
			return src;
		StringBuilder sb = null;
		int i = 0;
		for (int len = src.length(); i < len; i++)
		{
			char c = src.charAt(i);
			int ix = from.indexOf(c);
			if (ix == -1)
			{
				if (sb != null)
					sb.append(c);
				continue;
			}
			if (sb == null)
			{
				sb = new StringBuilder(len);
				sb.append(src, 0, i);
			}
			if (ix < to.length())
				sb.append(to.charAt(ix));
		}

		return sb != null ? sb.toString() : src;
	}

	public static String[] split(String str, char ch)
	{
		List list = null;
		int ix = 0;
		int len = str.length();
		for (int i = 0; i < len; i++)
		{
			char c = str.charAt(i);
			if (c != ch)
				continue;
			if (list == null)
				list = new ArrayList();
			list.add(str.substring(ix, i));
			ix = i + 1;
		}

		if (ix > 0)
			list.add(str.substring(ix));
		return list != null ? (String[])(String[])list.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
	}

	public static String join(String array[])
	{
		if (array.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		String arr$[] = array;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String s = arr$[i$];
			sb.append(s);
		}

		return sb.toString();
	}

	public static String join(String array[], char split)
	{
		if (array.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
				sb.append(split);
			sb.append(array[i]);
		}

		return sb.toString();
	}

	public static String join(String array[], String split)
	{
		if (array.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++)
		{
			if (i > 0)
				sb.append(split);
			sb.append(array[i]);
		}

		return sb.toString();
	}

	public static String join(Collection coll, String split)
	{
		if (coll.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		String s;
		for (Iterator i$ = coll.iterator(); i$.hasNext(); sb.append(s))
		{
			s = (String)i$.next();
			if (isFirst)
				isFirst = false;
			else
				sb.append(split);
		}

		return sb.toString();
	}

	private static Map parseKeyValuePair(String str, String itemSeparator)
	{
		String tmp[] = str.split(itemSeparator);
		Map map = new HashMap(tmp.length);
		for (int i = 0; i < tmp.length; i++)
		{
			Matcher matcher = KVP_PATTERN.matcher(tmp[i]);
			if (matcher.matches())
				map.put(matcher.group(1), matcher.group(2));
		}

		return map;
	}

	public static String getQueryStringValue(String qs, String key)
	{
		Map map = parseQueryString(qs);
		return (String)map.get(key);
	}

	public static Map parseQueryString(String qs)
	{
		if (qs == null || qs.length() == 0)
			return new HashMap();
		else
			return parseKeyValuePair(qs, "\\&");
	}

	public static String getServiceKey(Map ps)
	{
		StringBuilder buf = new StringBuilder();
		String group = (String)ps.get("group");
		if (group != null && group.length() > 0)
			buf.append(group).append("/");
		buf.append((String)ps.get("interface"));
		String version = (String)ps.get("version");
		if (version != null && version.length() > 0)
			buf.append(":").append(version);
		return buf.toString();
	}

	public static String toQueryString(Map ps)
	{
		StringBuilder buf = new StringBuilder();
		if (ps != null && ps.size() > 0)
		{
			Iterator i$ = (new TreeMap(ps)).entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				if (key != null && key.length() > 0 && value != null && value.length() > 0)
				{
					if (buf.length() > 0)
						buf.append("&");
					buf.append(key);
					buf.append("=");
					buf.append(value);
				}
			} while (true);
		}
		return buf.toString();
	}

	public static String camelToSplitName(String camelName, String split)
	{
		if (camelName == null || camelName.length() == 0)
			return camelName;
		StringBuilder buf = null;
		for (int i = 0; i < camelName.length(); i++)
		{
			char ch = camelName.charAt(i);
			if (ch >= 'A' && ch <= 'Z')
			{
				if (buf == null)
				{
					buf = new StringBuilder();
					if (i > 0)
						buf.append(camelName.substring(0, i));
				}
				if (i > 0)
					buf.append(split);
				buf.append(Character.toLowerCase(ch));
				continue;
			}
			if (buf != null)
				buf.append(ch);
		}

		return buf != null ? buf.toString() : camelName;
	}

	public static String toArgumentString(Object args[])
	{
		StringBuilder buf = new StringBuilder();
		Object arr$[] = args;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Object arg = arr$[i$];
			if (buf.length() > 0)
				buf.append(",");
			if (arg == null || ReflectUtils.isPrimitives(arg.getClass()))
			{
				buf.append(arg);
				continue;
			}
			try
			{
				buf.append(JSON.json(arg));
				continue;
			}
			catch (IOException e)
			{
				logger.warn(e.getMessage(), e);
			}
			buf.append(arg);
		}

		return buf.toString();
	}

	private StringUtils()
	{
	}

}
