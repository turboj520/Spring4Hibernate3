// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ShortArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;

public class ShortArrayMerger
	implements Merger
{

	public ShortArrayMerger()
	{
	}

	public transient short[] merge(short items[][])
	{
		int total = 0;
		short arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			short array[] = arr$[i$];
			total += array.length;
		}

		short result[] = new short[total];
		int index = 0;
		short arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			short array[] = arr$[i$];
			short arr$[] = array;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				short item = arr$[i$];
				result[index++] = item;
			}

		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((short[][])x0);
	}
}
