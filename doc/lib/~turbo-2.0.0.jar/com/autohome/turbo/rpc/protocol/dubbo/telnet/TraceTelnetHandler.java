// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TraceTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.rpc.Exporter;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import com.autohome.turbo.rpc.protocol.dubbo.filter.TraceFilter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public class TraceTelnetHandler
	implements TelnetHandler
{

	public TraceTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		String service = (String)channel.getAttribute("telnet.service");
		if ((service == null || service.length() == 0) && (message == null || message.length() == 0))
			return "Please input service name, eg: \r\ntrace XxxService\r\ntrace XxxService xxxMethod\r\ntrace XxxService xxxMethod 10\r\nor \"cd XxxService\" firstly.";
		String parts[] = message.split("\\s+");
		String method;
		if (service == null || service.length() == 0)
		{
			service = parts.length <= 0 ? null : parts[0];
			method = parts.length <= 1 ? null : parts[1];
		} else
		{
			method = parts.length <= 0 ? null : parts[0];
		}
		String times;
		if (StringUtils.isInteger(method))
		{
			times = method;
			method = null;
		} else
		{
			times = parts.length <= 2 ? "1" : parts[2];
		}
		if (!StringUtils.isInteger(times))
			return (new StringBuilder()).append("Illegal times ").append(times).append(", must be integer.").toString();
		Invoker invoker = null;
		Iterator i$ = DubboProtocol.getDubboProtocol().getExporters().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Exporter exporter = (Exporter)i$.next();
			if (!service.equals(exporter.getInvoker().getInterface().getSimpleName()) && !service.equals(exporter.getInvoker().getInterface().getName()) && !service.equals(exporter.getInvoker().getUrl().getPath()))
				continue;
			invoker = exporter.getInvoker();
			break;
		} while (true);
		if (invoker != null)
		{
			if (method != null && method.length() > 0)
			{
				boolean found = false;
				Method arr$[] = invoker.getInterface().getMethods();
				int len$ = arr$.length;
				int i$ = 0;
				do
				{
					if (i$ >= len$)
						break;
					Method m = arr$[i$];
					if (m.getName().equals(method))
					{
						found = true;
						break;
					}
					i$++;
				} while (true);
				if (!found)
					return (new StringBuilder()).append("No such method ").append(method).append(" in class ").append(invoker.getInterface().getName()).toString();
			}
			TraceFilter.addTracer(invoker.getInterface(), method, channel, Integer.parseInt(times));
		} else
		{
			return (new StringBuilder()).append("No such service ").append(service).toString();
		}
		return null;
	}
}
