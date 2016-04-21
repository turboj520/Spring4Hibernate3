// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractMonitorFactory.java

package com.autohome.turbo.monitor.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.monitor.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractMonitorFactory
	implements MonitorFactory
{

	private static final ReentrantLock LOCK = new ReentrantLock();
	private static final Map MONITORS = new ConcurrentHashMap();

	public AbstractMonitorFactory()
	{
	}

	public static Collection getMonitors()
	{
		return Collections.unmodifiableCollection(MONITORS.values());
	}

	public Monitor getMonitor(URL url)
	{
		String key;
		url = url.setPath(com/autohome/turbo/monitor/MonitorService.getName()).addParameter("interface", com/autohome/turbo/monitor/MonitorService.getName());
		key = url.toServiceString();
		LOCK.lock();
		Monitor monitor1;
		Monitor monitor = (Monitor)MONITORS.get(key);
		if (monitor == null)
			break MISSING_BLOCK_LABEL_62;
		monitor1 = monitor;
		LOCK.unlock();
		return monitor1;
		Monitor monitor = createMonitor(url);
		if (monitor == null)
			throw new IllegalStateException((new StringBuilder()).append("Can not create monitor ").append(url).toString());
		MONITORS.put(key, monitor);
		monitor1 = monitor;
		LOCK.unlock();
		return monitor1;
		Exception exception;
		exception;
		LOCK.unlock();
		throw exception;
	}

	protected abstract Monitor createMonitor(URL url);

}
