// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServletHttpBinder.java

package com.autohome.turbo.remoting.http.servlet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.http.*;

// Referenced classes of package com.autohome.turbo.remoting.http.servlet:
//			ServletHttpServer

public class ServletHttpBinder
	implements HttpBinder
{

	public ServletHttpBinder()
	{
	}

	public HttpServer bind(URL url, HttpHandler handler)
	{
		return new ServletHttpServer(url, handler);
	}
}
