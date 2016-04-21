// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianProxy.java

package com.autohome.hessian.client;

import com.autohome.hessian.io.AbstractHessianInput;
import com.autohome.hessian.io.AbstractHessianOutput;
import com.autohome.hessian.io.HessianDebugInputStream;
import com.autohome.hessian.io.HessianDebugOutputStream;
import com.autohome.hessian.io.HessianProtocolException;
import com.autohome.hessian.io.HessianRemote;
import com.autohome.services.server.AbstractSkeleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.client:
//			HessianRuntimeException, HessianConnection, HessianProxyFactory, HessianConnectionFactory

public class HessianProxy
	implements InvocationHandler, Serializable
{
	static class LogWriter extends Writer
	{

		private Logger _log;
		private Level _level;
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
					_log.log(_level, _sb.toString());
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
			if (_sb.length() > 0)
				_log.log(_level, _sb.toString());
		}

		LogWriter(Logger log)
		{
			_level = Level.FINEST;
			_sb = new StringBuilder();
			_log = log;
		}
	}

	static class ResultInputStream extends InputStream
	{

		private HessianConnection _conn;
		private InputStream _connIs;
		private AbstractHessianInput _in;
		private InputStream _hessianIs;

		public int read()
			throws IOException
		{
			if (_hessianIs != null)
			{
				int value = _hessianIs.read();
				if (value < 0)
					close();
				return value;
			} else
			{
				return -1;
			}
		}

		public int read(byte buffer[], int offset, int length)
			throws IOException
		{
			if (_hessianIs != null)
			{
				int value = _hessianIs.read(buffer, offset, length);
				if (value < 0)
					close();
				return value;
			} else
			{
				return -1;
			}
		}

		public void close()
			throws IOException
		{
			HessianConnection conn = _conn;
			_conn = null;
			InputStream connIs = _connIs;
			_connIs = null;
			AbstractHessianInput in = _in;
			_in = null;
			InputStream hessianIs = _hessianIs;
			_hessianIs = null;
			try
			{
				if (hessianIs != null)
					hessianIs.close();
			}
			catch (Exception e)
			{
				HessianProxy.log.log(Level.FINE, e.toString(), e);
			}
			try
			{
				if (in != null)
				{
					in.completeReply();
					in.close();
				}
			}
			catch (Exception e)
			{
				HessianProxy.log.log(Level.FINE, e.toString(), e);
			}
			try
			{
				if (connIs != null)
					connIs.close();
			}
			catch (Exception e)
			{
				HessianProxy.log.log(Level.FINE, e.toString(), e);
			}
			try
			{
				if (conn != null)
					conn.close();
			}
			catch (Exception e)
			{
				HessianProxy.log.log(Level.FINE, e.toString(), e);
			}
		}

		ResultInputStream(HessianConnection conn, InputStream is, AbstractHessianInput in, InputStream hessianIs)
		{
			_conn = conn;
			_connIs = is;
			_in = in;
			_hessianIs = hessianIs;
		}
	}


	private static final Logger log = Logger.getLogger(com/autohome/hessian/client/HessianProxy.getName());
	protected HessianProxyFactory _factory;
	private WeakHashMap _mangleMap;
	private Class _type;
	private URL _url;
	private java.net.Proxy proxy;

	public java.net.Proxy getProxy()
	{
		return proxy;
	}

	public void setProxy(java.net.Proxy proxy)
	{
		this.proxy = proxy;
	}

	protected HessianProxy(URL url, HessianProxyFactory factory)
	{
		this(url, factory, null);
	}

	protected HessianProxy(URL url, HessianProxyFactory factory, Class type)
	{
		_mangleMap = new WeakHashMap();
		_factory = factory;
		_url = url;
		_type = type;
	}

	public URL getURL()
	{
		return _url;
	}

	public Object invoke(Object proxy, Method method, Object args[])
		throws Throwable
	{
		String mangleName;
		InputStream is;
		HessianConnection conn;
		synchronized (_mangleMap)
		{
			mangleName = (String)_mangleMap.get(method);
		}
		if (mangleName == null)
		{
			String methodName = method.getName();
			Class params[] = method.getParameterTypes();
			if (methodName.equals("equals") && params.length == 1 && params[0].equals(java/lang/Object))
			{
				Object value = args[0];
				if (value == null || !Proxy.isProxyClass(value.getClass()))
					return Boolean.FALSE;
				Object proxyHandler = Proxy.getInvocationHandler(value);
				if (!(proxyHandler instanceof HessianProxy))
				{
					return Boolean.FALSE;
				} else
				{
					HessianProxy handler = (HessianProxy)proxyHandler;
					return new Boolean(_url.equals(handler.getURL()));
				}
			}
			if (methodName.equals("hashCode") && params.length == 0)
				return new Integer(_url.hashCode());
			if (methodName.equals("getHessianType"))
				return proxy.getClass().getInterfaces()[0].getName();
			if (methodName.equals("getHessianURL"))
				return _url.toString();
			if (methodName.equals("toString") && params.length == 0)
				return (new StringBuilder()).append("HessianProxy[").append(_url).append("]").toString();
			if (!_factory.isOverloadEnabled())
				mangleName = method.getName();
			else
				mangleName = mangleName(method);
			synchronized (_mangleMap)
			{
				_mangleMap.put(method, mangleName);
			}
		}
		is = null;
		conn = null;
		int code;
		Object obj;
		if (log.isLoggable(Level.FINER))
			log.finer((new StringBuilder()).append("Hessian[").append(_url).append("] calling ").append(mangleName).toString());
		conn = sendRequest(mangleName, args);
		is = conn.getInputStream();
		if (log.isLoggable(Level.FINEST))
		{
			PrintWriter dbg = new PrintWriter(new LogWriter(log));
			HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
			dIs.startTop2();
			is = dIs;
		}
		code = is.read();
		if (code != 72)
			break MISSING_BLOCK_LABEL_576;
		int major = is.read();
		int minor = is.read();
		AbstractHessianInput in = _factory.getHessian2Input(is);
		Object value = in.readReply(method.getReturnType());
		obj = value;
		try
		{
			if (is != null)
				is.close();
		}
		catch (Exception e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		try
		{
			if (conn != null)
				conn.destroy();
		}
		catch (Exception e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		return obj;
		if (code != 114)
			break MISSING_BLOCK_LABEL_737;
		int major = is.read();
		int minor = is.read();
		AbstractHessianInput in = _factory.getHessian1Input(is);
		in.startReplyBody();
		Object value = in.readObject(method.getReturnType());
		if (value instanceof InputStream)
		{
			value = new ResultInputStream(conn, is, in, (InputStream)value);
			is = null;
			conn = null;
		} else
		{
			in.completeReply();
		}
		obj = value;
		try
		{
			if (is != null)
				is.close();
		}
		catch (Exception e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		try
		{
			if (conn != null)
				conn.destroy();
		}
		catch (Exception e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		return obj;
		try
		{
			throw new HessianProtocolException((new StringBuilder()).append("'").append((char)code).append("' is an unknown code").toString());
		}
		catch (HessianProtocolException e)
		{
			throw new HessianRuntimeException(e);
		}
		Exception exception2;
		exception2;
		try
		{
			if (is != null)
				is.close();
		}
		catch (Exception e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		try
		{
			if (conn != null)
				conn.destroy();
		}
		catch (Exception e)
		{
			log.log(Level.FINE, e.toString(), e);
		}
		throw exception2;
	}

	protected String mangleName(Method method)
	{
		Class param[] = method.getParameterTypes();
		if (param == null || param.length == 0)
			return method.getName();
		else
			return AbstractSkeleton.mangleName(method, false);
	}

	protected HessianConnection sendRequest(String methodName, Object args[])
		throws IOException
	{
		HessianConnection conn;
		boolean isValid;
		conn = null;
		if (getProxy() != null)
			conn = _factory.getConnectionFactory().open(_url, getProxy());
		else
			conn = _factory.getConnectionFactory().open(_url);
		isValid = false;
		HessianConnection hessianconnection;
		addRequestHeaders(conn);
		OutputStream os = null;
		try
		{
			os = conn.getOutputStream();
		}
		catch (Exception e)
		{
			throw new HessianRuntimeException(e);
		}
		if (log.isLoggable(Level.FINEST))
		{
			PrintWriter dbg = new PrintWriter(new LogWriter(log));
			HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
			dOs.startTop2();
			os = dOs;
		}
		AbstractHessianOutput out = _factory.getHessianOutput(os);
		out.call(methodName, args);
		out.flush();
		conn.sendRequest();
		isValid = true;
		hessianconnection = conn;
		if (!isValid && conn != null)
			conn.destroy();
		return hessianconnection;
		Exception exception;
		exception;
		if (!isValid && conn != null)
			conn.destroy();
		throw exception;
	}

	protected void addRequestHeaders(HessianConnection conn)
	{
		conn.addHeader("Content-Type", "x-application/hessian");
		String basicAuth = _factory.getBasicAuth();
		if (basicAuth != null)
			conn.addHeader("Authorization", basicAuth);
	}

	protected void parseResponseHeaders(URLConnection urlconnection)
	{
	}

	public Object writeReplace()
	{
		return new HessianRemote(_type.getName(), _url.toString());
	}


}
