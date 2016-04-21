// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LongArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;

public class LongArrayMerger
	implements Merger
{

	public LongArrayMerger()
	{
	}

	public transient long[] merge(long items[][])
	{
		int total = 0;
		long arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			long array[] = arr$[i$];
			total += array.length;
		}

		long result[] = new long[total];
		int index = 0;
		long arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			long array[] = arr$[i$];
			long arr$[] = array;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				long item = arr$[i$];
				result[index++] = item;
			}

		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((long[][])x0);
	}
}
