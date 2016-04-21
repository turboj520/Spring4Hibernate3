// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Channel.java

package com.autohome.turbo.remoting;

import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting:
//			Endpoint

public interface Channel
	extends Endpoint
{

	public abstract InetSocketAddress getRemoteAddress();

	public abstract boolean isConnected();

	public abstract boolean hasAttribute(String s);

	public abstract Object getAttribute(String s);

	public abstract void setAttribute(String s, Object obj);

	public abstract void removeAttribute(String s);
}
