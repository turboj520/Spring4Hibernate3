// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StatusPageHandler.java

package com.autohome.turbo.container.page.pages;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.common.status.support.StatusUtils;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import java.util.*;

public class StatusPageHandler
	implements PageHandler
{

	public StatusPageHandler()
	{
	}

	public Page handle(URL url)
	{
		List rows = new ArrayList();
		Set names = ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/status/StatusChecker).getSupportedExtensions();
		Map statuses = new HashMap();
		Iterator i$ = names.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String name = (String)i$.next();
			StatusChecker checker = (StatusChecker)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/status/StatusChecker).getExtension(name);
			List row = new ArrayList();
			row.add(name);
			Status status = checker.check();
			if (status != null && !com.autohome.turbo.common.status.Status.Level.UNKNOWN.equals(status.getLevel()))
			{
				statuses.put(name, status);
				row.add(getLevelHtml(status.getLevel()));
				row.add(status.getMessage());
				rows.add(row);
			}
		} while (true);
		Status status = StatusUtils.getSummaryStatus(statuses);
		if ("status".equals(url.getPath()))
		{
			return new Page("", "", "", status.getLevel().toString());
		} else
		{
			List row = new ArrayList();
			row.add("summary");
			row.add(getLevelHtml(status.getLevel()));
			row.add("<a href=\"/status\" target=\"_blank\">summary</a>");
			rows.add(row);
			return new Page("Status (<a href=\"/status\" target=\"_blank\">summary</a>)", "Status", new String[] {
				"Name", "Status", "Description"
			}, rows);
		}
	}

	private String getLevelHtml(com.autohome.turbo.common.status.Status.Level level)
	{
		return (new StringBuilder()).append("<font color=\"").append(getLevelColor(level)).append("\">").append(level.name()).append("</font>").toString();
	}

	private String getLevelColor(com.autohome.turbo.common.status.Status.Level level)
	{
		if (level == com.autohome.turbo.common.status.Status.Level.OK)
			return "green";
		if (level == com.autohome.turbo.common.status.Status.Level.ERROR)
			return "red";
		if (level == com.autohome.turbo.common.status.Status.Level.WARN)
			return "yellow";
		else
			return "gray";
	}
}
