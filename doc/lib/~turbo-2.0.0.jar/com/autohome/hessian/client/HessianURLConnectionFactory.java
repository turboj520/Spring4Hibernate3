// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianURLConnectionFactory.java

package com.autohome.hessian.client;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.client:
//			HessianURLConnection, HessianConnectionFactory, HessianProxyFactory, HessianConnection

public class HessianURLConnectionFactory
	implements HessianConnectionFactory
{

	private static final Logger log = Logger.getLogger(com/autohome/hessian/client/HessianURLConnectionFactory.getName());
	private HessianProxyFactory _proxyFactory;

	public HessianURLConnectionFactory()
	{
	}

	public void setHessianProxyFactory(HessianProxyFactory factory)
	{
		_proxyFactory = factory;
	}

	public HessianConnection open(URL url)
		throws IOException
	{
		if (log.isLoggable(Level.FINER))
			log.finer((new StringBuilder()).append(this).append(" open(").append(url).append(")").toString());
		URLConnection conn = url.openConnection();
		long connectTimeout = _proxyFactory.getConnectTimeout();
		if (connectTimeout >= 0L)
			conn.setConnectTimeout((int)connectTimeout);
		conn.setDoOutput(true);
		long readTimeout = _proxyFactory.getReadTimeout();
		if (readTimeout > 0L)
			try
			{
				conn.setReadTimeout((int)readTimeout);
			}
			catch (Throwable e) { }
		return new HessianURLConnection(url, conn);
	}

	public HessianConnection open(URL url, Proxy proxy)
		throws IOException
	{
		if (log.isLoggable(Level.FINER))
			log.finer((new StringBuilder()).append(this).append(" open(").append(url).append(")").toString());
		URLConnection conn = url.openConnection(proxy);
		long connectTimeout = _proxyFactory.getConnectTimeout();
		if (connectTimeout >= 0L)
			conn.setConnectTimeout((int)connectTimeout);
		conn.setDoOutput(true);
		long readTimeout = _proxyFactory.getReadTimeout();
		if (readTimeout > 0L)
			try
			{
				conn.setReadTimeout((int)readTimeout);
			}
			catch (Throwable e) { }
		return new HessianURLConnection(url, conn);
	}

}
