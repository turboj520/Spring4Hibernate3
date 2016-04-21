// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpProtocol.java

package com.autohome.turbo.rpc.protocol.http;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.http.*;
import com.autohome.turbo.rpc.RpcContext;
import com.autohome.turbo.rpc.RpcException;
import com.autohome.turbo.rpc.protocol.AbstractProxyProtocol;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.httpinvoker.*;

public class HttpProtocol extends AbstractProxyProtocol
{
	private class InternalHandler
		implements HttpHandler
	{

		final HttpProtocol this$0;

		public void handle(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
		{
			String uri = request.getRequestURI();
			HttpInvokerServiceExporter skeleton = (HttpInvokerServiceExporter)skeletonMap.get(uri);
			if (!request.getMethod().equalsIgnoreCase("POST"))
			{
				response.setStatus(500);
			} else
			{
				RpcContext.getContext().setRemoteAddress(request.getRemoteAddr(), request.getRemotePort());
				try
				{
					skeleton.handleRequest(request, response);
				}
				catch (Throwable e)
				{
					throw new ServletException(e);
				}
			}
		}

		private InternalHandler()
		{
			this$0 = HttpProtocol.this;
			super();
		}

	}


	public static final int DEFAULT_PORT = 80;
	private final Map serverMap = new ConcurrentHashMap();
	private final Map skeletonMap = new ConcurrentHashMap();
	private HttpBinder httpBinder;

	public HttpProtocol()
	{
		super(new Class[] {
			org/springframework/remoting/RemoteAccessException
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

	protected Runnable doExport(Object impl, Class type, URL url)
		throws RpcException
	{
		String addr = (new StringBuilder()).append(url.getIp()).append(":").append(url.getPort()).toString();
		HttpServer server = (HttpServer)serverMap.get(addr);
		if (server == null)
		{
			server = httpBinder.bind(url, new InternalHandler());
			serverMap.put(addr, server);
		}
		HttpInvokerServiceExporter httpServiceExporter = new HttpInvokerServiceExporter();
		httpServiceExporter.setServiceInterface(type);
		httpServiceExporter.setService(impl);
		try
		{
			httpServiceExporter.afterPropertiesSet();
		}
		catch (Exception e)
		{
			throw new RpcException(e.getMessage(), e);
		}
		final String path = url.getAbsolutePath();
		skeletonMap.put(path, httpServiceExporter);
		return new Runnable() {

			final String val$path;
			final HttpProtocol this$0;

			public void run()
			{
				skeletonMap.remove(path);
			}

			
			{
				this$0 = HttpProtocol.this;
				path = s;
				super();
			}
		};
	}

	protected Object doRefer(Class serviceType, final URL url)
		throws RpcException
	{
		HttpInvokerProxyFactoryBean httpProxyFactoryBean = new HttpInvokerProxyFactoryBean();
		httpProxyFactoryBean.setServiceUrl(url.toIdentityString());
		httpProxyFactoryBean.setServiceInterface(serviceType);
		String client = url.getParameter("client");
		if (client == null || client.length() == 0 || "simple".equals(client))
		{
			SimpleHttpInvokerRequestExecutor httpInvokerRequestExecutor = new SimpleHttpInvokerRequestExecutor() {

				final URL val$url;
				final HttpProtocol this$0;

				protected void prepareConnection(HttpURLConnection con, int contentLength)
					throws IOException
				{
					super.prepareConnection(con, contentLength);
					con.setReadTimeout(url.getParameter("timeout", 1000));
					con.setConnectTimeout(url.getParameter("connect.timeout", 3000));
				}

			
			{
				this$0 = HttpProtocol.this;
				url = url1;
				super();
			}
			};
			httpProxyFactoryBean.setHttpInvokerRequestExecutor(httpInvokerRequestExecutor);
		} else
		if ("commons".equals(client))
		{
			CommonsHttpInvokerRequestExecutor httpInvokerRequestExecutor = new CommonsHttpInvokerRequestExecutor();
			httpInvokerRequestExecutor.setReadTimeout(url.getParameter("connect.timeout", 3000));
			httpProxyFactoryBean.setHttpInvokerRequestExecutor(httpInvokerRequestExecutor);
		} else
		if (client != null && client.length() > 0)
			throw new IllegalStateException((new StringBuilder()).append("Unsupported http protocol client ").append(client).append(", only supported: simple, commons").toString());
		httpProxyFactoryBean.afterPropertiesSet();
		return httpProxyFactoryBean.getObject();
	}

	protected int getErrorCode(Throwable e)
	{
		if (e instanceof RemoteAccessException)
			e = e.getCause();
		if (e != null)
		{
			Class cls = e.getClass();
			if (java/net/SocketTimeoutException.equals(cls))
				return 2;
			if (java/io/IOException.isAssignableFrom(cls))
				return 1;
			if (java/lang/ClassNotFoundException.isAssignableFrom(cls))
				return 5;
		}
		return super.getErrorCode(e);
	}

}
