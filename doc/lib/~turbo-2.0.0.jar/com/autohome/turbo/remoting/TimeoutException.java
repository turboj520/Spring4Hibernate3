// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TimeoutException.java

package com.autohome.turbo.remoting;

import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting:
//			RemotingException, Channel

public class TimeoutException extends RemotingException
{

	private static final long serialVersionUID = 0x2b5701b24fbb7f64L;
	public static final int CLIENT_SIDE = 0;
	public static final int SERVER_SIDE = 1;
	private final int phase;

	public TimeoutException(boolean serverSide, Channel channel, String message)
	{
		super(channel, message);
		phase = serverSide ? 1 : 0;
	}

	public TimeoutException(boolean serverSide, InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message)
	{
		super(localAddress, remoteAddress, message);
		phase = serverSide ? 1 : 0;
	}

	public int getPhase()
	{
		return phase;
	}

	public boolean isServerSide()
	{
		return phase == 1;
	}

	public boolean isClientSide()
	{
		return phase == 0;
	}
}
