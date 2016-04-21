// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegistryStatusChecker.java

package com.autohome.turbo.registry.status;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.registry.Registry;
import com.autohome.turbo.registry.support.AbstractRegistryFactory;
import java.util.Collection;
import java.util.Iterator;

public class RegistryStatusChecker
	implements StatusChecker
{

	public RegistryStatusChecker()
	{
	}

	public Status check()
	{
		Collection regsitries = AbstractRegistryFactory.getRegistries();
		if (regsitries == null || regsitries.size() == 0)
			return new Status(com.autohome.turbo.common.status.Status.Level.UNKNOWN);
		com.autohome.turbo.common.status.Status.Level level = com.autohome.turbo.common.status.Status.Level.OK;
		StringBuilder buf = new StringBuilder();
		for (Iterator i$ = regsitries.iterator(); i$.hasNext();)
		{
			Registry registry = (Registry)i$.next();
			if (buf.length() > 0)
				buf.append(",");
			buf.append(registry.getUrl().getAddress());
			if (!registry.isAvailable())
			{
				level = com.autohome.turbo.common.status.Status.Level.ERROR;
				buf.append("(disconnected)");
			} else
			{
				buf.append("(connected)");
			}
		}

		return new Status(level, buf.toString());
	}
}
