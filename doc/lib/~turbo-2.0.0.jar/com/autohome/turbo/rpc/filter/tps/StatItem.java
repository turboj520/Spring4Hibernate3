// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StatItem.java

package com.autohome.turbo.rpc.filter.tps;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;
import java.util.concurrent.atomic.AtomicInteger;

class StatItem
{

	private String name;
	private long lastResetTime;
	private long interval;
	private AtomicInteger token;
	private int rate;

	StatItem(String name, int rate, long interval)
	{
		this.name = name;
		this.rate = rate;
		this.interval = interval;
		lastResetTime = System.currentTimeMillis();
		token = new AtomicInteger(rate);
	}

	public boolean isAllowable(URL url, Invocation invocation)
	{
		long now = System.currentTimeMillis();
		if (now > lastResetTime + interval)
		{
			token.set(rate);
			lastResetTime = now;
		}
		int value = token.get();
		boolean flag;
		for (flag = false; value > 0 && !flag; value = token.get())
			flag = token.compareAndSet(value, value - 1);

		return flag;
	}

	long getLastResetTime()
	{
		return lastResetTime;
	}

	int getToken()
	{
		return token.get();
	}

	public String toString()
	{
		return (new StringBuilder(32)).append("StatItem ").append("[name=").append(name).append(", ").append("rate = ").append(rate).append(", ").append("interval = ").append(interval).append("]").toString();
	}
}
