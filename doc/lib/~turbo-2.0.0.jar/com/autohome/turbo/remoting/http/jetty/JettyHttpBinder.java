// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JettyHttpBinder.java

package com.autohome.turbo.remoting.http.jetty;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.http.*;

// Referenced classes of package com.autohome.turbo.remoting.http.jetty:
//			JettyHttpServer

public class JettyHttpBinder
	implements HttpBinder
{

	public JettyHttpBinder()
	{
	}

	public HttpServer bind(URL url, HttpHandler handler)
	{
		return new JettyHttpServer(url, handler);
	}
}
