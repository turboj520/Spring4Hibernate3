// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeaderExchanger.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import com.autohome.turbo.remoting.transport.DecodeHandler;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support.header:
//			HeaderExchangeClient, HeaderExchangeHandler, HeaderExchangeServer

public class HeaderExchanger
	implements Exchanger
{

	public static final String NAME = "header";

	public HeaderExchanger()
	{
	}

	public ExchangeClient connect(URL url, ExchangeHandler handler)
		throws RemotingException
	{
		return new HeaderExchangeClient(Transporters.connect(url, new ChannelHandler[] {
			new DecodeHandler(new HeaderExchangeHandler(handler))
		}));
	}

	public ExchangeServer bind(URL url, ExchangeHandler handler)
		throws RemotingException
	{
		return new HeaderExchangeServer(Transporters.bind(url, new ChannelHandler[] {
			new DecodeHandler(new HeaderExchangeHandler(handler))
		}));
	}
}
