// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ListTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.rpc.Exporter;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public class ListTelnetHandler
	implements TelnetHandler
{

	public ListTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		StringBuilder buf = new StringBuilder();
		String service = null;
		boolean detail = false;
		if (message.length() > 0)
		{
			String parts[] = message.split("\\s+");
			String arr$[] = parts;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String part = arr$[i$];
				if ("-l".equals(part))
				{
					detail = true;
				} else
				{
					if (service != null && service.length() > 0)
						return (new StringBuilder()).append("Invaild parameter ").append(part).toString();
					service = part;
				}
			}

		} else
		{
			service = (String)channel.getAttribute("telnet.service");
			if (service != null && service.length() > 0)
				buf.append((new StringBuilder()).append("Use default service ").append(service).append(".\r\n").toString());
		}
		if (service == null || service.length() == 0)
		{
			Iterator i$ = DubboProtocol.getDubboProtocol().getExporters().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Exporter exporter = (Exporter)i$.next();
				if (buf.length() > 0)
					buf.append("\r\n");
				buf.append(exporter.getInvoker().getInterface().getName());
				if (detail)
				{
					buf.append(" -> ");
					buf.append(exporter.getInvoker().getUrl());
				}
			} while (true);
		} else
		{
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
				Method methods[] = invoker.getInterface().getMethods();
				Method arr$[] = methods;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Method method = arr$[i$];
					if (buf.length() > 0)
						buf.append("\r\n");
					if (detail)
						buf.append(ReflectUtils.getName(method));
					else
						buf.append(method.getName());
				}

			} else
			{
				buf.append((new StringBuilder()).append("No such service ").append(service).toString());
			}
		}
		return buf.toString();
	}
}
