// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ByteArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;

public class ByteArrayMerger
	implements Merger
{

	public ByteArrayMerger()
	{
	}

	public transient byte[] merge(byte items[][])
	{
		int total = 0;
		byte arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			byte array[] = arr$[i$];
			total += array.length;
		}

		byte result[] = new byte[total];
		int index = 0;
		byte arr$[][] = items;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			byte array[] = arr$[i$];
			byte arr$[] = array;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				byte item = arr$[i$];
				result[index++] = item;
			}

		}

		return result;
	}

	public volatile Object merge(Object x0[])
	{
		return merge((byte[][])x0);
	}
}
