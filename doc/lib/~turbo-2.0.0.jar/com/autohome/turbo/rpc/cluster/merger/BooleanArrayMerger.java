// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BooleanArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;

public class BooleanArrayMerger
	implements Merger
{

	public BooleanArrayMerger()
	{
	}

	public transient boolean[] merge(boolean items[][])
	{
		int totalLen = 0;
		boolean arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			boolean array[] = arr$[i$];
			totalLen += array.length;
		}

		boolean result[] = new boolean[totalLen];
		int index = 0;
		boolean arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			boolean array[] = arr$[i$];
			boolean arr$[] = array;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				boolean item = arr$[i$];
				result[index++] = item;
			}

		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((boolean[][])x0);
	}
}
