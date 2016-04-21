// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServletManager.java

package com.autohome.turbo.remoting.http.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;

public class ServletManager
{

	public static final int EXTERNAL_SERVER_PORT = -1234;
	private static final ServletManager instance = new ServletManager();
	private final Map contextMap = new ConcurrentHashMap();

	public ServletManager()
	{
	}

	public static ServletManager getInstance()
	{
		return instance;
	}

	public void addServletContext(int port, ServletContext servletContext)
	{
		contextMap.put(Integer.valueOf(port), servletContext);
	}

	public void removeServletContext(int port)
	{
		contextMap.remove(Integer.valueOf(port));
	}

	public ServletContext getServletContext(int port)
	{
		return (ServletContext)contextMap.get(Integer.valueOf(port));
	}

}
