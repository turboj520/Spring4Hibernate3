// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServerStatusChecker.java

package com.autohome.turbo.rpc.protocol.dubbo.status;

import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.remoting.exchange.ExchangeServer;
import com.autohome.turbo.rpc.protocol.dubbo.DubboProtocol;
import java.util.Collection;
import java.util.Iterator;

public class ServerStatusChecker
	implements StatusChecker
{

	public ServerStatusChecker()
	{
	}

	public Status check()
	{
		Collection servers = DubboProtocol.getDubboProtocol().getServers();
		if (servers == null || servers.size() == 0)
			return new Status(com.autohome.turbo.common.status.Status.Level.UNKNOWN);
		com.autohome.turbo.common.status.Status.Level level = com.autohome.turbo.common.status.Status.Level.OK;
		StringBuilder buf = new StringBuilder();
		for (Iterator i$ = servers.iterator(); i$.hasNext(); buf.append(")"))
		{
			ExchangeServer server = (ExchangeServer)i$.next();
			if (!server.isBound())
			{
				level = com.autohome.turbo.common.status.Status.Level.ERROR;
				buf.setLength(0);
				buf.append(server.getLocalAddress());
				break;
			}
			if (buf.length() > 0)
				buf.append(",");
			buf.append(server.getLocalAddress());
			buf.append("(clients:");
			buf.append(server.getChannels().size());
		}

		return new Status(level, buf.toString());
	}
}
