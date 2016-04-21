// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClientsPageHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.page;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;
import com.autohome.turbo.remoting.exchange.ExchangeServer;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.util.*;

public class ClientsPageHandler
	implements PageHandler
{

	public ClientsPageHandler()
	{
	}

	public Page handle(URL url)
	{
		String port = url.getParameter("port");
		int p = port != null && port.length() != 0 ? Integer.parseInt(port) : 0;
		Collection servers = DubboProtocol.getDubboProtocol().getServers();
		ExchangeServer server = null;
		StringBuilder select = new StringBuilder();
		if (servers != null && servers.size() > 0)
			if (servers.size() == 1)
			{
				server = (ExchangeServer)servers.iterator().next();
				String address = server.getUrl().getAddress();
				select.append((new StringBuilder()).append(" &gt; ").append(NetUtils.getHostName(address)).append("/").append(address).toString());
			} else
			{
				select.append(" &gt; <select onchange=\"window.location.href='clients.html?port=' + this.value;\">");
				for (Iterator i$ = servers.iterator(); i$.hasNext(); select.append("</option>"))
				{
					ExchangeServer s = (ExchangeServer)i$.next();
					int sp = s.getUrl().getPort();
					select.append("<option value=\">");
					select.append(sp);
					if (p == 0 && server == null || p == sp)
					{
						server = s;
						select.append("\" selected=\"selected");
					}
					select.append("\">");
					select.append(s.getUrl().getAddress());
				}

				select.append("</select>");
			}
		List rows = new ArrayList();
		if (server != null)
		{
			Collection channels = server.getExchangeChannels();
			List row;
			for (Iterator i$ = channels.iterator(); i$.hasNext(); rows.add(row))
			{
				ExchangeChannel c = (ExchangeChannel)i$.next();
				row = new ArrayList();
				String address = NetUtils.toAddressString(c.getRemoteAddress());
				row.add((new StringBuilder()).append(NetUtils.getHostName(address)).append("/").append(address).toString());
			}

		}
		return new Page((new StringBuilder()).append("<a href=\"servers.html\">Servers</a>").append(select.toString()).append(" &gt; Clients").toString(), (new StringBuilder()).append("Clients (").append(rows.size()).append(")").toString(), new String[] {
			"Client Address:"
		}, rows);
	}
}
