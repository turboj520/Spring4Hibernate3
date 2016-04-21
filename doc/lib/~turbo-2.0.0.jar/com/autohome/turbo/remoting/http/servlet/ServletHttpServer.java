// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServletHttpServer.java

package com.autohome.turbo.remoting.http.servlet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.http.HttpHandler;
import com.autohome.turbo.remoting.http.support.AbstractHttpServer;

// Referenced classes of package com.autohome.turbo.remoting.http.servlet:
//			DispatcherServlet

public class ServletHttpServer extends AbstractHttpServer
{

	public ServletHttpServer(URL url, HttpHandler handler)
	{
		super(url, handler);
		DispatcherServlet.addHttpHandler(url.getPort(), handler);
	}
}
