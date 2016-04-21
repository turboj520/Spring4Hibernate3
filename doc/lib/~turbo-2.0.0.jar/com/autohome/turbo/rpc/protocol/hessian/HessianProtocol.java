// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianProtocol.java

package com.autohome.turbo.rpc.protocol.hessian;

import com.autohome.hessian.HessianException;
import com.autohome.hessian.client.HessianConnectionException;
import com.autohome.hessian.client.HessianProxyFactory;
import com.autohome.hessian.io.HessianMethodSerializationException;
import com.autohome.hessian.server.HessianSkeleton;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.http.HttpBinder;
import com.autohome.turbo.remoting.http.HttpHandler;
import com.autohome.turbo.remoting.http.HttpServer;
import com.autohome.turbo.rpc.RpcContext;
import com.autohome.turbo.rpc.RpcException;
import com.autohome.turbo.rpc.protocol.AbstractProxyProtocol;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HessianProtocol extends AbstractProxyProtocol
{
	private class HessianHandler
		implements HttpHandler
	{

		final HessianProtocol this$0;

		public void handle(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
		{
			String uri = request.getRequestURI();
			HessianSkeleton skeleton = (HessianSkeleton)skeletonMap.get(uri);
			if (!request.getMethod().equalsIgnoreCase("POST"))
			{
				response.setStatus(500);
			} else
			{
				RpcContext.getContext().setRemoteAddress(request.getRemoteAddr(), request.getRemotePort());
				try
				{
					skeleton.invoke(request.getInputStream(), response.getOutputStream());
				}
				catch (Throwable e)
				{
					throw new ServletException(e);
				}
			}
		}

		private HessianHandler()
		{
			this$0 = HessianProtocol.this;
			super();
		}

	}


	private final Map serverMap = new ConcurrentHashMap();
	private final Map skeletonMap = new ConcurrentHashMap();
	private HttpBinder httpBinder;

	public HessianProtocol()
	{
		super(new Class[] {
			com/autohome/hessian/HessianException
		});
	}

	public void setHttpBinder(HttpBinder httpBinder)
	{
		this.httpBinder = httpBinder;
	}

	public int getDefaultPort()
	{
		return 80;
	}

	protected Runnable doExport(Object impl, Class type, com.autohome.turbo.common.URL url)
		throws RpcException
	{
		String addr = (new StringBuilder()).append(url.getIp()).append(":").append(url.getPort()).toString();
		HttpServer server = (HttpServer)serverMap.get(addr);
		if (server == null)
		{
			server = httpBinder.bind(url, new HessianHandler());
			serverMap.put(addr, server);
		}
		final String path = url.getAbsolutePath();
		HessianSkeleton skeleton = new HessianSkeleton(impl, type);
		skeletonMap.put(path, skeleton);
		return new Runnable() {

			final String val$path;
			final HessianProtocol this$0;

			public void run()
			{
				skeletonMap.remove(path);
			}

			
			{
				this$0 = HessianProtocol.this;
				path = s;
				super();
			}
		};
	}

	protected Object doRefer(Class serviceType, com.autohome.turbo.common.URL url)
		throws RpcException
	{
		HessianProxyFactory hessianProxyFactory;
		byte ip[];
		hessianProxyFactory = new HessianProxyFactory();
		String client = url.getParameter("client", "jdk");
		if (client != null && client.length() > 0 && !"jdk".equals(client))
			throw new IllegalStateException((new StringBuilder()).append("Unsupported http protocol client=\"").append(client).append("\"!").toString());
		int timeout = url.getParameter("timeout", 1000);
		hessianProxyFactory.setConnectTimeout(timeout);
		hessianProxyFactory.setReadTimeout(timeout);
		if (!StringUtils.isNotEmpty(url.getParameter("csharp")))
			break MISSING_BLOCK_LABEL_242;
		String s[] = url.getHost().split("\\.", 4);
		ip = new byte[s.length];
		for (int i = 0; i < s.length; i++)
			ip[i] = (byte)Integer.parseInt(s[i], 10);

		Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByAddress(ip), url.getPort()));
		return hessianProxyFactory.create(serviceType, new URL(url.getParameter("csharp")), Thread.currentThread().getContextClassLoader(), proxy);
		UnknownHostException e;
		e;
		throw new RpcException(e);
		e;
		throw new RpcException(e);
		return hessianProxyFactory.create(serviceType, url.setProtocol("http").toJavaURL(), Thread.currentThread().getContextClassLoader());
	}

	protected int getErrorCode(Throwable e)
	{
		if (e instanceof HessianConnectionException)
		{
			if (e.getCause() != null)
			{
				Class cls = e.getCause().getClass();
				if (java/net/SocketTimeoutException.equals(cls))
					return 2;
			}
			return 1;
		}
		if (e instanceof HessianMethodSerializationException)
			return 5;
		else
			return super.getErrorCode(e);
	}

	public void destroy()
	{
		super.destroy();
		Iterator i$ = (new ArrayList(serverMap.keySet())).iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String key = (String)i$.next();
			HttpServer server = (HttpServer)serverMap.remove(key);
			if (server != null)
				try
				{
					if (logger.isInfoEnabled())
						logger.info((new StringBuilder()).append("Close hessian server ").append(server.getUrl()).toString());
					server.close();
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
		} while (true);
	}

}
