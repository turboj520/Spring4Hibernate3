// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SimpleDataStore.java

package com.autohome.turbo.common.store.support;

import com.autohome.turbo.common.store.DataStore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleDataStore
	implements DataStore
{

	private ConcurrentMap data;

	public SimpleDataStore()
	{
		data = new ConcurrentHashMap();
	}

	public Map get(String componentName)
	{
		ConcurrentMap value = (ConcurrentMap)data.get(componentName);
		if (value == null)
			return new HashMap();
		else
			return new HashMap(value);
	}

	public Object get(String componentName, String key)
	{
		if (!data.containsKey(componentName))
			return null;
		else
			return ((ConcurrentMap)data.get(componentName)).get(key);
	}

	public void put(String componentName, String key, Object value)
	{
		Map componentData = (Map)data.get(componentName);
		if (null == componentData)
		{
			data.putIfAbsent(componentName, new ConcurrentHashMap());
			componentData = (Map)data.get(componentName);
		}
		componentData.put(key, value);
	}

	public void remove(String componentName, String key)
	{
		if (!data.containsKey(componentName))
		{
			return;
		} else
		{
			((ConcurrentMap)data.get(componentName)).remove(key);
			return;
		}
	}
}
