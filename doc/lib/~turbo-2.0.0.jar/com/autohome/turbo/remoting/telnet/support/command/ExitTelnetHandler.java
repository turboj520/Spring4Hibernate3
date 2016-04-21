// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExitTelnetHandler.java

package com.autohome.turbo.remoting.telnet.support.command;

import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;

public class ExitTelnetHandler
	implements TelnetHandler
{

	public ExitTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		channel.close();
		return null;
	}
}
