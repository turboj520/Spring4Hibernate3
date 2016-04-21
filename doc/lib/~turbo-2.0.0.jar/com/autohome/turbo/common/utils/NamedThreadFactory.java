// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NamedThreadFactory.java

package com.autohome.turbo.common.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory
	implements ThreadFactory
{

	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);
	private final AtomicInteger mThreadNum;
	private final String mPrefix;
	private final boolean mDaemo;
	private final ThreadGroup mGroup;

	public NamedThreadFactory()
	{
		this((new StringBuilder()).append("pool-").append(POOL_SEQ.getAndIncrement()).toString(), false);
	}

	public NamedThreadFactory(String prefix)
	{
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean daemo)
	{
		mThreadNum = new AtomicInteger(1);
		mPrefix = (new StringBuilder()).append(prefix).append("-thread-").toString();
		mDaemo = daemo;
		SecurityManager s = System.getSecurityManager();
		mGroup = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	public Thread newThread(Runnable runnable)
	{
		String name = (new StringBuilder()).append(mPrefix).append(mThreadNum.getAndIncrement()).toString();
		Thread ret = new Thread(mGroup, runnable, name, 0L);
		ret.setDaemon(mDaemo);
		return ret;
	}

	public ThreadGroup getThreadGroup()
	{
		return mGroup;
	}

}
