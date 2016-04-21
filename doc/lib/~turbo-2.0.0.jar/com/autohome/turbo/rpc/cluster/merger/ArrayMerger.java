// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ArrayMerger.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.rpc.cluster.Merger;
import java.lang.reflect.Array;

public class ArrayMerger
	implements Merger
{

	public static final ArrayMerger INSTANCE = new ArrayMerger();

	public ArrayMerger()
	{
	}

	public transient Object[] merge(Object others[][])
	{
		if (others.length == 0)
			return null;
		int totalLen = 0;
		for (int i = 0; i < others.length; i++)
		{
			Object item = ((Object) (others[i]));
			if (item != null && item.getClass().isArray())
				totalLen += Array.getLength(item);
			else
				throw new IllegalArgumentException((new StringBuilder(32)).append(i + 1).append("th argument is not an array").toString());
		}

		if (totalLen == 0)
			return null;
		Class type = ((Object) (others[0])).getClass().getComponentType();
		Object result = Array.newInstance(type, totalLen);
		int index = 0;
		Object arr$[][] = others;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Object array = ((Object) (arr$[i$]));
			for (int i = 0; i < Array.getLength(array); i++)
				Array.set(result, index++, Array.get(array, i));

		}

		return (Object[])(Object[])result;
	}

	public volatile Object merge(Object x0[])
	{
		return ((Object) (merge((Object[][])x0)));
	}

}
