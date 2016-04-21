// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeHandler.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.telnet.TelnetHandler;

// Referenced classes of package com.autohome.turbo.remoting.exchange:
//			ExchangeChannel

public interface ExchangeHandler
	extends ChannelHandler, TelnetHandler
{

	public abstract Object reply(ExchangeChannel exchangechannel, Object obj)
		throws RemotingException;
}
