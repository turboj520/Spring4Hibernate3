// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExchangeChannel.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.RemotingException;

// Referenced classes of package com.autohome.turbo.remoting.exchange:
//			ResponseFuture, ExchangeHandler

public interface ExchangeChannel
	extends Channel
{

	public abstract ResponseFuture request(Object obj)
		throws RemotingException;

	public abstract ResponseFuture request(Object obj, int i)
		throws RemotingException;

	public abstract ExchangeHandler getExchangeHandler();

	public abstract void close(int i);
}
