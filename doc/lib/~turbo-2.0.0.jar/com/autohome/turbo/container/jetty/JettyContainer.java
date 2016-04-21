// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JettyContainer.java

package com.autohome.turbo.container.jetty;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.container.Container;
import com.autohome.turbo.container.page.PageServlet;
import com.autohome.turbo.container.page.ResourceFilter;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.*;

public class JettyContainer
	implements Container
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/container/jetty/JettyContainer);
	public static final String JETTY_PORT = "dubbo.jetty.port";
	public static final String JETTY_DIRECTORY = "dubbo.jetty.directory";
	public static final String JETTY_PAGES = "dubbo.jetty.page";
	public static final int DEFAULT_JETTY_PORT = 8080;
	SelectChannelConnector connector;

	public JettyContainer()
	{
	}

	public void start()
	{
		String serverPort = ConfigUtils.getProperty("dubbo.jetty.port");
		int port;
		if (serverPort == null || serverPort.length() == 0)
			port = 8080;
		else
			port = Integer.parseInt(serverPort);
		connector = new SelectChannelConnector();
		connector.setPort(port);
		ServletHandler handler = new ServletHandler();
		String resources = ConfigUtils.getProperty("dubbo.jetty.directory");
		if (resources != null && resources.length() > 0)
		{
			FilterHolder resourceHolder = handler.addFilterWithMapping(com/autohome/turbo/container/page/ResourceFilter, "/*", 0);
			resourceHolder.setInitParameter("resources", resources);
		}
		ServletHolder pageHolder = handler.addServletWithMapping(com/autohome/turbo/container/page/PageServlet, "/*");
		pageHolder.setInitParameter("pages", ConfigUtils.getProperty("dubbo.jetty.page"));
		pageHolder.setInitOrder(2);
		Server server = new Server();
		server.addConnector(connector);
		server.addHandler(handler);
		try
		{
			server.start();
		}
		catch (Exception e)
		{
			throw new IllegalStateException((new StringBuilder()).append("Failed to start jetty server on ").append(NetUtils.getLocalHost()).append(":").append(port).append(", cause: ").append(e.getMessage()).toString(), e);
		}
	}

	public void stop()
	{
		try
		{
			if (connector != null)
			{
				connector.close();
				connector = null;
			}
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
