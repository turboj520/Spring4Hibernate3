// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractConfigurator.java

package com.autohome.turbo.rpc.cluster.configurator;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.Configurator;
import java.io.PrintStream;
import java.util.*;

public abstract class AbstractConfigurator
	implements Configurator
{

	private final URL configuratorUrl;

	public AbstractConfigurator(URL url)
	{
		if (url == null)
		{
			throw new IllegalArgumentException("configurator url == null");
		} else
		{
			configuratorUrl = url;
			return;
		}
	}

	public URL getUrl()
	{
		return configuratorUrl;
	}

	public URL configure(URL url)
	{
		if (configuratorUrl == null || configuratorUrl.getHost() == null || url == null || url.getHost() == null)
			return url;
		if ("0.0.0.0".equals(configuratorUrl.getHost()) || url.getHost().equals(configuratorUrl.getHost()))
		{
			String configApplication = configuratorUrl.getParameter("application", configuratorUrl.getUsername());
			String currentApplication = url.getParameter("application", url.getUsername());
			if ((configApplication == null || "*".equals(configApplication) || configApplication.equals(currentApplication)) && (configuratorUrl.getPort() == 0 || url.getPort() == configuratorUrl.getPort()))
			{
				Set condtionKeys = new HashSet();
				condtionKeys.add("category");
				condtionKeys.add("check");
				condtionKeys.add("dynamic");
				condtionKeys.add("enabled");
				for (Iterator i$ = configuratorUrl.getParameters().entrySet().iterator(); i$.hasNext();)
				{
					java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
					String key = (String)entry.getKey();
					String value = (String)entry.getValue();
					if (key.startsWith("~") || "application".equals(key) || "side".equals(key))
					{
						condtionKeys.add(key);
						if (value != null && !"*".equals(value) && !value.equals(url.getParameter(key.startsWith("~") ? key.substring(1) : key)))
							return url;
					}
				}

				return doConfigure(url, configuratorUrl.removeParameters(condtionKeys));
			}
		}
		return url;
	}

	public int compareTo(Configurator o)
	{
		if (o == null)
			return -1;
		else
			return getUrl().getHost().compareTo(o.getUrl().getHost());
	}

	protected abstract URL doConfigure(URL url, URL url1);

	public static void main(String args[])
	{
		System.out.println(URL.encode("timeout=100"));
	}

	public volatile int compareTo(Object x0)
	{
		return compareTo((Configurator)x0);
	}
}
