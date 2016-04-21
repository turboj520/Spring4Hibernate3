// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelHandler.java

package com.autohome.turbo.remoting;


// Referenced classes of package com.autohome.turbo.remoting:
//			RemotingException, Channel

public interface ChannelHandler
{

	public abstract void connected(Channel channel)
		throws RemotingException;

	public abstract void disconnected(Channel channel)
		throws RemotingException;

	public abstract void sent(Channel channel, Object obj)
		throws RemotingException;

	public abstract void received(Channel channel, Object obj)
		throws RemotingException;

	public abstract void caught(Channel channel, Throwable throwable)
		throws RemotingException;
}
