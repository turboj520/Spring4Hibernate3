// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RpcStatus.java

package com.autohome.turbo.rpc;

import com.autohome.turbo.common.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RpcStatus
{

	private static final ConcurrentMap SERVICE_STATISTICS = new ConcurrentHashMap();
	private static final ConcurrentMap METHOD_STATISTICS = new ConcurrentHashMap();
	private final ConcurrentMap values = new ConcurrentHashMap();
	private final AtomicInteger active = new AtomicInteger();
	private final AtomicLong total = new AtomicLong();
	private final AtomicInteger failed = new AtomicInteger();
	private final AtomicLong totalElapsed = new AtomicLong();
	private final AtomicLong failedElapsed = new AtomicLong();
	private final AtomicLong maxElapsed = new AtomicLong();
	private final AtomicLong failedMaxElapsed = new AtomicLong();
	private final AtomicLong succeededMaxElapsed = new AtomicLong();

	public static RpcStatus getStatus(URL url)
	{
		String uri = url.toIdentityString();
		RpcStatus status = (RpcStatus)SERVICE_STATISTICS.get(uri);
		if (status == null)
		{
			SERVICE_STATISTICS.putIfAbsent(uri, new RpcStatus());
			status = (RpcStatus)SERVICE_STATISTICS.get(uri);
		}
		return status;
	}

	public static void removeStatus(URL url)
	{
		String uri = url.toIdentityString();
		SERVICE_STATISTICS.remove(uri);
	}

	public static RpcStatus getStatus(URL url, String methodName)
	{
		String uri = url.toIdentityString();
		ConcurrentMap map = (ConcurrentMap)METHOD_STATISTICS.get(uri);
		if (map == null)
		{
			METHOD_STATISTICS.putIfAbsent(uri, new ConcurrentHashMap());
			map = (ConcurrentMap)METHOD_STATISTICS.get(uri);
		}
		RpcStatus status = (RpcStatus)map.get(methodName);
		if (status == null)
		{
			map.putIfAbsent(methodName, new RpcStatus());
			status = (RpcStatus)map.get(methodName);
		}
		return status;
	}

	public static void removeStatus(URL url, String methodName)
	{
		String uri = url.toIdentityString();
		ConcurrentMap map = (ConcurrentMap)METHOD_STATISTICS.get(uri);
		if (map != null)
			map.remove(methodName);
	}

	public static void beginCount(URL url, String methodName)
	{
		beginCount(getStatus(url));
		beginCount(getStatus(url, methodName));
	}

	private static void beginCount(RpcStatus status)
	{
		status.active.incrementAndGet();
	}

	public static void endCount(URL url, String methodName, long elapsed, boolean succeeded)
	{
		endCount(getStatus(url), elapsed, succeeded);
		endCount(getStatus(url, methodName), elapsed, succeeded);
	}

	private static void endCount(RpcStatus status, long elapsed, boolean succeeded)
	{
		status.active.decrementAndGet();
		status.total.incrementAndGet();
		status.totalElapsed.addAndGet(elapsed);
		if (status.maxElapsed.get() < elapsed)
			status.maxElapsed.set(elapsed);
		if (succeeded)
		{
			if (status.succeededMaxElapsed.get() < elapsed)
				status.succeededMaxElapsed.set(elapsed);
		} else
		{
			status.failed.incrementAndGet();
			status.failedElapsed.addAndGet(elapsed);
			if (status.failedMaxElapsed.get() < elapsed)
				status.failedMaxElapsed.set(elapsed);
		}
	}

	private RpcStatus()
	{
	}

	public void set(String key, Object value)
	{
		values.put(key, value);
	}

	public Object get(String key)
	{
		return values.get(key);
	}

	public int getActive()
	{
		return active.get();
	}

	public long getTotal()
	{
		return total.longValue();
	}

	public long getTotalElapsed()
	{
		return totalElapsed.get();
	}

	public long getAverageElapsed()
	{
		long total = getTotal();
		if (total == 0L)
			return 0L;
		else
			return getTotalElapsed() / total;
	}

	public long getMaxElapsed()
	{
		return maxElapsed.get();
	}

	public int getFailed()
	{
		return failed.get();
	}

	public long getFailedElapsed()
	{
		return failedElapsed.get();
	}

	public long getFailedAverageElapsed()
	{
		long failed = getFailed();
		if (failed == 0L)
			return 0L;
		else
			return getFailedElapsed() / failed;
	}

	public long getFailedMaxElapsed()
	{
		return failedMaxElapsed.get();
	}

	public long getSucceeded()
	{
		return getTotal() - (long)getFailed();
	}

	public long getSucceededElapsed()
	{
		return getTotalElapsed() - getFailedElapsed();
	}

	public long getSucceededAverageElapsed()
	{
		long succeeded = getSucceeded();
		if (succeeded == 0L)
			return 0L;
		else
			return getSucceededElapsed() / succeeded;
	}

	public long getSucceededMaxElapsed()
	{
		return succeededMaxElapsed.get();
	}

	public long getAverageTps()
	{
		if (getTotalElapsed() >= 1000L)
			return getTotal() / (getTotalElapsed() / 1000L);
		else
			return getTotal();
	}

}
