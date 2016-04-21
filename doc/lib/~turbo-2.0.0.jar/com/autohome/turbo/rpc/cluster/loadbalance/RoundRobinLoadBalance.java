// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RoundRobinLoadBalance.java

package com.autohome.turbo.rpc.cluster.loadbalance;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.AtomicPositiveInteger;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.Invoker;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Referenced classes of package com.autohome.turbo.rpc.cluster.loadbalance:
//			AbstractLoadBalance

public class RoundRobinLoadBalance extends AbstractLoadBalance
{

	public static final String NAME = "roundrobin";
	private final ConcurrentMap sequences = new ConcurrentHashMap();
	private final ConcurrentMap weightSequences = new ConcurrentHashMap();

	public RoundRobinLoadBalance()
	{
	}

	protected Invoker doSelect(List invokers, URL url, Invocation invocation)
	{
		String key = (new StringBuilder()).append(((Invoker)invokers.get(0)).getUrl().getServiceKey()).append(".").append(invocation.getMethodName()).toString();
		int length = invokers.size();
		int maxWeight = 0;
		int minWeight = 0x7fffffff;
		for (int i = 0; i < length; i++)
		{
			int weight = getWeight((Invoker)invokers.get(i), invocation);
			maxWeight = Math.max(maxWeight, weight);
			minWeight = Math.min(minWeight, weight);
		}

		if (maxWeight > 0 && minWeight < maxWeight)
		{
			AtomicPositiveInteger weightSequence = (AtomicPositiveInteger)weightSequences.get(key);
			if (weightSequence == null)
			{
				weightSequences.putIfAbsent(key, new AtomicPositiveInteger());
				weightSequence = (AtomicPositiveInteger)weightSequences.get(key);
			}
			int currentWeight = weightSequence.getAndIncrement() % maxWeight;
			List weightInvokers = new ArrayList();
			Iterator i$ = invokers.iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Invoker invoker = (Invoker)i$.next();
				if (getWeight(invoker, invocation) > currentWeight)
					weightInvokers.add(invoker);
			} while (true);
			int weightLength = weightInvokers.size();
			if (weightLength == 1)
				return (Invoker)weightInvokers.get(0);
			if (weightLength > 1)
			{
				invokers = weightInvokers;
				length = invokers.size();
			}
		}
		AtomicPositiveInteger sequence = (AtomicPositiveInteger)sequences.get(key);
		if (sequence == null)
		{
			sequences.putIfAbsent(key, new AtomicPositiveInteger());
			sequence = (AtomicPositiveInteger)sequences.get(key);
		}
		return (Invoker)invokers.get(sequence.getAndIncrement() % length);
	}
}
