// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LeastActiveLoadBalance.java

package com.autohome.turbo.rpc.cluster.loadbalance;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import java.util.List;
import java.util.Random;

// Referenced classes of package com.autohome.turbo.rpc.cluster.loadbalance:
//			AbstractLoadBalance

public class LeastActiveLoadBalance extends AbstractLoadBalance
{

	public static final String NAME = "leastactive";
	private final Random random = new Random();

	public LeastActiveLoadBalance()
	{
	}

	protected Invoker doSelect(List invokers, URL url, Invocation invocation)
	{
		int length = invokers.size();
		int leastActive = -1;
		int leastCount = 0;
		int leastIndexs[] = new int[length];
		int totalWeight = 0;
		int firstWeight = 0;
		boolean sameWeight = true;
		for (int i = 0; i < length; i++)
		{
			Invoker invoker = (Invoker)invokers.get(i);
			int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive();
			int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), "weight", 100);
			if (leastActive == -1 || active < leastActive)
			{
				leastActive = active;
				leastCount = 1;
				leastIndexs[0] = i;
				totalWeight = weight;
				firstWeight = weight;
				sameWeight = true;
				continue;
			}
			if (active != leastActive)
				continue;
			leastIndexs[leastCount++] = i;
			totalWeight += weight;
			if (sameWeight && i > 0 && weight != firstWeight)
				sameWeight = false;
		}

		if (leastCount == 1)
			return (Invoker)invokers.get(leastIndexs[0]);
		if (!sameWeight && totalWeight > 0)
		{
			int offsetWeight = random.nextInt(totalWeight);
			for (int i = 0; i < leastCount; i++)
			{
				int leastIndex = leastIndexs[i];
				offsetWeight -= getWeight((Invoker)invokers.get(leastIndex), invocation);
				if (offsetWeight <= 0)
					return (Invoker)invokers.get(leastIndex);
			}

		}
		return (Invoker)invokers.get(leastIndexs[random.nextInt(leastCount)]);
	}
}
