// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ProxyFactory.java

package com.autohome.turbo.rpc;

import com.autohome.turbo.common.URL;

// Referenced classes of package com.autohome.turbo.rpc:
//			RpcException, Invoker

public interface ProxyFactory
{

	public abstract Object getProxy(Invoker invoker)
		throws RpcException;

	public abstract Invoker getInvoker(Object obj, Class class1, URL url)
		throws RpcException;
}
