// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Protocol.java

package com.autohome.turbo.rpc;

import com.autohome.turbo.common.URL;

// Referenced classes of package com.autohome.turbo.rpc:
//			RpcException, Invoker, Exporter

public interface Protocol
{

	public abstract int getDefaultPort();

	public abstract Exporter export(Invoker invoker)
		throws RpcException;

	public abstract Invoker refer(Class class1, URL url)
		throws RpcException;

	public abstract void destroy();
}
