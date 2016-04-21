// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HelpTelnetHandler.java

package com.autohome.turbo.remoting.telnet.support.command;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.remoting.telnet.support.Help;
import com.autohome.turbo.remoting.telnet.support.TelnetUtils;
import java.util.*;

public class HelpTelnetHandler
	implements TelnetHandler
{

	private final ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/telnet/TelnetHandler);

	public HelpTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		if (message.length() > 0)
			if (!extensionLoader.hasExtension(message))
			{
				return (new StringBuilder()).append("No such command ").append(message).toString();
			} else
			{
				TelnetHandler handler = (TelnetHandler)extensionLoader.getExtension(message);
				Help help = (Help)handler.getClass().getAnnotation(com/autohome/turbo/remoting/telnet/support/Help);
				StringBuilder buf = new StringBuilder();
				buf.append("Command:\r\n    ");
				buf.append((new StringBuilder()).append(message).append(" ").append(help.parameter().replace("\r\n", " ").replace("\n", " ")).toString());
				buf.append("\r\nSummary:\r\n    ");
				buf.append(help.summary().replace("\r\n", " ").replace("\n", " "));
				buf.append("\r\nDetail:\r\n    ");
				buf.append(help.detail().replace("\r\n", "    \r\n").replace("\n", "    \n"));
				return buf.toString();
			}
		List table = new ArrayList();
		List handlers = extensionLoader.getActivateExtension(channel.getUrl(), "telnet");
		if (handlers != null && handlers.size() > 0)
		{
			List row;
			for (Iterator i$ = handlers.iterator(); i$.hasNext(); table.add(row))
			{
				TelnetHandler handler = (TelnetHandler)i$.next();
				Help help = (Help)handler.getClass().getAnnotation(com/autohome/turbo/remoting/telnet/support/Help);
				row = new ArrayList();
				String parameter = (new StringBuilder()).append(" ").append(extensionLoader.getExtensionName(handler)).append(" ").append(help == null ? "" : help.parameter().replace("\r\n", " ").replace("\n", " ")).toString();
				row.add(parameter.length() <= 50 ? ((Object) (parameter)) : ((Object) ((new StringBuilder()).append(parameter.substring(0, 50)).append("...").toString())));
				String summary = help == null ? "" : help.summary().replace("\r\n", " ").replace("\n", " ");
				row.add(summary.length() <= 50 ? ((Object) (summary)) : ((Object) ((new StringBuilder()).append(summary.substring(0, 50)).append("...").toString())));
			}

		}
		return (new StringBuilder()).append("Please input \"help [command]\" show detail.\r\n").append(TelnetUtils.toList(table)).toString();
	}
}
