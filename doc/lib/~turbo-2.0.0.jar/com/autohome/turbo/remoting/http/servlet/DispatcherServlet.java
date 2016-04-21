// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DispatcherServlet.java

package com.autohome.turbo.remoting.http.servlet;

import com.autohome.turbo.remoting.http.HttpHandler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DispatcherServlet extends HttpServlet
{

	private static final long serialVersionUID = 0x5006300733718190L;
	private static DispatcherServlet INSTANCE;
	private static final Map handlers = new ConcurrentHashMap();

	public static void addHttpHandler(int port, HttpHandler processor)
	{
		handlers.put(Integer.valueOf(port), processor);
	}

	public static void removeHttpHandler(int port)
	{
		handlers.remove(Integer.valueOf(port));
	}

	public static DispatcherServlet getInstance()
	{
		return INSTANCE;
	}

	public DispatcherServlet()
	{
		INSTANCE = this;
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		HttpHandler handler = (HttpHandler)handlers.get(Integer.valueOf(request.getLocalPort()));
		if (handler == null)
			response.sendError(404, "Service not found.");
		else
			handler.handle(request, response);
	}

}
