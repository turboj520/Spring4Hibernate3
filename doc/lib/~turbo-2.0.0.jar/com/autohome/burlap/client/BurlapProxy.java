// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapProxy.java

package com.autohome.burlap.client;

import com.autohome.burlap.io.AbstractBurlapInput;
import com.autohome.burlap.io.BurlapOutput;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.burlap.client:
//			BurlapRuntimeException, BurlapProtocolException, BurlapProxyFactory

public class BurlapProxy
	implements InvocationHandler
{

	private static final Logger log = Logger.getLogger(com/autohome/burlap/client/BurlapProxy.getName());
	private BurlapProxyFactory _factory;
	private URL _url;

	BurlapProxy(BurlapProxyFactory factory, URL url)
	{
		_factory = factory;
		_url = url;
	}

	public URL getURL()
	{
		return _url;
	}

	public Object invoke(Object proxy, Method method, Object args[])
		throws Throwable
	{
		InputStream is;
		HttpURLConnection httpConn;
		Exception exception;
		String methodName = method.getName();
		Class params[] = method.getParameterTypes();
		if (methodName.equals("equals") && params.length == 1 && params[0].equals(java/lang/Object))
		{
			Object value = args[0];
			if (value == null || !Proxy.isProxyClass(value.getClass()))
			{
				return new Boolean(false);
			} else
			{
				BurlapProxy handler = (BurlapProxy)Proxy.getInvocationHandler(value);
				return new Boolean(_url.equals(handler.getURL()));
			}
		}
		if (methodName.equals("hashCode") && params.length == 0)
			return new Integer(_url.hashCode());
		if (methodName.equals("getBurlapType"))
			return proxy.getClass().getInterfaces()[0].getName();
		if (methodName.equals("getBurlapURL"))
			return _url.toString();
		if (methodName.equals("toString") && params.length == 0)
			return (new StringBuilder()).append(getClass().getSimpleName()).append("[").append(_url).append("]").toString();
		is = null;
		URLConnection conn = null;
		httpConn = null;
		Object obj;
		try
		{
			conn = _factory.openConnection(_url);
			httpConn = (HttpURLConnection)conn;
			httpConn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/xml");
			OutputStream os;
			try
			{
				os = conn.getOutputStream();
			}
			catch (Exception e)
			{
				throw new BurlapRuntimeException(e);
			}
			BurlapOutput out = _factory.getBurlapOutput(os);
			if (_factory.isOverloadEnabled())
				if (args != null)
					methodName = (new StringBuilder()).append(methodName).append("__").append(args.length).toString();
				else
					methodName = (new StringBuilder()).append(methodName).append("__0").toString();
			if (log.isLoggable(Level.FINE))
				log.fine((new StringBuilder()).append(this).append(" calling ").append(methodName).append(" (").append(method).append(")").toString());
			out.call(methodName, args);
			try
			{
				os.flush();
			}
			catch (Exception e)
			{
				throw new BurlapRuntimeException(e);
			}
			if (conn instanceof HttpURLConnection)
			{
				httpConn = (HttpURLConnection)conn;
				int code = 500;
				try
				{
					code = httpConn.getResponseCode();
				}
				catch (Exception e) { }
				if (code != 200)
				{
					StringBuffer sb = new StringBuffer();
					try
					{
						is = httpConn.getInputStream();
						int ch;
						if (is != null)
						{
							while ((ch = is.read()) >= 0) 
								sb.append((char)ch);
							is.close();
						}
						is = httpConn.getErrorStream();
						if (is != null)
							while ((ch = is.read()) >= 0) 
								sb.append((char)ch);
					}
					catch (FileNotFoundException e)
					{
						throw new BurlapRuntimeException((new StringBuilder()).append(code).append(": ").append(String.valueOf(e)).toString());
					}
					catch (IOException e) { }
					if (is != null)
						is.close();
					throw new BurlapProtocolException((new StringBuilder()).append(code).append(": ").append(sb.toString()).toString());
				}
			}
			is = conn.getInputStream();
			AbstractBurlapInput in = _factory.getBurlapInput(is);
			obj = in.readReply(method.getReturnType());
		}
		catch (BurlapProtocolException e)
		{
			throw new BurlapRuntimeException(e);
		}
		finally { }
		try
		{
			if (is != null)
				is.close();
		}
		catch (IOException e) { }
		if (httpConn != null)
			httpConn.disconnect();
		return obj;
		try
		{
			if (is != null)
				is.close();
		}
		catch (IOException e) { }
		if (httpConn != null)
			httpConn.disconnect();
		throw exception;
	}

	public String toString()
	{
		return (new StringBuilder()).append(getClass().getSimpleName()).append("[").append(_url).append("]").toString();
	}

}
