// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpServer.java

package com.autohome.turbo.remoting.http;

import com.autohome.turbo.common.Resetable;
import com.autohome.turbo.common.URL;
import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting.http:
//			HttpHandler

public interface HttpServer
	extends Resetable
{

	public abstract HttpHandler getHttpHandler();

	public abstract URL getUrl();

	public abstract InetSocketAddress getLocalAddress();

	public abstract void close();

	public abstract void close(int i);

	public abstract boolean isBound();

	public abstract boolean isClosed();
}
