// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractChannelHandlerDelegate.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.utils.Assert;
import com.autohome.turbo.remoting.*;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			ChannelHandlerDelegate

public abstract class AbstractChannelHandlerDelegate
	implements ChannelHandlerDelegate
{

	protected ChannelHandler handler;

	protected AbstractChannelHandlerDelegate(ChannelHandler handler)
	{
		Assert.notNull(handler, "handler == null");
		this.handler = handler;
	}

	public ChannelHandler getHandler()
	{
		if (handler instanceof ChannelHandlerDelegate)
			return ((ChannelHandlerDelegate)handler).getHandler();
		else
			return handler;
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		handler.connected(channel);
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		handler.disconnected(channel);
	}

	public void sent(Channel channel, Object message)
		throws RemotingException
	{
		handler.sent(channel, message);
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		handler.received(channel, message);
	}

	public void caught(Channel channel, Throwable exception)
		throws RemotingException
	{
		handler.caught(channel, exception);
	}
}
