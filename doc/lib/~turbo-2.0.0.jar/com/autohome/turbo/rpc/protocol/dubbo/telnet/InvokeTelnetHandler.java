// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InvokeTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.lang.reflect.Method;
import java.util.*;

public class InvokeTelnetHandler
	implements TelnetHandler
{

	public InvokeTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		if (message == null || message.length() == 0)
			return "Please input method name, eg: \r\ninvoke xxxMethod(1234, \"abcd\", {\"prop\" : \"value\"})\r\ninvoke XxxService.xxxMethod(1234, \"abcd\", {\"prop\" : \"value\"})\r\ninvoke com.xxx.XxxService.xxxMethod(1234, \"abcd\", {\"prop\" : \"value\"})";
		StringBuilder buf = new StringBuilder();
		String service = (String)channel.getAttribute("telnet.service");
		if (service != null && service.length() > 0)
			buf.append((new StringBuilder()).append("Use default service ").append(service).append(".\r\n").toString());
		int i = message.indexOf("(");
		if (i < 0 || !message.endsWith(")"))
			return "Invalid parameters, format: service.method(args)";
		String method = message.substring(0, i).trim();
		String args = message.substring(i + 1, message.length() - 1).trim();
		i = method.lastIndexOf(".");
		if (i >= 0)
		{
			service = method.substring(0, i).trim();
			method = method.substring(i + 1).trim();
		}
		List list;
		try
		{
			list = (List)JSON.parse((new StringBuilder()).append("[").append(args).append("]").toString(), java/util/List);
		}
		catch (Throwable t)
		{
			return (new StringBuilder()).append("Invalid json argument, cause: ").append(t.getMessage()).toString();
		}
		Invoker invoker = null;
		Method invokeMethod = null;
		Iterator i$ = DubboProtocol.getDubboProtocol().getExporters().iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Exporter exporter = (Exporter)i$.next();
			if (service == null || service.length() == 0)
			{
				invokeMethod = findMethod(exporter, method, list);
				if (invokeMethod == null)
					continue;
				invoker = exporter.getInvoker();
				break;
			}
			if (!service.equals(exporter.getInvoker().getInterface().getSimpleName()) && !service.equals(exporter.getInvoker().getInterface().getName()) && !service.equals(exporter.getInvoker().getUrl().getPath()))
				continue;
			invokeMethod = findMethod(exporter, method, list);
			invoker = exporter.getInvoker();
			break;
		} while (true);
		if (invoker != null)
		{
			if (invokeMethod != null)
				try
				{
					Object array[] = PojoUtils.realize(list.toArray(), invokeMethod.getParameterTypes());
					RpcContext.getContext().setLocalAddress(channel.getLocalAddress()).setRemoteAddress(channel.getRemoteAddress());
					long start = System.currentTimeMillis();
					Object result = invoker.invoke(new RpcInvocation(invokeMethod, array)).recreate();
					long end = System.currentTimeMillis();
					buf.append(JSON.json(result));
					buf.append("\r\nelapsed: ");
					buf.append(end - start);
					buf.append(" ms.");
				}
				catch (Throwable t)
				{
					return (new StringBuilder()).append("Failed to invoke method ").append(invokeMethod.getName()).append(", cause: ").append(StringUtils.toString(t)).toString();
				}
			else
				buf.append((new StringBuilder()).append("No such method ").append(method).append(" in service ").append(service).toString());
		} else
		{
			buf.append((new StringBuilder()).append("No such service ").append(service).toString());
		}
		return buf.toString();
	}

	private static Method findMethod(Exporter exporter, String method, List args)
	{
		Invoker invoker = exporter.getInvoker();
		Method methods[] = invoker.getInterface().getMethods();
		Method invokeMethod = null;
		Method arr$[] = methods;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Method m = arr$[i$];
			if (!m.getName().equals(method) || m.getParameterTypes().length != args.size())
				continue;
			if (invokeMethod != null)
			{
				if (isMatch(invokeMethod.getParameterTypes(), args))
				{
					invokeMethod = m;
					break;
				}
			} else
			{
				invokeMethod = m;
			}
			invoker = exporter.getInvoker();
		}

		return invokeMethod;
	}

	private static boolean isMatch(Class types[], List args)
	{
		if (types.length != args.size())
			return false;
		for (int i = 0; i < types.length; i++)
		{
			Class type = types[i];
			Object arg = args.get(i);
			if (ReflectUtils.isPrimitive(arg.getClass()))
			{
				if (!ReflectUtils.isPrimitive(type))
					return false;
				continue;
			}
			if (arg instanceof Map)
			{
				String name = (String)((Map)arg).get("class");
				Class cls = arg.getClass();
				if (name != null && name.length() > 0)
					cls = ReflectUtils.forName(name);
				if (!type.isAssignableFrom(cls))
					return false;
				continue;
			}
			if (arg instanceof Collection)
			{
				if (!type.isArray() && !type.isAssignableFrom(arg.getClass()))
					return false;
				continue;
			}
			if (!type.isAssignableFrom(arg.getClass()))
				return false;
		}

		return true;
	}
}
