// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Server.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.Resetable;
import java.net.InetSocketAddress;
import java.util.Collection;

// Referenced classes of package com.autohome.turbo.remoting:
//			Endpoint, Channel

public interface Server
	extends Endpoint, Resetable
{

	public abstract boolean isBound();

	public abstract Collection getChannels();

	public abstract Channel getChannel(InetSocketAddress inetsocketaddress);

	/**
	 * @deprecated Method reset is deprecated
	 */

	public abstract void reset(Parameters parameters);
}
