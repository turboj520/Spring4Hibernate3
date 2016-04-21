// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeServer.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.remoting.Server;
import java.net.InetSocketAddress;
import java.util.Collection;

// Referenced classes of package com.autohome.turbo.remoting.exchange:
//			ExchangeChannel

public interface ExchangeServer
	extends Server
{

	public abstract Collection getExchangeChannels();

	public abstract ExchangeChannel getExchangeChannel(InetSocketAddress inetsocketaddress);
}
