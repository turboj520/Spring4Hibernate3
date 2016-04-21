// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MapMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;
import java.util.HashMap;
import java.util.Map;

public class MapMerger
	implements Merger
{

	public MapMerger()
	{
	}

	public transient Map merge(Map items[])
	{
		if (items.length == 0)
			return null;
		Map result = new HashMap();
		Map arr$[] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Map item = arr$[i$];
			if (item != null)
				result.putAll(item);
		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((Map[])x0);
	}
}
