// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RegisteredPageHandler.java

package com.autohome.turbo.registry.pages;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import com.autohome.turbo.registry.Registry;
import com.autohome.turbo.registry.support.AbstractRegistry;
import com.autohome.turbo.registry.support.AbstractRegistryFactory;
import java.util.*;

public class RegisteredPageHandler
	implements PageHandler
{

	public RegisteredPageHandler()
	{
	}

	public Page handle(URL url)
	{
		String registryAddress = url.getParameter("registry", "");
		List rows = new ArrayList();
		Collection registries = AbstractRegistryFactory.getRegistries();
		StringBuilder select = new StringBuilder();
		Registry registry = null;
		if (registries != null && registries.size() > 0)
			if (registries.size() == 1)
			{
				registry = (Registry)registries.iterator().next();
				select.append((new StringBuilder()).append(" &gt; ").append(registry.getUrl().getAddress()).toString());
			} else
			{
				select.append(" &gt; <select onchange=\"window.location.href='registered.html?registry=' + this.value;\">");
				for (Iterator i$ = registries.iterator(); i$.hasNext(); select.append("</option>"))
				{
					Registry r = (Registry)i$.next();
					String sp = r.getUrl().getAddress();
					select.append("<option value=\">");
					select.append(sp);
					if ((registryAddress == null || registryAddress.length() == 0) && registry == null || registryAddress.equals(sp))
					{
						registry = r;
						select.append("\" selected=\"selected");
					}
					select.append("\">");
					select.append(sp);
				}

				select.append("</select>");
			}
		if (registry instanceof AbstractRegistry)
		{
			Set services = ((AbstractRegistry)registry).getRegistered();
			if (services != null && services.size() > 0)
			{
				List row;
				for (Iterator i$ = services.iterator(); i$.hasNext(); rows.add(row))
				{
					URL u = (URL)i$.next();
					row = new ArrayList();
					row.add(u.toFullString().replace("<", "&lt;").replace(">", "&gt;"));
				}

			}
		}
		return new Page((new StringBuilder()).append("<a href=\"registries.html\">Registries</a>").append(select.toString()).append(" &gt; Registered | <a href=\"subscribed.html?registry=").append(registryAddress).append("\">Subscribed</a>").toString(), (new StringBuilder()).append("Registered (").append(rows.size()).append(")").toString(), new String[] {
			"Provider URL:"
		}, rows);
	}
}
