// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Client.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.Resetable;

// Referenced classes of package com.autohome.turbo.remoting:
//			Endpoint, Channel, RemotingException

public interface Client
	extends Endpoint, Channel, Resetable
{

	public abstract void reconnect()
		throws RemotingException;

	/**
	 * @deprecated Method reset is deprecated
	 */

	public abstract void reset(Parameters parameters);
}
