// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegistriesPageHandler.java

package com.autohome.turbo.registry.pages;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import com.autohome.turbo.registry.Registry;
import com.autohome.turbo.registry.support.AbstractRegistry;
import com.autohome.turbo.registry.support.AbstractRegistryFactory;
import java.util.*;

public class RegistriesPageHandler
	implements PageHandler
{

	public RegistriesPageHandler()
	{
	}

	public Page handle(URL url)
	{
		List rows = new ArrayList();
		Collection registries = AbstractRegistryFactory.getRegistries();
		int registeredCount = 0;
		int subscribedCount = 0;
		if (registries != null && registries.size() > 0)
		{
			List row;
			for (Iterator i$ = registries.iterator(); i$.hasNext(); rows.add(row))
			{
				Registry registry = (Registry)i$.next();
				String server = registry.getUrl().getAddress();
				row = new ArrayList();
				row.add((new StringBuilder()).append(NetUtils.getHostName(server)).append("/").append(server).toString());
				if (registry.isAvailable())
					row.add("<font color=\"green\">Connected</font>");
				else
					row.add("<font color=\"red\">Disconnected</font>");
				int registeredSize = 0;
				int subscribedSize = 0;
				if (registry instanceof AbstractRegistry)
				{
					registeredSize = ((AbstractRegistry)registry).getRegistered().size();
					registeredCount += registeredSize;
					subscribedSize = ((AbstractRegistry)registry).getSubscribed().size();
					subscribedCount += subscribedSize;
				}
				row.add((new StringBuilder()).append("<a href=\"registered.html?registry=").append(server).append("\">Registered(").append(registeredSize).append(")</a>").toString());
				row.add((new StringBuilder()).append("<a href=\"subscribed.html?registry=").append(server).append("\">Subscribed(").append(subscribedSize).append(")</a>").toString());
			}

		}
		return new Page("Registries", (new StringBuilder()).append("Registries (").append(rows.size()).append(")").toString(), new String[] {
			"Registry Address:", "Status", (new StringBuilder()).append("Registered(").append(registeredCount).append(")").toString(), (new StringBuilder()).append("Subscribed(").append(subscribedCount).append(")").toString()
		}, rows);
	}
}
