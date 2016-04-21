// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CharArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;

public class CharArrayMerger
	implements Merger
{

	public CharArrayMerger()
	{
	}

	public transient char[] merge(char items[][])
	{
		int total = 0;
		char arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			char array[] = arr$[i$];
			total += array.length;
		}

		char result[] = new char[total];
		int index = 0;
		char arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			char array[] = arr$[i$];
			char arr$[] = array;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				char item = arr$[i$];
				result[index++] = item;
			}

		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((char[][])x0);
	}
}
