// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractProtocol.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.support.ProtocolUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractProtocol
	implements Protocol
{

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Map exporterMap = new ConcurrentHashMap();
	protected final Set invokers = new ConcurrentHashSet();

	public AbstractProtocol()
	{
	}

	protected static String serviceKey(URL url)
	{
		return ProtocolUtils.serviceKey(url);
	}

	protected static String serviceKey(int port, String serviceName, String serviceVersion, String serviceGroup)
	{
		return ProtocolUtils.serviceKey(port, serviceName, serviceVersion, serviceGroup);
	}

	public void destroy()
	{
		Iterator i$ = invokers.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Invoker invoker = (Invoker)i$.next();
			if (invoker != null)
			{
				invokers.remove(invoker);
				try
				{
					if (logger.isInfoEnabled())
						logger.info((new StringBuilder()).append("Destroy reference: ").append(invoker.getUrl()).toString());
					invoker.destroy();
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
			}
		} while (true);
		i$ = (new ArrayList(exporterMap.keySet())).iterator();
		do
		{
			if (!i$.hasNext())
				break;
			String key = (String)i$.next();
			Exporter exporter = (Exporter)exporterMap.remove(key);
			if (exporter != null)
				try
				{
					if (logger.isInfoEnabled())
						logger.info((new StringBuilder()).append("Unexport service: ").append(exporter.getInvoker().getUrl()).toString());
					exporter.unexport();
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
		} while (true);
	}

	protected static int getServerShutdownTimeout()
	{
		int timeout = 10000;
		String value = ConfigUtils.getProperty("dubbo.service.shutdown.wait");
		if (value != null && value.length() > 0)
		{
			try
			{
				timeout = Integer.parseInt(value);
			}
			catch (Exception e) { }
		} else
		{
			value = ConfigUtils.getProperty("dubbo.service.shutdown.wait.seconds");
			if (value != null && value.length() > 0)
				try
				{
					timeout = Integer.parseInt(value) * 1000;
				}
				catch (Exception e) { }
		}
		return timeout;
	}
}
