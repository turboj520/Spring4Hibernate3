// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TelnetHandler.java

package com.autohome.turbo.remoting.telnet;

import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.RemotingException;

public interface TelnetHandler
{

	public abstract String telnet(Channel channel, String s)
		throws RemotingException;
}
