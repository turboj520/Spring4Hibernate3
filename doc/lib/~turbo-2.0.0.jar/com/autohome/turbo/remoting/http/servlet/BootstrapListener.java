// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BootstrapListener.java

package com.autohome.turbo.remoting.http.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// Referenced classes of package com.autohome.turbo.remoting.http.servlet:
//			ServletManager

public class BootstrapListener
	implements ServletContextListener
{

	public BootstrapListener()
	{
	}

	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		ServletManager.getInstance().addServletContext(-1234, servletContextEvent.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		ServletManager.getInstance().removeServletContext(-1234);
	}
}
