// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StatusTelnetHandler.java

package com.autohome.turbo.remoting.telnet.support.command;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.common.status.support.StatusUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.remoting.telnet.support.TelnetUtils;
import java.util.*;
import java.util.regex.Pattern;

public class StatusTelnetHandler
	implements TelnetHandler
{

	private final ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/status/StatusChecker);

	public StatusTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		if (message.equals("-l"))
		{
			List checkers = extensionLoader.getActivateExtension(channel.getUrl(), "status");
			String header[] = {
				"resource", "status", "message"
			};
			List table = new ArrayList();
			Map statuses = new HashMap();
			if (checkers != null && checkers.size() > 0)
			{
				Iterator i$ = checkers.iterator();
				do
				{
					if (!i$.hasNext())
						break;
					StatusChecker checker = (StatusChecker)i$.next();
					String name = extensionLoader.getExtensionName(checker);
					Status stat;
					try
					{
						stat = checker.check();
					}
					catch (Throwable t)
					{
						stat = new Status(com.autohome.turbo.common.status.Status.Level.ERROR, t.getMessage());
					}
					statuses.put(name, stat);
					if (stat.getLevel() != null && stat.getLevel() != com.autohome.turbo.common.status.Status.Level.UNKNOWN)
					{
						List row = new ArrayList();
						row.add(name);
						row.add(String.valueOf(stat.getLevel()));
						row.add(stat.getMessage() != null ? ((Object) (stat.getMessage())) : "");
						table.add(row);
					}
				} while (true);
			}
			Status stat = StatusUtils.getSummaryStatus(statuses);
			List row = new ArrayList();
			row.add("summary");
			row.add(String.valueOf(stat.getLevel()));
			row.add(stat.getMessage());
			table.add(row);
			return TelnetUtils.toTable(header, table);
		}
		if (message.length() > 0)
			return (new StringBuilder()).append("Unsupported parameter ").append(message).append(" for status.").toString();
		String status = channel.getUrl().getParameter("status");
		Map statuses = new HashMap();
		if (status != null && status.length() > 0)
		{
			String ss[] = Constants.COMMA_SPLIT_PATTERN.split(status);
			String arr$[] = ss;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				String s = arr$[i$];
				StatusChecker handler = (StatusChecker)extensionLoader.getExtension(s);
				Status stat;
				try
				{
					stat = handler.check();
				}
				catch (Throwable t)
				{
					stat = new Status(com.autohome.turbo.common.status.Status.Level.ERROR, t.getMessage());
				}
				statuses.put(s, stat);
			}

		}
		Status stat = StatusUtils.getSummaryStatus(statuses);
		return String.valueOf(stat.getLevel());
	}
}
