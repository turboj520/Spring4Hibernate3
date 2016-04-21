// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CountTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.remoting.telnet.support.TelnetUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.lang.reflect.Method;
import java.util.*;

public class CountTelnetHandler
	implements TelnetHandler
{

	public CountTelnetHandler()
	{
	}

	public String telnet(final Channel channel, String message)
	{
		String service = (String)channel.getAttribute("telnet.service");
		if ((service == null || service.length() == 0) && (message == null || message.length() == 0))
			return "Please input service name, eg: \r\ncount XxxService\r\ncount XxxService xxxMethod\r\ncount XxxService xxxMethod 10\r\nor \"cd XxxService\" firstly.";
		StringBuilder buf = new StringBuilder();
		if (service != null && service.length() > 0)
			buf.append((new StringBuilder()).append("Use default service ").append(service).append(".\r\n").toString());
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
		final int t = Integer.parseInt(times);
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
			if (t > 0)
			{
				final String mtd = method;
				final Invoker inv = invoker;
				final String prompt = channel.getUrl().getParameter("prompt", "telnet");
				Thread thread = new Thread(new Runnable() {

					final int val$t;
					final Invoker val$inv;
					final String val$mtd;
					final Channel val$channel;
					final String val$prompt;
					final CountTelnetHandler this$0;

					public void run()
					{
						for (int i = 0; i < t; i++)
						{
							String result = count(inv, mtd);
							try
							{
								channel.send((new StringBuilder()).append("\r\n").append(result).toString());
							}
							catch (RemotingException e1)
							{
								return;
							}
							if (i >= t - 1)
								continue;
							try
							{
								Thread.sleep(1000L);
							}
							catch (InterruptedException e) { }
						}

						try
						{
							channel.send((new StringBuilder()).append("\r\n").append(prompt).append("> ").toString());
						}
						catch (RemotingException e1)
						{
							return;
						}
					}

			
			{
				this$0 = CountTelnetHandler.this;
				t = i;
				inv = invoker;
				mtd = s;
				channel = channel1;
				prompt = s1;
				super();
			}
				}, "TelnetCount");
				thread.setDaemon(true);
				thread.start();
			}
		} else
		{
			buf.append((new StringBuilder()).append("No such service ").append(service).toString());
		}
		return buf.toString();
	}

	private String count(Invoker invoker, String method)
	{
		URL url = invoker.getUrl();
		List table = new ArrayList();
		List header = new ArrayList();
		header.add("method");
		header.add("total");
		header.add("failed");
		header.add("active");
		header.add("average");
		header.add("max");
		if (method == null || method.length() == 0)
		{
			Method arr$[] = invoker.getInterface().getMethods();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Method m = arr$[i$];
				RpcStatus count = RpcStatus.getStatus(url, m.getName());
				List row = new ArrayList();
				row.add(m.getName());
				row.add(String.valueOf(count.getTotal()));
				row.add(String.valueOf(count.getFailed()));
				row.add(String.valueOf(count.getActive()));
				row.add((new StringBuilder()).append(String.valueOf(count.getSucceededAverageElapsed())).append("ms").toString());
				row.add((new StringBuilder()).append(String.valueOf(count.getSucceededMaxElapsed())).append("ms").toString());
				table.add(row);
			}

		} else
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
			if (found)
			{
				RpcStatus count = RpcStatus.getStatus(url, method);
				List row = new ArrayList();
				row.add(method);
				row.add(String.valueOf(count.getTotal()));
				row.add(String.valueOf(count.getFailed()));
				row.add(String.valueOf(count.getActive()));
				row.add((new StringBuilder()).append(String.valueOf(count.getSucceededAverageElapsed())).append("ms").toString());
				row.add((new StringBuilder()).append(String.valueOf(count.getSucceededMaxElapsed())).append("ms").toString());
				table.add(row);
			} else
			{
				return (new StringBuilder()).append("No such method ").append(method).append(" in class ").append(invoker.getInterface().getName()).toString();
			}
		}
		return TelnetUtils.toTable(header, table);
	}

}
