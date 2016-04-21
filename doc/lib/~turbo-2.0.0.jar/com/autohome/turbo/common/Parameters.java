// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Parameters.java

package com.autohome.turbo.common;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @deprecated Class Parameters is deprecated
 */

public class Parameters
{

	private final Map parameters;
	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/Parameters);

	public transient Parameters(String pairs[])
	{
		this(toMap(pairs));
	}

	public Parameters(Map parameters)
	{
		this.parameters = Collections.unmodifiableMap(parameters == null ? ((Map) (new HashMap(0))) : ((Map) (new HashMap(parameters))));
	}

	private static transient Map toMap(String pairs[])
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

	public Map getParameters()
	{
		return parameters;
	}

	public Object getExtension(Class type, String key)
	{
		String name = getParameter(key);
		return ExtensionLoader.getExtensionLoader(type).getExtension(name);
	}

	public Object getExtension(Class type, String key, String defaultValue)
	{
		String name = getParameter(key, defaultValue);
		return ExtensionLoader.getExtensionLoader(type).getExtension(name);
	}

	public Object getMethodExtension(Class type, String method, String key)
	{
		String name = getMethodParameter(method, key);
		return ExtensionLoader.getExtensionLoader(type).getExtension(name);
	}

	public Object getMethodExtension(Class type, String method, String key, String defaultValue)
	{
		String name = getMethodParameter(method, key, defaultValue);
		return ExtensionLoader.getExtensionLoader(type).getExtension(name);
	}

	public String getDecodedParameter(String key)
	{
		return getDecodedParameter(key, null);
	}

	public String getDecodedParameter(String key, String defaultValue)
	{
		String value = getParameter(key, defaultValue);
		if (value != null && value.length() > 0)
			try
			{
				value = URLDecoder.decode(value, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				logger.error(e.getMessage(), e);
			}
		return value;
	}

	public String getParameter(String key)
	{
		String value = (String)parameters.get(key);
		if (value == null || value.length() == 0)
			value = (String)parameters.get((new StringBuilder()).append(".").append(key).toString());
		if (value == null || value.length() == 0)
			value = (String)parameters.get((new StringBuilder()).append("default.").append(key).toString());
		if (value == null || value.length() == 0)
			value = (String)parameters.get((new StringBuilder()).append(".default.").append(key).toString());
		return value;
	}

	public String getParameter(String key, String defaultValue)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value;
	}

	public int getIntParameter(String key)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return 0;
		else
			return Integer.parseInt(value);
	}

	public int getIntParameter(String key, int defaultValue)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Integer.parseInt(value);
	}

	public int getPositiveIntParameter(String key, int defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		int i = Integer.parseInt(value);
		if (i > 0)
			return i;
		else
			return defaultValue;
	}

	public boolean getBooleanParameter(String key)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return false;
		else
			return Boolean.parseBoolean(value);
	}

	public boolean getBooleanParameter(String key, boolean defaultValue)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}

	public boolean hasParamter(String key)
	{
		String value = getParameter(key);
		return value != null && value.length() > 0;
	}

	public String getMethodParameter(String method, String key)
	{
		String value = (String)parameters.get((new StringBuilder()).append(method).append(".").append(key).toString());
		if (value == null || value.length() == 0)
			value = (String)parameters.get((new StringBuilder()).append(".").append(method).append(".").append(key).toString());
		if (value == null || value.length() == 0)
			return getParameter(key);
		else
			return value;
	}

	public String getMethodParameter(String method, String key, String defaultValue)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value;
	}

	public int getMethodIntParameter(String method, String key)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return 0;
		else
			return Integer.parseInt(value);
	}

	public int getMethodIntParameter(String method, String key, int defaultValue)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Integer.parseInt(value);
	}

	public int getMethodPositiveIntParameter(String method, String key, int defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		int i = Integer.parseInt(value);
		if (i > 0)
			return i;
		else
			return defaultValue;
	}

	public boolean getMethodBooleanParameter(String method, String key)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return false;
		else
			return Boolean.parseBoolean(value);
	}

	public boolean getMethodBooleanParameter(String method, String key, boolean defaultValue)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}

	public boolean hasMethodParamter(String method, String key)
	{
		String value = getMethodParameter(method, key);
		return value != null && value.length() > 0;
	}

	public static Parameters parseParameters(String query)
	{
		return new Parameters(StringUtils.parseQueryString(query));
	}

	public boolean equals(Object o)
	{
		return parameters.equals(o);
	}

	public int hashCode()
	{
		return parameters.hashCode();
	}

	public String toString()
	{
		return StringUtils.toQueryString(getParameters());
	}

}
