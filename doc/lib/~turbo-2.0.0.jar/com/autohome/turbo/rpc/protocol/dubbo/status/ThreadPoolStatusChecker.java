// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThreadPoolStatusChecker.java

package com.autohome.turbo.rpc.protocol.dubbo.status;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.common.store.DataStore;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolStatusChecker
	implements StatusChecker
{

	public ThreadPoolStatusChecker()
	{
	}

	public Status check()
	{
		DataStore dataStore = (DataStore)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/store/DataStore).getDefaultExtension();
		Map executors = dataStore.get(Constants.EXECUTOR_SERVICE_COMPONENT_KEY);
		StringBuilder msg = new StringBuilder();
		com.autohome.turbo.common.status.Status.Level level = com.autohome.turbo.common.status.Status.Level.OK;
		Iterator i$ = executors.entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String port = (String)entry.getKey();
			ExecutorService executor = (ExecutorService)entry.getValue();
			if (executor != null && (executor instanceof ThreadPoolExecutor))
			{
				ThreadPoolExecutor tp = (ThreadPoolExecutor)executor;
				boolean ok = tp.getActiveCount() < tp.getMaximumPoolSize() - 1;
				com.autohome.turbo.common.status.Status.Level lvl = com.autohome.turbo.common.status.Status.Level.OK;
				if (!ok)
				{
					level = com.autohome.turbo.common.status.Status.Level.WARN;
					lvl = com.autohome.turbo.common.status.Status.Level.WARN;
				}
				if (msg.length() > 0)
					msg.append(";");
				msg.append((new StringBuilder()).append("Pool status:").append(lvl).append(", max:").append(tp.getMaximumPoolSize()).append(", core:").append(tp.getCorePoolSize()).append(", largest:").append(tp.getLargestPoolSize()).append(", active:").append(tp.getActiveCount()).append(", task:").append(tp.getTaskCount()).append(", service port: ").append(port).toString());
			}
		} while (true);
		return msg.length() != 0 ? new Status(level, msg.toString()) : new Status(com.autohome.turbo.common.status.Status.Level.UNKNOWN);
	}
}
