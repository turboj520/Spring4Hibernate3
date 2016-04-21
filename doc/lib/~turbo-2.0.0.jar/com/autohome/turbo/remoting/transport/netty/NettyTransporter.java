// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyTransporter.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;

// Referenced classes of package com.autohome.turbo.remoting.transport.netty:
//			NettyServer, NettyClient

public class NettyTransporter
	implements Transporter
{

	public static final String NAME = "netty";

	public NettyTransporter()
	{
	}

	public Server bind(URL url, ChannelHandler listener)
		throws RemotingException
	{
		return new NettyServer(url, listener);
	}

	public Client connect(URL url, ChannelHandler listener)
		throws RemotingException
	{
		return new NettyClient(url, listener);
	}
}
