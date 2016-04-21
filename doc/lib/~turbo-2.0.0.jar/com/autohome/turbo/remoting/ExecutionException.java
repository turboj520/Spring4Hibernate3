// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExecutionException.java

package com.autohome.turbo.remoting;

import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting:
//			RemotingException, Channel

public class ExecutionException extends RemotingException
{

	private static final long serialVersionUID = 0xdcdfc763555d6024L;
	private final Object request;

	public ExecutionException(Object request, Channel channel, String message, Throwable cause)
	{
		super(channel, message, cause);
		this.request = request;
	}

	public ExecutionException(Object request, Channel channel, String msg)
	{
		super(channel, msg);
		this.request = request;
	}

	public ExecutionException(Object request, Channel channel, Throwable cause)
	{
		super(channel, cause);
		this.request = request;
	}

	public ExecutionException(Object request, InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message, Throwable cause)
	{
		super(localAddress, remoteAddress, message, cause);
		this.request = request;
	}

	public ExecutionException(Object request, InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message)
	{
		super(localAddress, remoteAddress, message);
		this.request = request;
	}

	public ExecutionException(Object request, InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause)
	{
		super(localAddress, remoteAddress, cause);
		this.request = request;
	}

	public Object getRequest()
	{
		return request;
	}
}
