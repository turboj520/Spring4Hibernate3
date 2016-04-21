// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpBinder.java

package com.autohome.turbo.remoting.http;

import com.autohome.turbo.common.URL;

// Referenced classes of package com.autohome.turbo.remoting.http:
//			HttpHandler, HttpServer

public interface HttpBinder
{

	public abstract HttpServer bind(URL url, HttpHandler httphandler);
}
