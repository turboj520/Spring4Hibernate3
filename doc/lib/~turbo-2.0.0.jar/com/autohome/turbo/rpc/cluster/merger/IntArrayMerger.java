// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IntArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;

public class IntArrayMerger
	implements Merger
{

	public IntArrayMerger()
	{
	}

	public transient int[] merge(int items[][])
	{
		int totalLen = 0;
		int arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			int item[] = arr$[i$];
			totalLen += item.length;
		}

		int result[] = new int[totalLen];
		int index = 0;
		int arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			int item[] = arr$[i$];
			int arr$[] = item;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				int i = arr$[i$];
				result[index++] = i;
			}

		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((int[][])x0);
	}
}
