// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DefaultTPSLimiter.java

package com.autohome.turbo.rpc.filter.tps;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Referenced classes of package com.autohome.turbo.rpc.filter.tps:
//			StatItem, TPSLimiter

public class DefaultTPSLimiter
	implements TPSLimiter
{

	private final ConcurrentMap stats = new ConcurrentHashMap();

	public DefaultTPSLimiter()
	{
	}

	public boolean isAllowable(URL url, Invocation invocation)
	{
		int rate = url.getParameter("tps", -1);
		long interval = url.getParameter("tps.interval", 60000L);
		String serviceKey = url.getServiceKey();
		StatItem statItem;
		if (rate > 0)
		{
			statItem = (StatItem)stats.get(serviceKey);
			if (statItem == null)
			{
				stats.putIfAbsent(serviceKey, new StatItem(serviceKey, rate, interval));
				statItem = (StatItem)stats.get(serviceKey);
			}
			return statItem.isAllowable(url, invocation);
		}
		statItem = (StatItem)stats.get(serviceKey);
		if (statItem != null)
			stats.remove(serviceKey);
		return true;
	}
}
