// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONObject.java

package com.autohome.turbo.common.json;

import java.io.IOException;
import java.util.*;

// Referenced classes of package com.autohome.turbo.common.json:
//			JSONArray, JSONNode, JSONConverter, JSONWriter

public class JSONObject
	implements JSONNode
{

	private Map mMap;

	public JSONObject()
	{
		mMap = new HashMap();
	}

	public Object get(String key)
	{
		return mMap.get(key);
	}

	public boolean getBoolean(String key, boolean def)
	{
		Object tmp = mMap.get(key);
		return tmp == null || !(tmp instanceof Boolean) ? def : ((Boolean)tmp).booleanValue();
	}

	public int getInt(String key, int def)
	{
		Object tmp = mMap.get(key);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).intValue();
	}

	public long getLong(String key, long def)
	{
		Object tmp = mMap.get(key);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).longValue();
	}

	public float getFloat(String key, float def)
	{
		Object tmp = mMap.get(key);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).floatValue();
	}

	public double getDouble(String key, double def)
	{
		Object tmp = mMap.get(key);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).doubleValue();
	}

	public String getString(String key)
	{
		Object tmp = mMap.get(key);
		return tmp != null ? tmp.toString() : null;
	}

	public JSONArray getArray(String key)
	{
		Object tmp = mMap.get(key);
		return tmp != null ? (tmp instanceof JSONArray) ? (JSONArray)tmp : null : null;
	}

	public JSONObject getObject(String key)
	{
		Object tmp = mMap.get(key);
		return tmp != null ? (tmp instanceof JSONObject) ? (JSONObject)tmp : null : null;
	}

	public Iterator keys()
	{
		return mMap.keySet().iterator();
	}

	public boolean contains(String key)
	{
		return mMap.containsKey(key);
	}

	public void put(String name, Object value)
	{
		mMap.put(name, value);
	}

	public void putAll(String names[], Object values[])
	{
		int i = 0;
		for (int len = Math.min(names.length, values.length); i < len; i++)
			mMap.put(names[i], values[i]);

	}

	public void putAll(Map map)
	{
		java.util.Map.Entry entry;
		for (Iterator i$ = map.entrySet().iterator(); i$.hasNext(); mMap.put(entry.getKey(), entry.getValue()))
			entry = (java.util.Map.Entry)i$.next();

	}

	public void writeJSON(JSONConverter jc, JSONWriter jb, boolean writeClass)
		throws IOException
	{
		jb.objectBegin();
		for (Iterator i$ = mMap.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String key = (String)entry.getKey();
			jb.objectItem(key);
			Object value = entry.getValue();
			if (value == null)
				jb.valueNull();
			else
				jc.writeValue(value, jb, writeClass);
		}

		jb.objectEnd();
	}
}
