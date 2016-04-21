// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StatusUtils.java

package com.autohome.turbo.common.status.support;

import com.autohome.turbo.common.status.Status;
import java.util.*;

public class StatusUtils
{

	public StatusUtils()
	{
	}

	public static Status getSummaryStatus(Map statuses)
	{
		com.autohome.turbo.common.status.Status.Level level = com.autohome.turbo.common.status.Status.Level.OK;
		StringBuilder msg = new StringBuilder();
		Iterator i$ = statuses.entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String key = (String)entry.getKey();
			Status status = (Status)entry.getValue();
			com.autohome.turbo.common.status.Status.Level l = status.getLevel();
			if (com.autohome.turbo.common.status.Status.Level.ERROR.equals(l))
			{
				level = com.autohome.turbo.common.status.Status.Level.ERROR;
				if (msg.length() > 0)
					msg.append(",");
				msg.append(key);
			} else
			if (com.autohome.turbo.common.status.Status.Level.WARN.equals(l))
			{
				if (!com.autohome.turbo.common.status.Status.Level.ERROR.equals(level))
					level = com.autohome.turbo.common.status.Status.Level.WARN;
				if (msg.length() > 0)
					msg.append(",");
				msg.append(key);
			}
		} while (true);
		return new Status(level, msg.toString());
	}
}
