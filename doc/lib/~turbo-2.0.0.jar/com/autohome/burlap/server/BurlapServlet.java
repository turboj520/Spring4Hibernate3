// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapServlet.java

package com.autohome.burlap.server;

import com.autohome.burlap.io.BurlapInput;
import com.autohome.burlap.io.BurlapOutput;
import com.autohome.services.server.Service;
import com.autohome.services.server.ServiceContext;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Referenced classes of package com.autohome.burlap.server:
//			BurlapSkeleton

public class BurlapServlet extends GenericServlet
{

	private Class _apiClass;
	private Object _service;
	private BurlapSkeleton _skeleton;

	public BurlapServlet()
	{
	}

	public String getServletInfo()
	{
		return "Burlap Servlet";
	}

	public void setService(Object service)
	{
		_service = service;
	}

	public void setAPIClass(Class apiClass)
	{
		_apiClass = apiClass;
	}

	public void init(ServletConfig config)
		throws ServletException
	{
		super.init(config);
		try
		{
			if (_service == null)
			{
				String className = getInitParameter("service-class");
				Class serviceClass = null;
				if (className != null)
				{
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					if (loader != null)
						serviceClass = Class.forName(className, false, loader);
					else
						serviceClass = Class.forName(className);
				} else
				{
					if (getClass().equals(com/autohome/burlap/server/BurlapServlet))
						throw new ServletException("server must extend BurlapServlet");
					serviceClass = getClass();
				}
				_service = serviceClass.newInstance();
				if (_service instanceof BurlapServlet)
					((BurlapServlet)_service).setService(this);
				if (_service instanceof Service)
					((Service)_service).init(getServletConfig());
				else
				if (_service instanceof Servlet)
					((Servlet)_service).init(getServletConfig());
			}
			if (_apiClass == null)
			{
				String className = getInitParameter("api-class");
				if (className != null)
				{
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					if (loader != null)
						_apiClass = Class.forName(className, false, loader);
					else
						_apiClass = Class.forName(className);
				} else
				{
					_apiClass = _service.getClass();
				}
			}
			_skeleton = new BurlapSkeleton(_service, _apiClass);
		}
		catch (ServletException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}
	}

	public void service(ServletRequest request, ServletResponse response)
		throws IOException, ServletException
	{
		Exception exception;
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		if (!req.getMethod().equals("POST"))
		{
			res.setStatus(500, "Burlap Requires POST");
			PrintWriter out = res.getWriter();
			res.setContentType("text/html");
			out.println("<h1>Burlap Requires POST</h1>");
			return;
		}
		String serviceId = req.getPathInfo();
		String objectId = req.getParameter("id");
		if (objectId == null)
			objectId = req.getParameter("ejbid");
		ServiceContext.begin(req, serviceId, objectId);
		try
		{
			java.io.InputStream is = request.getInputStream();
			java.io.OutputStream os = response.getOutputStream();
			BurlapInput in = new BurlapInput(is);
			BurlapOutput out = new BurlapOutput(os);
			_skeleton.invoke(in, out);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (ServletException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
		finally
		{
			ServiceContext.end();
		}
		ServiceContext.end();
		break MISSING_BLOCK_LABEL_189;
		throw exception;
	}
}
