// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapProxyFactory.java

package com.autohome.burlap.client;

import com.autohome.burlap.io.*;
import com.autohome.services.client.ServiceProxyFactory;
import java.io.*;
import java.lang.reflect.Proxy;
import java.net.*;
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.spi.ObjectFactory;

// Referenced classes of package com.autohome.burlap.client:
//			BurlapProxyResolver, BurlapMetaInfoAPI, BurlapRuntimeException, BurlapProxy

public class BurlapProxyFactory
	implements ServiceProxyFactory, ObjectFactory
{

	private BurlapRemoteResolver _resolver;
	private String _user;
	private String _password;
	private String _basicAuth;
	private long _readTimeout;
	private boolean _isOverloadEnabled;

	public BurlapProxyFactory()
	{
		_isOverloadEnabled = false;
		_resolver = new BurlapProxyResolver(this);
	}

	public void setUser(String user)
	{
		_user = user;
		_basicAuth = null;
	}

	public void setPassword(String password)
	{
		_password = password;
		_basicAuth = null;
	}

	public boolean isOverloadEnabled()
	{
		return _isOverloadEnabled;
	}

	public void setOverloadEnabled(boolean isOverloadEnabled)
	{
		_isOverloadEnabled = isOverloadEnabled;
	}

	public BurlapRemoteResolver getRemoteResolver()
	{
		return _resolver;
	}

	protected URLConnection openConnection(URL url)
		throws IOException
	{
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		if (_basicAuth != null)
			conn.setRequestProperty("Authorization", _basicAuth);
		else
		if (_user != null && _password != null)
		{
			_basicAuth = (new StringBuilder()).append("Basic ").append(base64((new StringBuilder()).append(_user).append(":").append(_password).toString())).toString();
			conn.setRequestProperty("Authorization", _basicAuth);
		}
		return conn;
	}

	public Object create(String url)
		throws MalformedURLException, ClassNotFoundException
	{
		BurlapMetaInfoAPI metaInfo = (BurlapMetaInfoAPI)create(com/autohome/burlap/client/BurlapMetaInfoAPI, url);
		String apiClassName = (String)metaInfo._burlap_getAttribute("java.api.class");
		if (apiClassName == null)
		{
			throw new BurlapRuntimeException((new StringBuilder()).append(url).append(" has an unknown api.").toString());
		} else
		{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class apiClass = Class.forName(apiClassName, false, loader);
			return create(apiClass, url);
		}
	}

	public Object create(Class api, String urlName)
		throws MalformedURLException
	{
		if (api == null)
			throw new NullPointerException();
		URL url = new URL(urlName);
		try
		{
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(10);
			conn.setReadTimeout(10);
			conn.setRequestProperty("Connection", "close");
			InputStream is = conn.getInputStream();
			is.close();
			conn.disconnect();
		}
		catch (IOException e) { }
		BurlapProxy handler = new BurlapProxy(this, url);
		return Proxy.newProxyInstance(api.getClassLoader(), new Class[] {
			api, com/autohome/burlap/io/BurlapRemoteObject
		}, handler);
	}

	public AbstractBurlapInput getBurlapInput(InputStream is)
	{
		AbstractBurlapInput in = new BurlapInput(is);
		in.setRemoteResolver(getRemoteResolver());
		return in;
	}

	public BurlapOutput getBurlapOutput(OutputStream os)
	{
		BurlapOutput out = new BurlapOutput(os);
		return out;
	}

	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment)
		throws Exception
	{
		Reference ref = (Reference)obj;
		String api = null;
		String url = null;
		String user = null;
		String password = null;
		for (int i = 0; i < ref.size(); i++)
		{
			RefAddr addr = ref.get(i);
			String type = addr.getType();
			String value = (String)addr.getContent();
			if (type.equals("type"))
			{
				api = value;
				continue;
			}
			if (type.equals("url"))
			{
				url = value;
				continue;
			}
			if (type.equals("user"))
			{
				setUser(value);
				continue;
			}
			if (type.equals("password"))
				setPassword(value);
		}

		if (url == null)
			throw new NamingException("`url' must be configured for BurlapProxyFactory.");
		if (api == null)
		{
			throw new NamingException("`type' must be configured for BurlapProxyFactory.");
		} else
		{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class apiClass = Class.forName(api, false, loader);
			return create(apiClass, url);
		}
	}

	private String base64(String value)
	{
		StringBuffer cb = new StringBuffer();
		int i = 0;
		for (i = 0; i + 2 < value.length(); i += 3)
		{
			long chunk = value.charAt(i);
			chunk = (chunk << 8) + (long)value.charAt(i + 1);
			chunk = (chunk << 8) + (long)value.charAt(i + 2);
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append(encode(chunk));
		}

		if (i + 1 < value.length())
		{
			long chunk = value.charAt(i);
			chunk = (chunk << 8) + (long)value.charAt(i + 1);
			chunk <<= 8;
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append('=');
		} else
		if (i < value.length())
		{
			long chunk = value.charAt(i);
			chunk <<= 16;
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append('=');
			cb.append('=');
		}
		return cb.toString();
	}

	public static char encode(long d)
	{
		d &= 63L;
		if (d < 26L)
			return (char)(int)(d + 65L);
		if (d < 52L)
			return (char)(int)((d + 97L) - 26L);
		if (d < 62L)
			return (char)(int)((d + 48L) - 52L);
		return d != 62L ? '/' : '+';
	}
}
