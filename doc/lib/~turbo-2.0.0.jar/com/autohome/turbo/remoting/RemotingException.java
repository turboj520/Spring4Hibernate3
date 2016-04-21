// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RemotingException.java

package com.autohome.turbo.remoting;

import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting:
//			Channel

public class RemotingException extends Exception
{

	private static final long serialVersionUID = 0xd423d183c51178abL;
	private InetSocketAddress localAddress;
	private InetSocketAddress remoteAddress;

	public RemotingException(Channel channel, String msg)
	{
		this(channel != null ? channel.getLocalAddress() : null, channel != null ? channel.getRemoteAddress() : null, msg);
	}

	public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message)
	{
		super(message);
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	public RemotingException(Channel channel, Throwable cause)
	{
		this(channel != null ? channel.getLocalAddress() : null, channel != null ? channel.getRemoteAddress() : null, cause);
	}

	public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause)
	{
		super(cause);
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	public RemotingException(Channel channel, String message, Throwable cause)
	{
		this(channel != null ? channel.getLocalAddress() : null, channel != null ? channel.getRemoteAddress() : null, message, cause);
	}

	public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message, Throwable cause)
	{
		super(message, cause);
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	public InetSocketAddress getLocalAddress()
	{
		return localAddress;
	}

	public InetSocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}
}
