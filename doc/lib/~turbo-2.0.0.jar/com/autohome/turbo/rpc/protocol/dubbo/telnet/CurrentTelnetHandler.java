// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CurrentTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;

public class CurrentTelnetHandler
	implements TelnetHandler
{

	public CurrentTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		if (message.length() > 0)
			return (new StringBuilder()).append("Unsupported parameter ").append(message).append(" for pwd.").toString();
		String service = (String)channel.getAttribute("telnet.service");
		StringBuilder buf = new StringBuilder();
		if (service == null || service.length() == 0)
			buf.append("/");
		else
			buf.append(service);
		return buf.toString();
	}
}
