// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RandomLoadBalance.java

package com.autohome.turbo.rpc.cluster.loadbalance;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.Invoker;
import java.util.List;
import java.util.Random;

// Referenced classes of package com.autohome.turbo.rpc.cluster.loadbalance:
//			AbstractLoadBalance

public class RandomLoadBalance extends AbstractLoadBalance
{

	public static final String NAME = "random";
	private final Random random = new Random();

	public RandomLoadBalance()
	{
	}

	protected Invoker doSelect(List invokers, URL url, Invocation invocation)
	{
		int length = invokers.size();
		int totalWeight = 0;
		boolean sameWeight = true;
		for (int i = 0; i < length; i++)
		{
			int weight = getWeight((Invoker)invokers.get(i), invocation);
			totalWeight += weight;
			if (sameWeight && i > 0 && weight != getWeight((Invoker)invokers.get(i - 1), invocation))
				sameWeight = false;
		}

		if (totalWeight > 0 && !sameWeight)
		{
			int offset = random.nextInt(totalWeight);
			for (int i = 0; i < length; i++)
			{
				offset -= getWeight((Invoker)invokers.get(i), invocation);
				if (offset < 0)
					return (Invoker)invokers.get(i);
			}

		}
		return (Invoker)invokers.get(random.nextInt(length));
	}
}
