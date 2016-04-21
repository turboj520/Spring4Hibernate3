// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONArray.java

package com.autohome.turbo.common.json;

import java.io.IOException;
import java.util.*;

// Referenced classes of package com.autohome.turbo.common.json:
//			JSONObject, JSONNode, JSONWriter, JSONConverter

public class JSONArray
	implements JSONNode
{

	private List mArray;

	public JSONArray()
	{
		mArray = new ArrayList();
	}

	public Object get(int index)
	{
		return mArray.get(index);
	}

	public boolean getBoolean(int index, boolean def)
	{
		Object tmp = mArray.get(index);
		return tmp == null || !(tmp instanceof Boolean) ? def : ((Boolean)tmp).booleanValue();
	}

	public int getInt(int index, int def)
	{
		Object tmp = mArray.get(index);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).intValue();
	}

	public long getLong(int index, long def)
	{
		Object tmp = mArray.get(index);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).longValue();
	}

	public float getFloat(int index, float def)
	{
		Object tmp = mArray.get(index);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).floatValue();
	}

	public double getDouble(int index, double def)
	{
		Object tmp = mArray.get(index);
		return tmp == null || !(tmp instanceof Number) ? def : ((Number)tmp).doubleValue();
	}

	public String getString(int index)
	{
		Object tmp = mArray.get(index);
		return tmp != null ? tmp.toString() : null;
	}

	public JSONArray getArray(int index)
	{
		Object tmp = mArray.get(index);
		return tmp != null ? (tmp instanceof JSONArray) ? (JSONArray)tmp : null : null;
	}

	public JSONObject getObject(int index)
	{
		Object tmp = mArray.get(index);
		return tmp != null ? (tmp instanceof JSONObject) ? (JSONObject)tmp : null : null;
	}

	public int length()
	{
		return mArray.size();
	}

	public void add(Object ele)
	{
		mArray.add(ele);
	}

	public void addAll(Object eles[])
	{
		Object arr$[] = eles;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Object ele = arr$[i$];
			mArray.add(ele);
		}

	}

	public void addAll(Collection c)
	{
		mArray.addAll(c);
	}

	public void writeJSON(JSONConverter jc, JSONWriter jb, boolean writeClass)
		throws IOException
	{
		jb.arrayBegin();
		for (Iterator i$ = mArray.iterator(); i$.hasNext();)
		{
			Object item = i$.next();
			if (item == null)
				jb.valueNull();
			else
				jc.writeValue(item, jb, writeClass);
		}

		jb.arrayEnd();
	}
}
