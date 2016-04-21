// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HomePageHandler.java

package com.autohome.turbo.container.page.pages;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.container.page.*;
import java.util.*;

public class HomePageHandler
	implements PageHandler
{

	public HomePageHandler()
	{
	}

	public Page handle(URL url)
	{
		List rows = new ArrayList();
		List row;
		for (Iterator i$ = PageServlet.getInstance().getMenus().iterator(); i$.hasNext(); rows.add(row))
		{
			PageHandler handler = (PageHandler)i$.next();
			String uri = ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/page/PageHandler).getExtensionName(handler);
			Menu menu = (Menu)handler.getClass().getAnnotation(com/autohome/turbo/container/page/Menu);
			row = new ArrayList();
			row.add((new StringBuilder()).append("<a href=\"").append(uri).append(".html\">").append(menu.name()).append("</a>").toString());
			row.add(menu.desc());
		}

		return new Page("Home", "Menus", new String[] {
			"Menu Name", "Menu Desc"
		}, rows);
	}
}
