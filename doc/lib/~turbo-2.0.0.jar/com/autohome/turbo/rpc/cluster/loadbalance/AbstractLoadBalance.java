// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractLoadBalance.java

package com.autohome.turbo.rpc.cluster.loadbalance;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.List;

public abstract class AbstractLoadBalance
	implements LoadBalance
{

	public AbstractLoadBalance()
	{
	}

	public Invoker select(List invokers, URL url, Invocation invocation)
	{
		if (invokers == null || invokers.size() == 0)
			return null;
		if (invokers.size() == 1)
			return (Invoker)invokers.get(0);
		else
			return doSelect(invokers, url, invocation);
	}

	protected abstract Invoker doSelect(List list, URL url, Invocation invocation);

	protected int getWeight(Invoker invoker, Invocation invocation)
	{
		int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), "weight", 100);
		if (weight > 0)
		{
			long timestamp = invoker.getUrl().getParameter("timestamp", 0L);
			if (timestamp > 0L)
			{
				int uptime = (int)(System.currentTimeMillis() - timestamp);
				int warmup = invoker.getUrl().getParameter("warmup", 0x927c0);
				if (uptime > 0 && uptime < warmup)
					weight = calculateWarmupWeight(uptime, warmup, weight);
			}
		}
		return weight;
	}

	static int calculateWarmupWeight(int uptime, int warmup, int weight)
	{
		int ww = (int)((float)uptime / ((float)warmup / (float)weight));
		return ww >= 1 ? ww <= weight ? ww : weight : 1;
	}
}
