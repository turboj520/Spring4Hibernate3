// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianProxyFactory.java

package com.autohome.hessian.client;

import com.autohome.hessian.io.AbstractHessianInput;
import com.autohome.hessian.io.AbstractHessianOutput;
import com.autohome.hessian.io.Hessian2Input;
import com.autohome.hessian.io.Hessian2Output;
import com.autohome.hessian.io.HessianDebugInputStream;
import com.autohome.hessian.io.HessianInput;
import com.autohome.hessian.io.HessianOutput;
import com.autohome.hessian.io.HessianRemoteObject;
import com.autohome.hessian.io.HessianRemoteResolver;
import com.autohome.hessian.io.SerializerFactory;
import com.autohome.services.client.ServiceProxyFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

// Referenced classes of package com.autohome.hessian.client:
//			HessianProxyResolver, HessianConnectionFactory, HessianURLConnectionFactory, HessianMetaInfoAPI, 
//			HessianRuntimeException, HessianProxy

public class HessianProxyFactory
	implements ServiceProxyFactory, ObjectFactory
{

	protected static Logger log = Logger.getLogger(com/autohome/hessian/client/HessianProxyFactory.getName());
	private final ClassLoader _loader;
	private SerializerFactory _serializerFactory;
	private HessianConnectionFactory _connFactory;
	private HessianRemoteResolver _resolver;
	private String _user;
	private String _password;
	private String _basicAuth;
	private boolean _isOverloadEnabled;
	private boolean _isHessian2Reply;
	private boolean _isHessian2Request;
	private boolean _isChunkedPost;
	private boolean _isDebug;
	private long _readTimeout;
	private long _connectTimeout;

	public HessianProxyFactory()
	{
		this(Thread.currentThread().getContextClassLoader());
	}

	public HessianProxyFactory(ClassLoader loader)
	{
		_isOverloadEnabled = false;
		_isHessian2Reply = true;
		_isHessian2Request = false;
		_isChunkedPost = true;
		_isDebug = false;
		_readTimeout = -1L;
		_connectTimeout = -1L;
		_loader = loader;
		_resolver = new HessianProxyResolver(this);
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

	public String getBasicAuth()
	{
		if (_basicAuth != null)
			return _basicAuth;
		if (_user != null && _password != null)
			return (new StringBuilder()).append("Basic ").append(base64((new StringBuilder()).append(_user).append(":").append(_password).toString())).toString();
		else
			return null;
	}

	public void setConnectionFactory(HessianConnectionFactory factory)
	{
		_connFactory = factory;
	}

	public HessianConnectionFactory getConnectionFactory()
	{
		if (_connFactory == null)
		{
			_connFactory = createHessianConnectionFactory();
			_connFactory.setHessianProxyFactory(this);
		}
		return _connFactory;
	}

	public void setDebug(boolean isDebug)
	{
		_isDebug = isDebug;
	}

	public boolean isDebug()
	{
		return _isDebug;
	}

	public boolean isOverloadEnabled()
	{
		return _isOverloadEnabled;
	}

	public void setOverloadEnabled(boolean isOverloadEnabled)
	{
		_isOverloadEnabled = isOverloadEnabled;
	}

	public void setChunkedPost(boolean isChunked)
	{
		_isChunkedPost = isChunked;
	}

	public boolean isChunkedPost()
	{
		return _isChunkedPost;
	}

	public long getReadTimeout()
	{
		return _readTimeout;
	}

	public void setReadTimeout(long timeout)
	{
		_readTimeout = timeout;
	}

	public long getConnectTimeout()
	{
		return _connectTimeout;
	}

	public void setConnectTimeout(long timeout)
	{
		_connectTimeout = timeout;
	}

	public void setHessian2Reply(boolean isHessian2)
	{
		_isHessian2Reply = isHessian2;
	}

	public void setHessian2Request(boolean isHessian2)
	{
		_isHessian2Request = isHessian2;
		if (isHessian2)
			_isHessian2Reply = true;
	}

	public HessianRemoteResolver getRemoteResolver()
	{
		return _resolver;
	}

	public void setSerializerFactory(SerializerFactory factory)
	{
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory()
	{
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory(_loader);
		return _serializerFactory;
	}

	protected HessianConnectionFactory createHessianConnectionFactory()
	{
		String className;
		HessianConnectionFactory factory;
		className = System.getProperty(com/autohome/hessian/client/HessianConnectionFactory.getName());
		factory = null;
		if (className == null)
			break MISSING_BLOCK_LABEL_55;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class cl = Class.forName(className, false, loader);
		factory = (HessianConnectionFactory)cl.newInstance();
		return factory;
		Exception e;
		e;
		throw new RuntimeException(e);
		return new HessianURLConnectionFactory();
	}

	public Object create(String url)
		throws MalformedURLException, ClassNotFoundException
	{
		HessianMetaInfoAPI metaInfo = (HessianMetaInfoAPI)create(com/autohome/hessian/client/HessianMetaInfoAPI, url);
		String apiClassName = (String)metaInfo._hessian_getAttribute("java.api.class");
		if (apiClassName == null)
		{
			throw new HessianRuntimeException((new StringBuilder()).append(url).append(" has an unknown api.").toString());
		} else
		{
			Class apiClass = Class.forName(apiClassName, false, _loader);
			return create(apiClass, url);
		}
	}

	public Object create(Class api, String urlName)
		throws MalformedURLException
	{
		return create(api, urlName, _loader);
	}

	public Object create(Class api, String urlName, ClassLoader loader)
		throws MalformedURLException
	{
		URL url = new URL(urlName);
		return create(api, url, loader);
	}

	public Object create(Class api, URL url, ClassLoader loader)
	{
		if (api == null)
		{
			throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
		} else
		{
			java.lang.reflect.InvocationHandler handler = null;
			handler = new HessianProxy(url, this, api);
			return Proxy.newProxyInstance(loader, new Class[] {
				api, com/autohome/hessian/io/HessianRemoteObject
			}, handler);
		}
	}

	public Object create(Class api, URL url, ClassLoader loader, java.net.Proxy proxy)
	{
		if (api == null)
		{
			throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
		} else
		{
			java.lang.reflect.InvocationHandler handler = null;
			HessianProxy hessianProxy = new HessianProxy(url, this, api);
			hessianProxy.setProxy(proxy);
			handler = hessianProxy;
			return Proxy.newProxyInstance(loader, new Class[] {
				api, com/autohome/hessian/io/HessianRemoteObject
			}, handler);
		}
	}

	public AbstractHessianInput getHessianInput(InputStream is)
	{
		return getHessian2Input(is);
	}

	public AbstractHessianInput getHessian1Input(InputStream is)
	{
		if (_isDebug)
			is = new HessianDebugInputStream(is, new PrintWriter(System.out));
		AbstractHessianInput in = new HessianInput(is);
		in.setRemoteResolver(getRemoteResolver());
		in.setSerializerFactory(getSerializerFactory());
		return in;
	}

	public AbstractHessianInput getHessian2Input(InputStream is)
	{
		if (_isDebug)
			is = new HessianDebugInputStream(is, new PrintWriter(System.out));
		AbstractHessianInput in = new Hessian2Input(is);
		in.setRemoteResolver(getRemoteResolver());
		in.setSerializerFactory(getSerializerFactory());
		return in;
	}

	public AbstractHessianOutput getHessianOutput(OutputStream os)
	{
		AbstractHessianOutput out;
		if (_isHessian2Request)
		{
			out = new Hessian2Output(os);
		} else
		{
			HessianOutput out1 = new HessianOutput(os);
			out = out1;
			if (_isHessian2Reply)
				out1.setVersion(2);
		}
		out.setSerializerFactory(getSerializerFactory());
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
			throw new NamingException("`url' must be configured for HessianProxyFactory.");
		if (api == null)
		{
			throw new NamingException("`type' must be configured for HessianProxyFactory.");
		} else
		{
			Class apiClass = Class.forName(api, false, _loader);
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
