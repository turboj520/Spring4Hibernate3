// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractChannel.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			AbstractPeer

public abstract class AbstractChannel extends AbstractPeer
	implements Channel
{

	public AbstractChannel(URL url, ChannelHandler handler)
	{
		super(url, handler);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		if (isClosed())
			throw new RemotingException(this, (new StringBuilder()).append("Failed to send message ").append(message != null ? message.getClass().getName() : "").append(":").append(message).append(", cause: Channel closed. channel: ").append(getLocalAddress()).append(" -> ").append(getRemoteAddress()).toString());
		else
			return;
	}

	public String toString()
	{
		return (new StringBuilder()).append(getLocalAddress()).append(" -> ").append(getRemoteAddress()).toString();
	}
}
