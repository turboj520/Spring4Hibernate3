// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FileRouterFactory.java

package com.autohome.turbo.rpc.cluster.router.file;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.IOUtils;
import com.autohome.turbo.rpc.cluster.Router;
import com.autohome.turbo.rpc.cluster.RouterFactory;
import java.io.*;

public class FileRouterFactory
	implements RouterFactory
{

	public static final String NAME = "file";
	private RouterFactory routerFactory;

	public FileRouterFactory()
	{
	}

	public void setRouterFactory(RouterFactory routerFactory)
	{
		this.routerFactory = routerFactory;
	}

	public Router getRouter(URL url)
	{
		URL script;
		String protocol = url.getParameter("router", "script");
		String type = null;
		String path = url.getPath();
		if (path != null)
		{
			int i = path.lastIndexOf('.');
			if (i > 0)
				type = path.substring(i + 1);
		}
		String rule = IOUtils.read(new FileReader(new File(url.getAbsolutePath())));
		script = url.setProtocol(protocol).addParameter("type", type).addParameterAndEncoded("rule", rule);
		return routerFactory.getRouter(script);
		IOException e;
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}
}
