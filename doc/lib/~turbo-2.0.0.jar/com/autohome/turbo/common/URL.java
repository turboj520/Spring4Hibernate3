// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   URL.java

package com.autohome.turbo.common;

import com.autohome.turbo.common.utils.CollectionUtils;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.common.utils.StringUtils;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common:
//			Constants

public final class URL
	implements Serializable
{

	private static final long serialVersionUID = 0xe4734684a440eeb9L;
	private final String protocol;
	private final String username;
	private final String password;
	private final String host;
	private final int port;
	private final String path;
	private final Map parameters;
	private volatile transient Map numbers;
	private volatile transient Map urls;
	private volatile transient String ip;
	private volatile transient String full;
	private volatile transient String identity;
	private volatile transient String parameter;
	private volatile transient String string;

	protected URL()
	{
		protocol = null;
		username = null;
		password = null;
		host = null;
		port = 0;
		path = null;
		parameters = null;
	}

	public URL(String protocol, String host, int port)
	{
		this(protocol, null, null, host, port, null, (Map)null);
	}

	public URL(String protocol, String host, int port, String pairs[])
	{
		this(protocol, null, null, host, port, null, CollectionUtils.toStringMap(pairs));
	}

	public URL(String protocol, String host, int port, Map parameters)
	{
		this(protocol, null, null, host, port, null, parameters);
	}

	public URL(String protocol, String host, int port, String path)
	{
		this(protocol, null, null, host, port, path, (Map)null);
	}

	public transient URL(String protocol, String host, int port, String path, String pairs[])
	{
		this(protocol, null, null, host, port, path, CollectionUtils.toStringMap(pairs));
	}

	public URL(String protocol, String host, int port, String path, Map parameters)
	{
		this(protocol, null, null, host, port, path, parameters);
	}

	public URL(String protocol, String username, String password, String host, int port, String path)
	{
		this(protocol, username, password, host, port, path, (Map)null);
	}

	public transient URL(String protocol, String username, String password, String host, int port, String path, String pairs[])
	{
		this(protocol, username, password, host, port, path, CollectionUtils.toStringMap(pairs));
	}

	public URL(String protocol, String username, String password, String host, int port, String path, Map parameters)
	{
		if ((username == null || username.length() == 0) && password != null && password.length() > 0)
			throw new IllegalArgumentException("Invalid url, password without username!");
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port >= 0 ? port : 0;
		this.path = path;
		for (; path != null && path.startsWith("/"); path = path.substring(1));
		if (parameters == null)
			parameters = new HashMap();
		else
			parameters = new HashMap(parameters);
		this.parameters = Collections.unmodifiableMap(parameters);
	}

	public static URL valueOf(String url)
	{
		if (url == null || (url = url.trim()).length() == 0)
			throw new IllegalArgumentException("url == null");
		String protocol = null;
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		String path = null;
		Map parameters = null;
		int i = url.indexOf("?");
		if (i >= 0)
		{
			String parts[] = url.substring(i + 1).split("\\&");
			parameters = new HashMap();
			String arr$[] = parts;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String part = arr$[i$];
				part = part.trim();
				if (part.length() <= 0)
					continue;
				int j = part.indexOf('=');
				if (j >= 0)
					parameters.put(part.substring(0, j), part.substring(j + 1));
				else
					parameters.put(part, part);
			}

			url = url.substring(0, i);
		}
		i = url.indexOf("://");
		if (i >= 0)
		{
			if (i == 0)
				throw new IllegalStateException((new StringBuilder()).append("url missing protocol: \"").append(url).append("\"").toString());
			protocol = url.substring(0, i);
			url = url.substring(i + 3);
		} else
		{
			i = url.indexOf(":/");
			if (i >= 0)
			{
				if (i == 0)
					throw new IllegalStateException((new StringBuilder()).append("url missing protocol: \"").append(url).append("\"").toString());
				protocol = url.substring(0, i);
				url = url.substring(i + 1);
			}
		}
		i = url.indexOf("/");
		if (i >= 0)
		{
			path = url.substring(i + 1);
			url = url.substring(0, i);
		}
		i = url.indexOf("@");
		if (i >= 0)
		{
			username = url.substring(0, i);
			int j = username.indexOf(":");
			if (j >= 0)
			{
				password = username.substring(j + 1);
				username = username.substring(0, j);
			}
			url = url.substring(i + 1);
		}
		i = url.indexOf(":");
		if (i >= 0 && i < url.length() - 1)
		{
			port = Integer.parseInt(url.substring(i + 1));
			url = url.substring(0, i);
		}
		if (url.length() > 0)
			host = url;
		return new URL(protocol, username, password, host, port, path, parameters);
	}

	public String getProtocol()
	{
		return protocol;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public String getAuthority()
	{
		if ((username == null || username.length() == 0) && (password == null || password.length() == 0))
			return null;
		else
			return (new StringBuilder()).append(username != null ? username : "").append(":").append(password != null ? password : "").toString();
	}

	public String getHost()
	{
		return host;
	}

	public String getIp()
	{
		if (ip == null)
			ip = NetUtils.getIpByHost(host);
		return ip;
	}

	public int getPort()
	{
		return port;
	}

	public int getPort(int defaultPort)
	{
		return port > 0 ? port : defaultPort;
	}

	public String getAddress()
	{
		return port > 0 ? (new StringBuilder()).append(host).append(":").append(port).toString() : host;
	}

	public String getBackupAddress()
	{
		return getBackupAddress(0);
	}

	public String getBackupAddress(int defaultPort)
	{
		StringBuilder address = new StringBuilder(appendDefaultPort(getAddress(), defaultPort));
		String backups[] = getParameter("backup", new String[0]);
		if (backups != null && backups.length > 0)
		{
			String arr$[] = backups;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String backup = arr$[i$];
				address.append(",");
				address.append(appendDefaultPort(backup, defaultPort));
			}

		}
		return address.toString();
	}

	public List getBackupUrls()
	{
		List urls = new ArrayList();
		urls.add(this);
		String backups[] = getParameter("backup", new String[0]);
		if (backups != null && backups.length > 0)
		{
			String arr$[] = backups;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String backup = arr$[i$];
				urls.add(setAddress(backup));
			}

		}
		return urls;
	}

	private String appendDefaultPort(String address, int defaultPort)
	{
		if (address != null && address.length() > 0 && defaultPort > 0)
		{
			int i = address.indexOf(':');
			if (i < 0)
				return (new StringBuilder()).append(address).append(":").append(defaultPort).toString();
			if (Integer.parseInt(address.substring(i + 1)) == 0)
				return (new StringBuilder()).append(address.substring(0, i + 1)).append(defaultPort).toString();
		}
		return address;
	}

	public String getPath()
	{
		return path;
	}

	public String getAbsolutePath()
	{
		if (path != null && !path.startsWith("/"))
			return (new StringBuilder()).append("/").append(path).toString();
		else
			return path;
	}

	public URL setProtocol(String protocol)
	{
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setUsername(String username)
	{
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setPassword(String password)
	{
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setAddress(String address)
	{
		int i = address.lastIndexOf(':');
		int port = this.port;
		String host;
		if (i >= 0)
		{
			host = address.substring(0, i);
			port = Integer.parseInt(address.substring(i + 1));
		} else
		{
			host = address;
		}
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setHost(String host)
	{
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setPort(int port)
	{
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setPath(String path)
	{
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public Map getParameters()
	{
		return parameters;
	}

	public String getParameterAndDecoded(String key)
	{
		return getParameterAndDecoded(key, null);
	}

	public String getParameterAndDecoded(String key, String defaultValue)
	{
		return decode(getParameter(key, defaultValue));
	}

	public String getParameter(String key)
	{
		String value = (String)parameters.get(key);
		if (value == null || value.length() == 0)
			value = (String)parameters.get((new StringBuilder()).append("default.").append(key).toString());
		return value;
	}

	public String getParameter(String key, String defaultValue)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value;
	}

	public String[] getParameter(String key, String defaultValue[])
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Constants.COMMA_SPLIT_PATTERN.split(value);
	}

	private Map getNumbers()
	{
		if (numbers == null)
			numbers = new ConcurrentHashMap();
		return numbers;
	}

	private Map getUrls()
	{
		if (urls == null)
			urls = new ConcurrentHashMap();
		return urls;
	}

	public URL getUrlParameter(String key)
	{
		URL u = (URL)getUrls().get(key);
		if (u != null)
			return u;
		String value = getParameterAndDecoded(key);
		if (value == null || value.length() == 0)
		{
			return null;
		} else
		{
			u = valueOf(value);
			getUrls().put(key, u);
			return u;
		}
	}

	public double getParameter(String key, double defaultValue)
	{
		Number n = (Number)getNumbers().get(key);
		if (n != null)
			return n.doubleValue();
		String value = getParameter(key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			double d = Double.parseDouble(value);
			getNumbers().put(key, Double.valueOf(d));
			return d;
		}
	}

	public float getParameter(String key, float defaultValue)
	{
		Number n = (Number)getNumbers().get(key);
		if (n != null)
			return n.floatValue();
		String value = getParameter(key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			float f = Float.parseFloat(value);
			getNumbers().put(key, Float.valueOf(f));
			return f;
		}
	}

	public long getParameter(String key, long defaultValue)
	{
		Number n = (Number)getNumbers().get(key);
		if (n != null)
			return n.longValue();
		String value = getParameter(key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			long l = Long.parseLong(value);
			getNumbers().put(key, Long.valueOf(l));
			return l;
		}
	}

	public int getParameter(String key, int defaultValue)
	{
		Number n = (Number)getNumbers().get(key);
		if (n != null)
			return n.intValue();
		String value = getParameter(key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			int i = Integer.parseInt(value);
			getNumbers().put(key, Integer.valueOf(i));
			return i;
		}
	}

	public short getParameter(String key, short defaultValue)
	{
		Number n = (Number)getNumbers().get(key);
		if (n != null)
			return n.shortValue();
		String value = getParameter(key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			short s = Short.parseShort(value);
			getNumbers().put(key, Short.valueOf(s));
			return s;
		}
	}

	public byte getParameter(String key, byte defaultValue)
	{
		Number n = (Number)getNumbers().get(key);
		if (n != null)
			return n.byteValue();
		String value = getParameter(key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			byte b = Byte.parseByte(value);
			getNumbers().put(key, Byte.valueOf(b));
			return b;
		}
	}

	public float getPositiveParameter(String key, float defaultValue)
	{
		if (defaultValue <= 0.0F)
			throw new IllegalArgumentException("defaultValue <= 0");
		float value = getParameter(key, defaultValue);
		if (value <= 0.0F)
			return defaultValue;
		else
			return value;
	}

	public double getPositiveParameter(String key, double defaultValue)
	{
		if (defaultValue <= 0.0D)
			throw new IllegalArgumentException("defaultValue <= 0");
		double value = getParameter(key, defaultValue);
		if (value <= 0.0D)
			return defaultValue;
		else
			return value;
	}

	public long getPositiveParameter(String key, long defaultValue)
	{
		if (defaultValue <= 0L)
			throw new IllegalArgumentException("defaultValue <= 0");
		long value = getParameter(key, defaultValue);
		if (value <= 0L)
			return defaultValue;
		else
			return value;
	}

	public int getPositiveParameter(String key, int defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		int value = getParameter(key, defaultValue);
		if (value <= 0)
			return defaultValue;
		else
			return value;
	}

	public short getPositiveParameter(String key, short defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		short value = getParameter(key, defaultValue);
		if (value <= 0)
			return defaultValue;
		else
			return value;
	}

	public byte getPositiveParameter(String key, byte defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		byte value = getParameter(key, defaultValue);
		if (value <= 0)
			return defaultValue;
		else
			return value;
	}

	public char getParameter(String key, char defaultValue)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value.charAt(0);
	}

	public boolean getParameter(String key, boolean defaultValue)
	{
		String value = getParameter(key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}

	public boolean hasParameter(String key)
	{
		String value = getParameter(key);
		return value != null && value.length() > 0;
	}

	public String getMethodParameterAndDecoded(String method, String key)
	{
		return decode(getMethodParameter(method, key));
	}

	public String getMethodParameterAndDecoded(String method, String key, String defaultValue)
	{
		return decode(getMethodParameter(method, key, defaultValue));
	}

	public String getMethodParameter(String method, String key)
	{
		String value = (String)parameters.get((new StringBuilder()).append(method).append(".").append(key).toString());
		if (value == null || value.length() == 0)
			return getParameter(key);
		else
			return value;
	}

	public String getMethodParameter(String method, String key, String defaultValue)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value;
	}

	public double getMethodParameter(String method, String key, double defaultValue)
	{
		String methodKey = (new StringBuilder()).append(method).append(".").append(key).toString();
		Number n = (Number)getNumbers().get(methodKey);
		if (n != null)
			return (double)n.intValue();
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			double d = Double.parseDouble(value);
			getNumbers().put(methodKey, Double.valueOf(d));
			return d;
		}
	}

	public float getMethodParameter(String method, String key, float defaultValue)
	{
		String methodKey = (new StringBuilder()).append(method).append(".").append(key).toString();
		Number n = (Number)getNumbers().get(methodKey);
		if (n != null)
			return (float)n.intValue();
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			float f = Float.parseFloat(value);
			getNumbers().put(methodKey, Float.valueOf(f));
			return f;
		}
	}

	public long getMethodParameter(String method, String key, long defaultValue)
	{
		String methodKey = (new StringBuilder()).append(method).append(".").append(key).toString();
		Number n = (Number)getNumbers().get(methodKey);
		if (n != null)
			return (long)n.intValue();
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			long l = Long.parseLong(value);
			getNumbers().put(methodKey, Long.valueOf(l));
			return l;
		}
	}

	public int getMethodParameter(String method, String key, int defaultValue)
	{
		String methodKey = (new StringBuilder()).append(method).append(".").append(key).toString();
		Number n = (Number)getNumbers().get(methodKey);
		if (n != null)
			return n.intValue();
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			int i = Integer.parseInt(value);
			getNumbers().put(methodKey, Integer.valueOf(i));
			return i;
		}
	}

	public short getMethodParameter(String method, String key, short defaultValue)
	{
		String methodKey = (new StringBuilder()).append(method).append(".").append(key).toString();
		Number n = (Number)getNumbers().get(methodKey);
		if (n != null)
			return n.shortValue();
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			short s = Short.parseShort(value);
			getNumbers().put(methodKey, Short.valueOf(s));
			return s;
		}
	}

	public byte getMethodParameter(String method, String key, byte defaultValue)
	{
		String methodKey = (new StringBuilder()).append(method).append(".").append(key).toString();
		Number n = (Number)getNumbers().get(methodKey);
		if (n != null)
			return n.byteValue();
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
		{
			return defaultValue;
		} else
		{
			byte b = Byte.parseByte(value);
			getNumbers().put(methodKey, Byte.valueOf(b));
			return b;
		}
	}

	public double getMethodPositiveParameter(String method, String key, double defaultValue)
	{
		if (defaultValue <= 0.0D)
			throw new IllegalArgumentException("defaultValue <= 0");
		double value = getMethodParameter(method, key, defaultValue);
		if (value <= 0.0D)
			return defaultValue;
		else
			return value;
	}

	public float getMethodPositiveParameter(String method, String key, float defaultValue)
	{
		if (defaultValue <= 0.0F)
			throw new IllegalArgumentException("defaultValue <= 0");
		float value = getMethodParameter(method, key, defaultValue);
		if (value <= 0.0F)
			return defaultValue;
		else
			return value;
	}

	public long getMethodPositiveParameter(String method, String key, long defaultValue)
	{
		if (defaultValue <= 0L)
			throw new IllegalArgumentException("defaultValue <= 0");
		long value = getMethodParameter(method, key, defaultValue);
		if (value <= 0L)
			return defaultValue;
		else
			return value;
	}

	public int getMethodPositiveParameter(String method, String key, int defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		int value = getMethodParameter(method, key, defaultValue);
		if (value <= 0)
			return defaultValue;
		else
			return value;
	}

	public short getMethodPositiveParameter(String method, String key, short defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		short value = getMethodParameter(method, key, defaultValue);
		if (value <= 0)
			return defaultValue;
		else
			return value;
	}

	public byte getMethodPositiveParameter(String method, String key, byte defaultValue)
	{
		if (defaultValue <= 0)
			throw new IllegalArgumentException("defaultValue <= 0");
		byte value = getMethodParameter(method, key, defaultValue);
		if (value <= 0)
			return defaultValue;
		else
			return value;
	}

	public char getMethodParameter(String method, String key, char defaultValue)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return value.charAt(0);
	}

	public boolean getMethodParameter(String method, String key, boolean defaultValue)
	{
		String value = getMethodParameter(method, key);
		if (value == null || value.length() == 0)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}

	public boolean hasMethodParameter(String method, String key)
	{
		if (method == null)
		{
			String suffix = (new StringBuilder()).append(".").append(key).toString();
			for (Iterator i$ = parameters.keySet().iterator(); i$.hasNext();)
			{
				String fullKey = (String)i$.next();
				if (fullKey.endsWith(suffix))
					return true;
			}

			return false;
		}
		if (key == null)
		{
			String prefix = (new StringBuilder()).append(method).append(".").toString();
			for (Iterator i$ = parameters.keySet().iterator(); i$.hasNext();)
			{
				String fullKey = (String)i$.next();
				if (fullKey.startsWith(prefix))
					return true;
			}

			return false;
		} else
		{
			String value = getMethodParameter(method, key);
			return value != null && value.length() > 0;
		}
	}

	public boolean isLocalHost()
	{
		return NetUtils.isLocalHost(host) || getParameter("localhost", false);
	}

	public boolean isAnyHost()
	{
		return "0.0.0.0".equals(host) || getParameter("anyhost", false);
	}

	public URL addParameterAndEncoded(String key, String value)
	{
		if (value == null || value.length() == 0)
			return this;
		else
			return addParameter(key, encode(value));
	}

	public URL addParameter(String key, boolean value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, char value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, byte value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, short value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, int value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, long value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, float value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, double value)
	{
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, Enum value)
	{
		if (value == null)
			return this;
		else
			return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, Number value)
	{
		if (value == null)
			return this;
		else
			return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, CharSequence value)
	{
		if (value == null || value.length() == 0)
			return this;
		else
			return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, String value)
	{
		if (key == null || key.length() == 0 || value == null || value.length() == 0)
			return this;
		if (value.equals(getParameters().get(key)))
		{
			return this;
		} else
		{
			Map map = new HashMap(getParameters());
			map.put(key, value);
			return new URL(protocol, username, password, host, port, path, map);
		}
	}

	public URL addParameterIfAbsent(String key, String value)
	{
		if (key == null || key.length() == 0 || value == null || value.length() == 0)
			return this;
		if (hasParameter(key))
		{
			return this;
		} else
		{
			Map map = new HashMap(getParameters());
			map.put(key, value);
			return new URL(protocol, username, password, host, port, path, map);
		}
	}

	public URL addParameters(Map parameters)
	{
		if (parameters == null || parameters.size() == 0)
			return this;
		boolean hasAndEqual = true;
		Iterator i$ = parameters.entrySet().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			String value = (String)getParameters().get(entry.getKey());
			if ((value != null || entry.getValue() == null) && value.equals(entry.getValue()))
				continue;
			hasAndEqual = false;
			break;
		} while (true);
		if (hasAndEqual)
		{
			return this;
		} else
		{
			Map map = new HashMap(getParameters());
			map.putAll(parameters);
			return new URL(protocol, username, password, host, port, path, map);
		}
	}

	public URL addParametersIfAbsent(Map parameters)
	{
		if (parameters == null || parameters.size() == 0)
		{
			return this;
		} else
		{
			Map map = new HashMap(parameters);
			map.putAll(getParameters());
			return new URL(protocol, username, password, host, port, path, map);
		}
	}

	public transient URL addParameters(String pairs[])
	{
		if (pairs == null || pairs.length == 0)
			return this;
		if (pairs.length % 2 != 0)
			throw new IllegalArgumentException("Map pairs can not be odd number.");
		Map map = new HashMap();
		int len = pairs.length / 2;
		for (int i = 0; i < len; i++)
			map.put(pairs[2 * i], pairs[2 * i + 1]);

		return addParameters(map);
	}

	public URL addParameterString(String query)
	{
		if (query == null || query.length() == 0)
			return this;
		else
			return addParameters(StringUtils.parseQueryString(query));
	}

	public URL removeParameter(String key)
	{
		if (key == null || key.length() == 0)
			return this;
		else
			return removeParameters(new String[] {
				key
			});
	}

	public URL removeParameters(Collection keys)
	{
		if (keys == null || keys.size() == 0)
			return this;
		else
			return removeParameters((String[])keys.toArray(new String[0]));
	}

	public transient URL removeParameters(String keys[])
	{
		if (keys == null || keys.length == 0)
			return this;
		Map map = new HashMap(getParameters());
		String arr$[] = keys;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			String key = arr$[i$];
			map.remove(key);
		}

		if (map.size() == getParameters().size())
			return this;
		else
			return new URL(protocol, username, password, host, port, path, map);
	}

	public URL clearParameters()
	{
		return new URL(protocol, username, password, host, port, path, new HashMap());
	}

	public String getRawParameter(String key)
	{
		if ("protocol".equals(key))
			return protocol;
		if ("username".equals(key))
			return username;
		if ("password".equals(key))
			return password;
		if ("host".equals(key))
			return host;
		if ("port".equals(key))
			return String.valueOf(port);
		if ("path".equals(key))
			return path;
		else
			return getParameter(key);
	}

	public Map toMap()
	{
		Map map = new HashMap(parameters);
		if (protocol != null)
			map.put("protocol", protocol);
		if (username != null)
			map.put("username", username);
		if (password != null)
			map.put("password", password);
		if (host != null)
			map.put("host", host);
		if (port > 0)
			map.put("port", String.valueOf(port));
		if (path != null)
			map.put("path", path);
		return map;
	}

	public String toString()
	{
		if (string != null)
			return string;
		else
			return string = buildString(false, true, new String[0]);
	}

	public transient String toString(String parameters[])
	{
		return buildString(false, true, parameters);
	}

	public String toIdentityString()
	{
		if (identity != null)
			return identity;
		else
			return identity = buildString(true, false, new String[0]);
	}

	public transient String toIdentityString(String parameters[])
	{
		return buildString(true, false, parameters);
	}

	public String toFullString()
	{
		if (full != null)
			return full;
		else
			return full = buildString(true, true, new String[0]);
	}

	public transient String toFullString(String parameters[])
	{
		return buildString(true, true, parameters);
	}

	public String toParameterString()
	{
		if (parameter != null)
			return parameter;
		else
			return parameter = toParameterString(new String[0]);
	}

	public transient String toParameterString(String parameters[])
	{
		StringBuilder buf = new StringBuilder();
		buildParameters(buf, false, parameters);
		return buf.toString();
	}

	private void buildParameters(StringBuilder buf, boolean concat, String parameters[])
	{
		if (getParameters() != null && getParameters().size() > 0)
		{
			List includes = parameters != null && parameters.length != 0 ? Arrays.asList(parameters) : null;
			boolean first = true;
			Iterator i$ = (new TreeMap(getParameters())).entrySet().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				if (entry.getKey() != null && ((String)entry.getKey()).length() > 0 && (includes == null || includes.contains(entry.getKey())))
				{
					if (first)
					{
						if (concat)
							buf.append("?");
						first = false;
					} else
					{
						buf.append("&");
					}
					buf.append((String)entry.getKey());
					buf.append("=");
					buf.append(entry.getValue() != null ? ((String)entry.getValue()).trim() : "");
				}
			} while (true);
		}
	}

	private transient String buildString(boolean appendUser, boolean appendParameter, String parameters[])
	{
		return buildString(appendUser, appendParameter, false, false, parameters);
	}

	private transient String buildString(boolean appendUser, boolean appendParameter, boolean useIP, boolean useService, String parameters[])
	{
		StringBuilder buf = new StringBuilder();
		if (protocol != null && protocol.length() > 0)
		{
			buf.append(protocol);
			buf.append("://");
		}
		if (appendUser && username != null && username.length() > 0)
		{
			buf.append(username);
			if (password != null && password.length() > 0)
			{
				buf.append(":");
				buf.append(password);
			}
			buf.append("@");
		}
		String host;
		if (useIP)
			host = getIp();
		else
			host = getHost();
		if (host != null && host.length() > 0)
		{
			buf.append(host);
			if (port > 0)
			{
				buf.append(":");
				buf.append(port);
			}
		}
		String path;
		if (useService)
			path = getServiceKey();
		else
			path = getPath();
		if (path != null && path.length() > 0)
		{
			buf.append("/");
			buf.append(path);
		}
		if (appendParameter)
			buildParameters(buf, true, parameters);
		return buf.toString();
	}

	public java.net.URL toJavaURL()
	{
		return new java.net.URL(toString());
		MalformedURLException e;
		e;
		throw new IllegalStateException(e.getMessage(), e);
	}

	public InetSocketAddress toInetSocketAddress()
	{
		return new InetSocketAddress(host, port);
	}

	public String getServiceKey()
	{
		String inf = getServiceInterface();
		if (inf == null)
			return null;
		StringBuilder buf = new StringBuilder();
		String group = getParameter("group");
		if (group != null && group.length() > 0)
			buf.append(group).append("/");
		buf.append(inf);
		String version = getParameter("version");
		if (version != null && version.length() > 0)
			buf.append(":").append(version);
		return buf.toString();
	}

	public String toServiceString()
	{
		return buildString(true, false, true, true, new String[0]);
	}

	/**
	 * @deprecated Method getServiceName is deprecated
	 */

	public String getServiceName()
	{
		return getServiceInterface();
	}

	public String getServiceInterface()
	{
		return getParameter("interface", path);
	}

	public URL setServiceInterface(String service)
	{
		return addParameter("interface", service);
	}

	/**
	 * @deprecated Method getIntParameter is deprecated
	 */

	public int getIntParameter(String key)
	{
		return getParameter(key, 0);
	}

	/**
	 * @deprecated Method getIntParameter is deprecated
	 */

	public int getIntParameter(String key, int defaultValue)
	{
		return getParameter(key, defaultValue);
	}

	/**
	 * @deprecated Method getPositiveIntParameter is deprecated
	 */

	public int getPositiveIntParameter(String key, int defaultValue)
	{
		return getPositiveParameter(key, defaultValue);
	}

	/**
	 * @deprecated Method getBooleanParameter is deprecated
	 */

	public boolean getBooleanParameter(String key)
	{
		return getParameter(key, false);
	}

	/**
	 * @deprecated Method getBooleanParameter is deprecated
	 */

	public boolean getBooleanParameter(String key, boolean defaultValue)
	{
		return getParameter(key, defaultValue);
	}

	/**
	 * @deprecated Method getMethodIntParameter is deprecated
	 */

	public int getMethodIntParameter(String method, String key)
	{
		return getMethodParameter(method, key, 0);
	}

	/**
	 * @deprecated Method getMethodIntParameter is deprecated
	 */

	public int getMethodIntParameter(String method, String key, int defaultValue)
	{
		return getMethodParameter(method, key, defaultValue);
	}

	/**
	 * @deprecated Method getMethodPositiveIntParameter is deprecated
	 */

	public int getMethodPositiveIntParameter(String method, String key, int defaultValue)
	{
		return getMethodPositiveParameter(method, key, defaultValue);
	}

	/**
	 * @deprecated Method getMethodBooleanParameter is deprecated
	 */

	public boolean getMethodBooleanParameter(String method, String key)
	{
		return getMethodParameter(method, key, false);
	}

	/**
	 * @deprecated Method getMethodBooleanParameter is deprecated
	 */

	public boolean getMethodBooleanParameter(String method, String key, boolean defaultValue)
	{
		return getMethodParameter(method, key, defaultValue);
	}

	public static String encode(String value)
	{
		if (value == null || value.length() == 0)
			return "";
		return URLEncoder.encode(value, "UTF-8");
		UnsupportedEncodingException e;
		e;
		throw new RuntimeException(e.getMessage(), e);
	}

	public static String decode(String value)
	{
		if (value == null || value.length() == 0)
			return "";
		return URLDecoder.decode(value, "UTF-8");
		UnsupportedEncodingException e;
		e;
		throw new RuntimeException(e.getMessage(), e);
	}

	public int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = 31 * result + (host != null ? host.hashCode() : 0);
		result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (path != null ? path.hashCode() : 0);
		result = 31 * result + port;
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		result = 31 * result + (username != null ? username.hashCode() : 0);
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URL other = (URL)obj;
		if (host == null)
		{
			if (other.host != null)
				return false;
		} else
		if (!host.equals(other.host))
			return false;
		if (parameters == null)
		{
			if (other.parameters != null)
				return false;
		} else
		if (!parameters.equals(other.parameters))
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		} else
		if (!password.equals(other.password))
			return false;
		if (path == null)
		{
			if (other.path != null)
				return false;
		} else
		if (!path.equals(other.path))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null)
		{
			if (other.protocol != null)
				return false;
		} else
		if (!protocol.equals(other.protocol))
			return false;
		if (username == null)
		{
			if (other.username != null)
				return false;
		} else
		if (!username.equals(other.username))
			return false;
		return true;
	}
}
