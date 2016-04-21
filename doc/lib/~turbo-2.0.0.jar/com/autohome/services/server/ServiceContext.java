// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceContext.java

package com.autohome.services.server;

import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class ServiceContext
{

	private static final ThreadLocal _localContext = new ThreadLocal();
	private ServletRequest _request;
	private String _serviceName;
	private String _objectId;
	private int _count;
	private HashMap _headers;

	private ServiceContext()
	{
		_headers = new HashMap();
	}

	public static void begin(ServletRequest request, String serviceName, String objectId)
		throws ServletException
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context == null)
		{
			context = new ServiceContext();
			_localContext.set(context);
		}
		context._request = request;
		context._serviceName = serviceName;
		context._objectId = objectId;
		context._count++;
	}

	public static ServiceContext getContext()
	{
		return (ServiceContext)_localContext.get();
	}

	public void addHeader(String header, Object value)
	{
		_headers.put(header, value);
	}

	public Object getHeader(String header)
	{
		return _headers.get(header);
	}

	public static Object getContextHeader(String header)
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context.getHeader(header);
		else
			return null;
	}

	public static ServletRequest getContextRequest()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context._request;
		else
			return null;
	}

	public static String getContextServiceName()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context._serviceName;
		else
			return null;
	}

	public static String getContextObjectId()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context._objectId;
		else
			return null;
	}

	public static void end()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null && --context._count == 0)
		{
			context._request = null;
			context._headers.clear();
		}
	}

	/**
	 * @deprecated Method getRequest is deprecated
	 */

	public static ServletRequest getRequest()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context._request;
		else
			return null;
	}

	/**
	 * @deprecated Method getServiceName is deprecated
	 */

	public static String getServiceName()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context._serviceName;
		else
			return null;
	}

	/**
	 * @deprecated Method getObjectId is deprecated
	 */

	public static String getObjectId()
	{
		ServiceContext context = (ServiceContext)_localContext.get();
		if (context != null)
			return context._objectId;
		else
			return null;
	}

}
