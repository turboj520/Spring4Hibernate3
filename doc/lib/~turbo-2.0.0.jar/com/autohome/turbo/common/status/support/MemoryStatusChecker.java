// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MemoryStatusChecker.java

package com.autohome.turbo.common.status.support;

import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;

public class MemoryStatusChecker
	implements StatusChecker
{

	public MemoryStatusChecker()
	{
	}

	public Status check()
	{
		Runtime runtime = Runtime.getRuntime();
		long freeMemory = runtime.freeMemory();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		boolean ok = maxMemory - (totalMemory - freeMemory) > 2048L;
		String msg = (new StringBuilder()).append("max:").append(maxMemory / 1024L / 1024L).append("M,total:").append(totalMemory / 1024L / 1024L).append("M,used:").append(totalMemory / 1024L / 1024L - freeMemory / 1024L / 1024L).append("M,free:").append(freeMemory / 1024L / 1024L).append("M").toString();
		return new Status(ok ? com.autohome.turbo.common.status.Status.Level.OK : com.autohome.turbo.common.status.Status.Level.WARN, msg);
	}
}
