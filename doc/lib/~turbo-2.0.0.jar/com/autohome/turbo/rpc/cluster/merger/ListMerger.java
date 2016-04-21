// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ListMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;
import java.util.ArrayList;
import java.util.List;

public class ListMerger
	implements Merger
{

	public ListMerger()
	{
	}

	public transient List merge(List items[])
	{
		List result = new ArrayList();
		List arr$[] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			List item = arr$[i$];
			if (item != null)
				result.addAll(item);
		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((List[])x0);
	}
}
