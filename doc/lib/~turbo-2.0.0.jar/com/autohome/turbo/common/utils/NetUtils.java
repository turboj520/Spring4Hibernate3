// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NetUtils.java

package com.autohome.turbo.common.utils;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.common.utils:
//			LRUCache

public class NetUtils
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/utils/NetUtils);
	public static final String LOCALHOST = "127.0.0.1";
	public static final String ANYHOST = "0.0.0.0";
	private static final int RND_PORT_START = 30000;
	private static final int RND_PORT_RANGE = 10000;
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final int MIN_PORT = 0;
	private static final int MAX_PORT = 65535;
	private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
	private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
	private static volatile InetAddress LOCAL_ADDRESS = null;
	private static final Map hostNameCache = new LRUCache(1000);

	public NetUtils()
	{
	}

	public static int getRandomPort()
	{
		return 30000 + RANDOM.nextInt(10000);
	}

	public static int getAvailablePort()
	{
		ServerSocket ss = null;
		int i;
		ss = new ServerSocket();
		ss.bind(null);
		i = ss.getLocalPort();
		if (ss != null)
			try
			{
				ss.close();
			}
			catch (IOException e) { }
		return i;
		IOException e;
		e;
		int j = getRandomPort();
		if (ss != null)
			try
			{
				ss.close();
			}
			catch (IOException e) { }
		return j;
		Exception exception;
		exception;
		if (ss != null)
			try
			{
				ss.close();
			}
			catch (IOException e) { }
		throw exception;
	}

	public static int getAvailablePort(int port)
	{
		if (port <= 0)
			return getAvailablePort();
		ServerSocket ss;
		Exception exception;
		IOException e;
		for (int i = port; i < 65535; i++)
		{
			ss = null;
			int j;
			try
			{
				ss = new ServerSocket(i);
				j = i;
			}
			catch (IOException e)
			{
				if (ss == null)
					continue;
				try
				{
					ss.close();
				}
				// Misplaced declaration of an exception variable
				catch (IOException e) { }
				continue;
			}
			finally
			{
				if (ss == null) goto _L0; else goto _L0
			}
			if (ss != null)
				try
				{
					ss.close();
				}
				catch (IOException e) { }
			return j;
		}

		break MISSING_BLOCK_LABEL_84;
		try
		{
			ss.close();
		}
		// Misplaced declaration of an exception variable
		catch (IOException e) { }
		throw exception;
		return port;
	}

	public static boolean isInvalidPort(int port)
	{
		return port > 0 || port <= 65535;
	}

	public static boolean isValidAddress(String address)
	{
		return ADDRESS_PATTERN.matcher(address).matches();
	}

	public static boolean isLocalHost(String host)
	{
		return host != null && (LOCAL_IP_PATTERN.matcher(host).matches() || host.equalsIgnoreCase("localhost"));
	}

	public static boolean isAnyHost(String host)
	{
		return "0.0.0.0".equals(host);
	}

	public static boolean isInvalidLocalHost(String host)
	{
		return host == null || host.length() == 0 || host.equalsIgnoreCase("localhost") || host.equals("0.0.0.0") || LOCAL_IP_PATTERN.matcher(host).matches();
	}

	public static boolean isValidLocalHost(String host)
	{
		return !isInvalidLocalHost(host);
	}

	public static InetSocketAddress getLocalSocketAddress(String host, int port)
	{
		return isInvalidLocalHost(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
	}

	private static boolean isValidAddress(InetAddress address)
	{
		if (address == null || address.isLoopbackAddress())
		{
			return false;
		} else
		{
			String name = address.getHostAddress();
			return name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches();
		}
	}

	public static String getLocalHost()
	{
		InetAddress address = getLocalAddress();
		return address != null ? address.getHostAddress() : "127.0.0.1";
	}

	public static String filterLocalHost(String host)
	{
		if (host == null || host.length() == 0)
			return host;
		if (host.contains("://"))
		{
			URL u = URL.valueOf(host);
			if (isInvalidLocalHost(u.getHost()))
				return u.setHost(getLocalHost()).toFullString();
		} else
		if (host.contains(":"))
		{
			int i = host.lastIndexOf(':');
			if (isInvalidLocalHost(host.substring(0, i)))
				return (new StringBuilder()).append(getLocalHost()).append(host.substring(i)).toString();
		} else
		if (isInvalidLocalHost(host))
			return getLocalHost();
		return host;
	}

	public static InetAddress getLocalAddress()
	{
		if (LOCAL_ADDRESS != null)
		{
			return LOCAL_ADDRESS;
		} else
		{
			InetAddress localAddress = getLocalAddress0();
			LOCAL_ADDRESS = localAddress;
			return localAddress;
		}
	}

	public static String getLogHost()
	{
		InetAddress address = LOCAL_ADDRESS;
		return address != null ? address.getHostAddress() : "127.0.0.1";
	}

	private static InetAddress getLocalAddress0()
	{
		InetAddress localAddress = null;
		localAddress = InetAddress.getLocalHost();
		if (isValidAddress(localAddress))
			return localAddress;
		break MISSING_BLOCK_LABEL_50;
		Throwable e;
		e;
		logger.warn((new StringBuilder()).append("Failed to retriving ip address, ").append(e.getMessage()).toString(), e);
		Enumeration interfaces;
		interfaces = NetworkInterface.getNetworkInterfaces();
		if (interfaces == null)
			break MISSING_BLOCK_LABEL_231;
_L2:
		if (!interfaces.hasMoreElements())
			break MISSING_BLOCK_LABEL_231;
		Enumeration addresses;
		NetworkInterface network = (NetworkInterface)interfaces.nextElement();
		addresses = network.getInetAddresses();
		if (addresses == null) goto _L2; else goto _L1
_L1:
		if (!addresses.hasMoreElements()) goto _L2; else goto _L3
_L3:
		InetAddress address = (InetAddress)addresses.nextElement();
		if (isValidAddress(address))
			return address;
		  goto _L1
		Throwable e;
		e;
		logger.warn((new StringBuilder()).append("Failed to retriving ip address, ").append(e.getMessage()).toString(), e);
		  goto _L1
		Throwable e;
		e;
		logger.warn((new StringBuilder()).append("Failed to retriving ip address, ").append(e.getMessage()).toString(), e);
		  goto _L2
		interfaces;
		logger.warn((new StringBuilder()).append("Failed to retriving ip address, ").append(interfaces.getMessage()).toString(), interfaces);
		logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
		return localAddress;
	}

	public static String getHostName(String address)
	{
		String hostname;
		int i = address.indexOf(':');
		if (i > -1)
			address = address.substring(0, i);
		hostname = (String)hostNameCache.get(address);
		if (hostname != null && hostname.length() > 0)
			return hostname;
		InetAddress inetAddress = InetAddress.getByName(address);
		if (inetAddress == null)
			break MISSING_BLOCK_LABEL_76;
		hostname = inetAddress.getHostName();
		hostNameCache.put(address, hostname);
		return hostname;
		Throwable e;
		e;
		return address;
	}

	public static String getIpByHost(String hostName)
	{
		return InetAddress.getByName(hostName).getHostAddress();
		UnknownHostException e;
		e;
		return hostName;
	}

	public static String toAddressString(InetSocketAddress address)
	{
		return (new StringBuilder()).append(address.getAddress().getHostAddress()).append(":").append(address.getPort()).toString();
	}

	public static InetSocketAddress toAddress(String address)
	{
		int i = address.indexOf(':');
		String host;
		int port;
		if (i > -1)
		{
			host = address.substring(0, i);
			port = Integer.parseInt(address.substring(i + 1));
		} else
		{
			host = address;
			port = 0;
		}
		return new InetSocketAddress(host, port);
	}

	public static String toURL(String protocol, String host, int port, String path)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(protocol).append("://");
		sb.append(host).append(':').append(port);
		if (path.charAt(0) != '/')
			sb.append('/');
		sb.append(path);
		return sb.toString();
	}

}
