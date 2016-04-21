// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChangeTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.rpc.Exporter;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.util.Collection;
import java.util.Iterator;

public class ChangeTelnetHandler
	implements TelnetHandler
{

	public static final String SERVICE_KEY = "telnet.service";

	public ChangeTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		if (message == null || message.length() == 0)
			return "Please input service name, eg: \r\ncd XxxService\r\ncd com.xxx.XxxService";
		StringBuilder buf = new StringBuilder();
		if (message.equals("/") || message.equals(".."))
		{
			String service = (String)channel.getAttribute("telnet.service");
			channel.removeAttribute("telnet.service");
			buf.append((new StringBuilder()).append("Cancelled default service ").append(service).append(".").toString());
		} else
		{
			boolean found = false;
			Iterator i$ = DubboProtocol.getDubboProtocol().getExporters().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Exporter exporter = (Exporter)i$.next();
				if (!message.equals(exporter.getInvoker().getInterface().getSimpleName()) && !message.equals(exporter.getInvoker().getInterface().getName()) && !message.equals(exporter.getInvoker().getUrl().getPath()))
					continue;
				found = true;
				break;
			} while (true);
			if (found)
			{
				channel.setAttribute("telnet.service", message);
				buf.append((new StringBuilder()).append("Used the ").append(message).append(" as default.\r\nYou can cancel default service by command: cd /").toString());
			} else
			{
				buf.append((new StringBuilder()).append("No such service ").append(message).toString());
			}
		}
		return buf.toString();
	}
}
