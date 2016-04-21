// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StaticContext.java

package com.autohome.turbo.rpc;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StaticContext extends ConcurrentHashMap
{

	private static final long serialVersionUID = 1L;
	private static final String SYSTEMNAME = "system";
	private String name;
	private static final ConcurrentMap context_map = new ConcurrentHashMap();

	private StaticContext(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public static StaticContext getSystemContext()
	{
		return getContext("system");
	}

	public static StaticContext getContext(String name)
	{
		StaticContext appContext = (StaticContext)context_map.get(name);
		if (appContext == null)
		{
			appContext = (StaticContext)context_map.putIfAbsent(name, new StaticContext(name));
			if (appContext == null)
				appContext = (StaticContext)context_map.get(name);
		}
		return appContext;
	}

	public static StaticContext remove(String name)
	{
		return (StaticContext)context_map.remove(name);
	}

	public static String getKey(URL url, String methodName, String suffix)
	{
		return getKey(url.getServiceKey(), methodName, suffix);
	}

	public static String getKey(Map paras, String methodName, String suffix)
	{
		return getKey(StringUtils.getServiceKey(paras), methodName, suffix);
	}

	private static String getKey(String servicekey, String methodName, String suffix)
	{
		StringBuffer sb = (new StringBuffer()).append(servicekey).append(".").append(methodName).append(".").append(suffix);
		return sb.toString();
	}

}
