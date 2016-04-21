// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TelnetHandlerAdapter.java

package com.autohome.turbo.remoting.telnet.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import com.autohome.turbo.remoting.transport.ChannelHandlerAdapter;

public class TelnetHandlerAdapter extends ChannelHandlerAdapter
	implements TelnetHandler
{

	private final ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/telnet/TelnetHandler);

	public TelnetHandlerAdapter()
	{
	}

	public String telnet(Channel channel, String message)
		throws RemotingException
	{
		String prompt;
		boolean noprompt;
		StringBuilder buf;
		String command;
		prompt = channel.getUrl().getParameterAndDecoded("prompt", "dubbo>");
		noprompt = message.contains("--no-prompt");
		message = message.replace("--no-prompt", "");
		buf = new StringBuilder();
		message = message.trim();
		if (message.length() > 0)
		{
			int i = message.indexOf(' ');
			if (i > 0)
			{
				command = message.substring(0, i).trim();
				message = message.substring(i + 1).trim();
			} else
			{
				command = message;
				message = "";
			}
		} else
		{
			command = "";
		}
		if (command.length() <= 0)
			break MISSING_BLOCK_LABEL_196;
		if (!extensionLoader.hasExtension(command))
			break MISSING_BLOCK_LABEL_180;
		String result = ((TelnetHandler)extensionLoader.getExtension(command)).telnet(channel, message);
		if (result == null)
			return null;
		try
		{
			buf.append(result);
		}
		catch (Throwable t)
		{
			buf.append(t.getMessage());
		}
		break MISSING_BLOCK_LABEL_196;
		buf.append("Unsupported command: ");
		buf.append(command);
		if (buf.length() > 0)
			buf.append("\r\n");
		if (prompt != null && prompt.length() > 0 && !noprompt)
			buf.append(prompt);
		return buf.toString();
	}
}
