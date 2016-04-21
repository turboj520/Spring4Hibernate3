// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericService.java

package com.autohome.services.server;

import javax.servlet.*;

// Referenced classes of package com.autohome.services.server:
//			Service, ServiceContext

public class GenericService
	implements Service
{

	protected ServletConfig config;

	public GenericService()
	{
	}

	public void init(ServletConfig config)
		throws ServletException
	{
		this.config = config;
		init();
	}

	public void init()
		throws ServletException
	{
	}

	public String getInitParameter(String name)
	{
		return config.getInitParameter(name);
	}

	public ServletConfig getServletConfig()
	{
		return config;
	}

	public ServletContext getServletContext()
	{
		return config.getServletContext();
	}

	public void log(String message)
	{
		getServletContext().log(message);
	}

	public ServletRequest getRequest()
	{
		return ServiceContext.getRequest();
	}

	public String getServiceName()
	{
		return ServiceContext.getServiceName();
	}

	/**
	 * @deprecated Method getServiceId is deprecated
	 */

	public String getServiceId()
	{
		return getServiceName();
	}

	public String getObjectId()
	{
		return ServiceContext.getObjectId();
	}

	public void destroy()
	{
	}
}
