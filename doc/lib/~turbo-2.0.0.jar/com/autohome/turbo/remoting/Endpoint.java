// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Endpoint.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.common.URL;
import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting:
//			RemotingException, ChannelHandler

public interface Endpoint
{

	public abstract URL getUrl();

	public abstract ChannelHandler getChannelHandler();

	public abstract InetSocketAddress getLocalAddress();

	public abstract void send(Object obj)
		throws RemotingException;

	public abstract void send(Object obj, boolean flag)
		throws RemotingException;

	public abstract void close();

	public abstract void close(int i);

	public abstract boolean isClosed();
}
