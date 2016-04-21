// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LoadStatusChecker.java

package com.autohome.turbo.common.status.support;

import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

public class LoadStatusChecker
	implements StatusChecker
{

	public LoadStatusChecker()
	{
	}

	public Status check()
	{
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		double load;
		try
		{
			Method method = java/lang/management/OperatingSystemMXBean.getMethod("getSystemLoadAverage", new Class[0]);
			load = ((Double)method.invoke(operatingSystemMXBean, new Object[0])).doubleValue();
		}
		catch (Throwable e)
		{
			load = -1D;
		}
		int cpu = operatingSystemMXBean.getAvailableProcessors();
		return new Status(load >= 0.0D ? load >= (double)cpu ? com.autohome.turbo.common.status.Status.Level.WARN : com.autohome.turbo.common.status.Status.Level.OK : com.autohome.turbo.common.status.Status.Level.UNKNOWN, (new StringBuilder()).append(load >= 0.0D ? (new StringBuilder()).append("load:").append(load).append(",").toString() : "").append("cpu:").append(cpu).toString());
	}
}
