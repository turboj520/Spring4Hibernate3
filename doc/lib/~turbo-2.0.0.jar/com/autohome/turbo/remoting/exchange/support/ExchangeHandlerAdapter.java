// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeHandlerAdapter.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;
import com.autohome.turbo.remoting.exchange.ExchangeHandler;
import com.autohome.turbo.remoting.telnet.support.TelnetHandlerAdapter;

public abstract class ExchangeHandlerAdapter extends TelnetHandlerAdapter
	implements ExchangeHandler
{

	public ExchangeHandlerAdapter()
	{
	}

	public Object reply(ExchangeChannel channel, Object msg)
		throws RemotingException
	{
		return null;
	}
}
