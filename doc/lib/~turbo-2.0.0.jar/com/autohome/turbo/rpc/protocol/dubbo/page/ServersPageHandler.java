// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServersPageHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.page;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import com.autohome.turbo.remoting.exchange.ExchangeServer;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.util.*;

public class ServersPageHandler
	implements PageHandler
{

	public ServersPageHandler()
	{
	}

	public Page handle(URL url)
	{
		List rows = new ArrayList();
		Collection servers = DubboProtocol.getDubboProtocol().getServers();
		int clientCount = 0;
		if (servers != null && servers.size() > 0)
		{
			List row;
			for (Iterator i$ = servers.iterator(); i$.hasNext(); rows.add(row))
			{
				ExchangeServer s = (ExchangeServer)i$.next();
				row = new ArrayList();
				String address = s.getUrl().getAddress();
				row.add((new StringBuilder()).append(NetUtils.getHostName(address)).append("/").append(address).toString());
				int clientSize = s.getExchangeChannels().size();
				clientCount += clientSize;
				row.add((new StringBuilder()).append("<a href=\"clients.html?port=").append(s.getUrl().getPort()).append("\">Clients(").append(clientSize).append(")</a>").toString());
			}

		}
		return new Page("Servers", (new StringBuilder()).append("Servers (").append(rows.size()).append(")").toString(), new String[] {
			"Server Address:", (new StringBuilder()).append("Clients(").append(clientCount).append(")").toString()
		}, rows);
	}
}
