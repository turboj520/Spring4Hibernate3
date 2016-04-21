// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PortTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;
import com.autohome.turbo.remoting.exchange.ExchangeServer;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.util.Collection;
import java.util.Iterator;

public class PortTelnetHandler
	implements TelnetHandler
{

	public PortTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		StringBuilder buf = new StringBuilder();
		String port = null;
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
					continue;
				}
				if (!StringUtils.isInteger(part))
					return (new StringBuilder()).append("Illegal port ").append(part).append(", must be integer.").toString();
				port = part;
			}

		}
		if (port == null || port.length() == 0)
		{
			for (Iterator i$ = DubboProtocol.getDubboProtocol().getServers().iterator(); i$.hasNext();)
			{
				ExchangeServer server = (ExchangeServer)i$.next();
				if (buf.length() > 0)
					buf.append("\r\n");
				if (detail)
					buf.append((new StringBuilder()).append(server.getUrl().getProtocol()).append("://").append(server.getUrl().getAddress()).toString());
				else
					buf.append(server.getUrl().getPort());
			}

		} else
		{
			int p = Integer.parseInt(port);
			ExchangeServer server = null;
			Iterator i$ = DubboProtocol.getDubboProtocol().getServers().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				ExchangeServer s = (ExchangeServer)i$.next();
				if (p != s.getUrl().getPort())
					continue;
				server = s;
				break;
			} while (true);
			if (server != null)
			{
				Collection channels = server.getExchangeChannels();
				for (Iterator i$ = channels.iterator(); i$.hasNext();)
				{
					ExchangeChannel c = (ExchangeChannel)i$.next();
					if (buf.length() > 0)
						buf.append("\r\n");
					if (detail)
						buf.append((new StringBuilder()).append(c.getRemoteAddress()).append(" -> ").append(c.getLocalAddress()).toString());
					else
						buf.append(c.getRemoteAddress());
				}

			} else
			{
				buf.append((new StringBuilder()).append("No such port ").append(port).toString());
			}
		}
		return buf.toString();
	}
}
