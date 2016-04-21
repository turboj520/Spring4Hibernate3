// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianServlet.java

package com.autohome.hessian.server;

import com.autohome.hessian.io.Hessian2Input;
import com.autohome.hessian.io.SerializerFactory;
import com.autohome.services.server.*;
import java.io.*;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Referenced classes of package com.autohome.hessian.server:
//			HessianSkeleton

public class HessianServlet extends GenericServlet
{
	static class LogWriter extends Writer
	{

		private Logger _log;
		private StringBuilder _sb;

		public void write(char ch)
		{
			if (ch == '\n' && _sb.length() > 0)
			{
				_log.fine(_sb.toString());
				_sb.setLength(0);
			} else
			{
				_sb.append(ch);
			}
		}

		public void write(char buffer[], int offset, int length)
		{
			for (int i = 0; i < length; i++)
			{
				char ch = buffer[offset + i];
				if (ch == '\n' && _sb.length() > 0)
				{
					_log.fine(_sb.toString());
					_sb.setLength(0);
				} else
				{
					_sb.append(ch);
				}
			}

		}

		public void flush()
		{
		}

		public void close()
		{
		}

		LogWriter(Logger log)
		{
			_sb = new StringBuilder();
			_log = log;
		}
	}


	private Logger _log;
	private Class _homeAPI;
	private Object _homeImpl;
	private Class _objectAPI;
	private Object _objectImpl;
	private HessianSkeleton _homeSkeleton;
	private HessianSkeleton _objectSkeleton;
	private SerializerFactory _serializerFactory;
	private boolean _isDebug;

	public HessianServlet()
	{
		_log = Logger.getLogger(com/autohome/hessian/server/HessianServlet.getName());
	}

	public String getServletInfo()
	{
		return "Hessian Servlet";
	}

	public void setHomeAPI(Class api)
	{
		_homeAPI = api;
	}

	public void setHome(Object home)
	{
		_homeImpl = home;
	}

	public void setObjectAPI(Class api)
	{
		_objectAPI = api;
	}

	public void setObject(Object object)
	{
		_objectImpl = object;
	}

	public void setService(Object service)
	{
		setHome(service);
	}

	public void setAPIClass(Class api)
	{
		setHomeAPI(api);
	}

	public Class getAPIClass()
	{
		return _homeAPI;
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory();
		return _serializerFactory;
	}

	public void setSendCollectionType(boolean sendType)
	{
		getSerializerFactory().setSendCollectionType(sendType);
	}

	public void setDebug(boolean isDebug)
	{
		_isDebug = isDebug;
	}

	public void setLogName(String name)
	{
		_log = Logger.getLogger(name);
	}

	public void init(ServletConfig config)
		throws ServletException
	{
		super.init(config);
		try
		{
			if (_homeImpl == null)
				if (getInitParameter("home-class") != null)
				{
					String className = getInitParameter("home-class");
					Class homeClass = loadClass(className);
					_homeImpl = homeClass.newInstance();
					init(_homeImpl);
				} else
				if (getInitParameter("service-class") != null)
				{
					String className = getInitParameter("service-class");
					Class homeClass = loadClass(className);
					_homeImpl = homeClass.newInstance();
					init(_homeImpl);
				} else
				{
					if (getClass().equals(com/autohome/hessian/server/HessianServlet))
						throw new ServletException("server must extend HessianServlet");
					_homeImpl = this;
				}
			if (_homeAPI == null)
				if (getInitParameter("home-api") != null)
				{
					String className = getInitParameter("home-api");
					_homeAPI = loadClass(className);
				} else
				if (getInitParameter("api-class") != null)
				{
					String className = getInitParameter("api-class");
					_homeAPI = loadClass(className);
				} else
				if (_homeImpl != null)
				{
					_homeAPI = findRemoteAPI(_homeImpl.getClass());
					if (_homeAPI == null)
						_homeAPI = _homeImpl.getClass();
				}
			if (_objectImpl == null && getInitParameter("object-class") != null)
			{
				String className = getInitParameter("object-class");
				Class objectClass = loadClass(className);
				_objectImpl = objectClass.newInstance();
				init(_objectImpl);
			}
			if (_objectAPI == null)
				if (getInitParameter("object-api") != null)
				{
					String className = getInitParameter("object-api");
					_objectAPI = loadClass(className);
				} else
				if (_objectImpl != null)
					_objectAPI = _objectImpl.getClass();
			_homeSkeleton = new HessianSkeleton(_homeImpl, _homeAPI);
			if (_objectAPI != null)
				_homeSkeleton.setObjectClass(_objectAPI);
			if (_objectImpl != null)
			{
				_objectSkeleton = new HessianSkeleton(_objectImpl, _objectAPI);
				_objectSkeleton.setHomeClass(_homeAPI);
			} else
			{
				_objectSkeleton = _homeSkeleton;
			}
			if ("true".equals(getInitParameter("debug")))
				_isDebug = true;
			if ("false".equals(getInitParameter("send-collection-type")))
				setSendCollectionType(false);
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

	private Class findRemoteAPI(Class implClass)
	{
		if (implClass == null || implClass.equals(com/autohome/services/server/GenericService))
			return null;
		Class interfaces[] = implClass.getInterfaces();
		if (interfaces.length == 1)
			return interfaces[0];
		else
			return findRemoteAPI(implClass.getSuperclass());
	}

	private Class loadClass(String className)
		throws ClassNotFoundException
	{
		ClassLoader loader = getContextClassLoader();
		if (loader != null)
			return Class.forName(className, false, loader);
		else
			return Class.forName(className);
	}

	protected ClassLoader getContextClassLoader()
	{
		return Thread.currentThread().getContextClassLoader();
	}

	private void init(Object service)
		throws ServletException
	{
		if (getClass().equals(com/autohome/hessian/server/HessianServlet))
			if (service instanceof Service)
				((Service)service).init(getServletConfig());
			else
			if (service instanceof Servlet)
				((Servlet)service).init(getServletConfig());
	}

	public void service(ServletRequest request, ServletResponse response)
		throws IOException, ServletException
	{
		Exception exception;
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		if (!req.getMethod().equals("POST"))
		{
			res.setStatus(500, "Hessian Requires POST");
			PrintWriter out = res.getWriter();
			res.setContentType("text/html");
			out.println("<h1>Hessian Requires POST</h1>");
			return;
		}
		String serviceId = req.getPathInfo();
		String objectId = req.getParameter("id");
		if (objectId == null)
			objectId = req.getParameter("ejbid");
		ServiceContext.begin(req, serviceId, objectId);
		try
		{
			InputStream is = request.getInputStream();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/x-hessian");
			SerializerFactory serializerFactory = getSerializerFactory();
			invoke(is, os, objectId, serializerFactory);
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
		break MISSING_BLOCK_LABEL_182;
		throw exception;
	}

	protected void invoke(InputStream is, OutputStream os, String objectId, SerializerFactory serializerFactory)
		throws Exception
	{
		if (objectId != null)
			_objectSkeleton.invoke(is, os, serializerFactory);
		else
			_homeSkeleton.invoke(is, os, serializerFactory);
	}

	protected Hessian2Input createHessian2Input(InputStream is)
	{
		return new Hessian2Input(is);
	}
}
