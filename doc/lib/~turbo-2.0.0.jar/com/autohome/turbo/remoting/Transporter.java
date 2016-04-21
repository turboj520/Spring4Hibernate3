// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Transporter.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.common.URL;

// Referenced classes of package com.autohome.turbo.remoting:
//			RemotingException, ChannelHandler, Server, Client

public interface Transporter
{

	public abstract Server bind(URL url, ChannelHandler channelhandler)
		throws RemotingException;

	public abstract Client connect(URL url, ChannelHandler channelhandler)
		throws RemotingException;
}
