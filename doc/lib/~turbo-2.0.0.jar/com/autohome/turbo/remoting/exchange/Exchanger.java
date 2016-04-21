// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Exchanger.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.RemotingException;

// Referenced classes of package com.autohome.turbo.remoting.exchange:
//			ExchangeHandler, ExchangeServer, ExchangeClient

public interface Exchanger
{

	public abstract ExchangeServer bind(URL url, ExchangeHandler exchangehandler)
		throws RemotingException;

	public abstract ExchangeClient connect(URL url, ExchangeHandler exchangehandler)
		throws RemotingException;
}
