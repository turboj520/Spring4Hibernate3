// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JettyHttpServer.java

package com.autohome.turbo.remoting.http.jetty;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.http.HttpHandler;
import com.autohome.turbo.remoting.http.servlet.DispatcherServlet;
import com.autohome.turbo.remoting.http.servlet.ServletManager;
import com.autohome.turbo.remoting.http.support.AbstractHttpServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;
import org.mortbay.log.Logger;
import org.mortbay.log.StdErrLog;
import org.mortbay.thread.QueuedThreadPool;

public class JettyHttpServer extends AbstractHttpServer
{

	private static final com.autohome.turbo.common.logger.Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/http/jetty/JettyHttpServer);
	private Server server;
	private URL url;

	public JettyHttpServer(URL url, HttpHandler handler)
	{
		super(url, handler);
		this.url = url;
		Log.setLog(new StdErrLog());
		Log.getLog().setDebugEnabled(false);
		DispatcherServlet.addHttpHandler(url.getPort(), handler);
		int threads = url.getParameter("threads", 200);
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setDaemon(true);
		threadPool.setMaxThreads(threads);
		threadPool.setMinThreads(threads);
		SelectChannelConnector connector = new SelectChannelConnector();
		if (!url.isAnyHost() && NetUtils.isValidLocalHost(url.getHost()))
			connector.setHost(url.getHost());
		connector.setPort(url.getPort());
		server = new Server();
		server.setThreadPool(threadPool);
		server.addConnector(connector);
		ServletHandler servletHandler = new ServletHandler();
		ServletHolder servletHolder = servletHandler.addServletWithMapping(com/autohome/turbo/remoting/http/servlet/DispatcherServlet, "/*");
		servletHolder.setInitOrder(2);
		Context context = new Context(server, "/", 1);
		context.setServletHandler(servletHandler);
		ServletManager.getInstance().addServletContext(url.getPort(), context.getServletContext());
		try
		{
			server.start();
		}
		catch (Exception e)
		{
			throw new IllegalStateException((new StringBuilder()).append("Failed to start jetty server on ").append(url.getAddress()).append(", cause: ").append(e.getMessage()).toString(), e);
		}
	}

	public void close()
	{
		super.close();
		ServletManager.getInstance().removeServletContext(url.getPort());
		if (server != null)
			try
			{
				server.stop();
			}
			catch (Exception e)
			{
				logger.warn(e.getMessage(), e);
			}
	}

}
