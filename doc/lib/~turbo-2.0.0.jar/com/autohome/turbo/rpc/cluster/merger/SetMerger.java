// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SetMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;
import java.util.HashSet;
import java.util.Set;

public class SetMerger
	implements Merger
{

	public SetMerger()
	{
	}

	public transient Set merge(Set items[])
	{
		Set result = new HashSet();
		Set arr$[] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Set item = arr$[i$];
			if (item != null)
				result.addAll(item);
		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((Set[])x0);
	}
}
