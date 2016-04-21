// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ProtocolUtils.java

package com.autohome.turbo.rpc.support;

import com.autohome.turbo.common.URL;

public class ProtocolUtils
{

	private ProtocolUtils()
	{
	}

	public static String serviceKey(URL url)
	{
		return serviceKey(url.getPort(), url.getPath(), url.getParameter("version"), url.getParameter("group"));
	}

	public static String serviceKey(int port, String serviceName, String serviceVersion, String serviceGroup)
	{
		StringBuilder buf = new StringBuilder();
		if (serviceGroup != null && serviceGroup.length() > 0)
		{
			buf.append(serviceGroup);
			buf.append("/");
		}
		buf.append(serviceName);
		if (serviceVersion != null && serviceVersion.length() > 0 && !"0.0.0".equals(serviceVersion))
		{
			buf.append(":");
			buf.append(serviceVersion);
		}
		buf.append(":");
		buf.append(port);
		return buf.toString();
	}

	public static boolean isGeneric(String generic)
	{
		return generic != null && !"".equals(generic) && ("true".equalsIgnoreCase(generic) || "nativejava".equalsIgnoreCase(generic) || "bean".equalsIgnoreCase(generic));
	}

	public static boolean isDefaultGenericSerialization(String generic)
	{
		return isGeneric(generic) && "true".equalsIgnoreCase(generic);
	}

	public static boolean isJavaGenericSerialization(String generic)
	{
		return isGeneric(generic) && "nativejava".equalsIgnoreCase(generic);
	}

	public static boolean isBeanGenericSerialization(String generic)
	{
		return isGeneric(generic) && "bean".equals(generic);
	}
}
