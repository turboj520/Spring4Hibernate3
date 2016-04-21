// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractHttpServer.java

package com.autohome.turbo.remoting.http.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.http.HttpHandler;
import com.autohome.turbo.remoting.http.HttpServer;
import java.net.InetSocketAddress;

public abstract class AbstractHttpServer
	implements HttpServer
{

	private final URL url;
	private final HttpHandler handler;
	private volatile boolean closed;

	public AbstractHttpServer(URL url, HttpHandler handler)
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		if (handler == null)
		{
			throw new IllegalArgumentException("handler == null");
		} else
		{
			this.url = url;
			this.handler = handler;
			return;
		}
	}

	public HttpHandler getHttpHandler()
	{
		return handler;
	}

	public URL getUrl()
	{
		return url;
	}

	public void reset(URL url1)
	{
	}

	public boolean isBound()
	{
		return true;
	}

	public InetSocketAddress getLocalAddress()
	{
		return url.toInetSocketAddress();
	}

	public void close()
	{
		closed = true;
	}

	public void close(int timeout)
	{
		close();
	}

	public boolean isClosed()
	{
		return closed;
	}
}
