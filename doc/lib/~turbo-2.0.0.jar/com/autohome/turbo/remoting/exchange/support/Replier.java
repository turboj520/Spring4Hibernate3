// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Replier.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;

public interface Replier
{

	public abstract Object reply(ExchangeChannel exchangechannel, Object obj)
		throws RemotingException;
}
