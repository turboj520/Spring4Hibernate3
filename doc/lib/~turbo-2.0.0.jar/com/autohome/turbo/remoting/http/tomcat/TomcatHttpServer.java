// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TomcatHttpServer.java

package com.autohome.turbo.remoting.http.tomcat;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.http.HttpHandler;
import com.autohome.turbo.remoting.http.servlet.DispatcherServlet;
import com.autohome.turbo.remoting.http.servlet.ServletManager;
import com.autohome.turbo.remoting.http.support.AbstractHttpServer;
import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class TomcatHttpServer extends AbstractHttpServer
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/http/tomcat/TomcatHttpServer);
	private final Tomcat tomcat = new Tomcat();
	private final URL url;

	public TomcatHttpServer(URL url, HttpHandler handler)
	{
		super(url, handler);
		this.url = url;
		DispatcherServlet.addHttpHandler(url.getPort(), handler);
		String baseDir = (new File(System.getProperty("java.io.tmpdir"))).getAbsolutePath();
		tomcat.setBaseDir(baseDir);
		tomcat.setPort(url.getPort());
		tomcat.getConnector().setProperty("maxThreads", String.valueOf(url.getParameter("threads", 200)));
		tomcat.getConnector().setProperty("maxConnections", String.valueOf(url.getParameter("accepts", -1)));
		tomcat.getConnector().setProperty("URIEncoding", "UTF-8");
		tomcat.getConnector().setProperty("connectionTimeout", "60000");
		tomcat.getConnector().setProperty("maxKeepAliveRequests", "-1");
		tomcat.getConnector().setProtocol("org.apache.coyote.http11.Http11NioProtocol");
		Context context = tomcat.addContext("/", baseDir);
		Tomcat.addServlet(context, "dispatcher", new DispatcherServlet());
		context.addServletMapping("/*", "dispatcher");
		ServletManager.getInstance().addServletContext(url.getPort(), context.getServletContext());
		try
		{
			tomcat.start();
		}
		catch (LifecycleException e)
		{
			throw new IllegalStateException((new StringBuilder()).append("Failed to start tomcat server at ").append(url.getAddress()).toString(), e);
		}
	}

	public void close()
	{
		super.close();
		ServletManager.getInstance().removeServletContext(url.getPort());
		try
		{
			tomcat.stop();
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

}
