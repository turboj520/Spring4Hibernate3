// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReplierDispatcher.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.ExchangeChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support:
//			Replier

public class ReplierDispatcher
	implements Replier
{

	private final Replier defaultReplier;
	private final Map repliers;

	public ReplierDispatcher()
	{
		this(null, null);
	}

	public ReplierDispatcher(Replier defaultReplier)
	{
		this(defaultReplier, null);
	}

	public ReplierDispatcher(Replier defaultReplier, Map repliers)
	{
		this.repliers = new ConcurrentHashMap();
		this.defaultReplier = defaultReplier;
		if (repliers != null && repliers.size() > 0)
			this.repliers.putAll(repliers);
	}

	public ReplierDispatcher addReplier(Class type, Replier replier)
	{
		repliers.put(type, replier);
		return this;
	}

	public ReplierDispatcher removeReplier(Class type)
	{
		repliers.remove(type);
		return this;
	}

	private Replier getReplier(Class type)
	{
		for (Iterator i$ = repliers.entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			if (((Class)entry.getKey()).isAssignableFrom(type))
				return (Replier)entry.getValue();
		}

		if (defaultReplier != null)
			return defaultReplier;
		else
			throw new IllegalStateException((new StringBuilder()).append("Replier not found, Unsupported message object: ").append(type).toString());
	}

	public Object reply(ExchangeChannel channel, Object request)
		throws RemotingException
	{
		return getReplier(request.getClass()).reply(channel, request);
	}
}
