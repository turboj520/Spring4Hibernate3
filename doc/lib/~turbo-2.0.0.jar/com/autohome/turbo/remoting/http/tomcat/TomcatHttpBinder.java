// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TomcatHttpBinder.java

package com.autohome.turbo.remoting.http.tomcat;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.http.*;

// Referenced classes of package com.autohome.turbo.remoting.http.tomcat:
//			TomcatHttpServer

public class TomcatHttpBinder
	implements HttpBinder
{

	public TomcatHttpBinder()
	{
	}

	public HttpServer bind(URL url, HttpHandler handler)
	{
		return new TomcatHttpServer(url, handler);
	}
}
