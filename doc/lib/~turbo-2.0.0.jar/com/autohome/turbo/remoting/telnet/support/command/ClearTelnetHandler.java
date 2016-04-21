// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClearTelnetHandler.java

package com.autohome.turbo.remoting.telnet.support.command;

import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;

public class ClearTelnetHandler
	implements TelnetHandler
{

	public ClearTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		int lines = 100;
		if (message.length() > 0)
		{
			if (!StringUtils.isInteger(message))
				return (new StringBuilder()).append("Illegal lines ").append(message).append(", must be integer.").toString();
			lines = Integer.parseInt(message);
		}
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < lines; i++)
			buf.append("\r\n");

		return buf.toString();
	}
}
