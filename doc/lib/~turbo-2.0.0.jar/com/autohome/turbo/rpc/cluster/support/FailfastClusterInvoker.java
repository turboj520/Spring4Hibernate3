// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailfastClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.List;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class FailfastClusterInvoker extends AbstractClusterInvoker
{

	public FailfastClusterInvoker(Directory directory)
	{
		super(directory);
	}

	public Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
		throws RpcException
	{
		Invoker invoker;
		checkInvokers(invokers, invocation);
		invoker = select(loadbalance, invocation, invokers, null);
		return invoker.invoke(invocation);
		Throwable e;
		e;
		if ((e instanceof RpcException) && ((RpcException)e).isBiz())
			throw (RpcException)e;
		else
			throw new RpcException((e instanceof RpcException) ? ((RpcException)e).getCode() : 0, (new StringBuilder()).append("Failfast invoke providers ").append(invoker.getUrl()).append(" ").append(loadbalance.getClass().getSimpleName()).append(" select from all providers ").append(invokers).append(" for service ").append(getInterface().getName()).append(" method ").append(invocation.getMethodName()).append(" on consumer ").append(NetUtils.getLocalHost()).append(" use dubbo version ").append(Version.getVersion()).append(", but no luck to perform the invocation. Last error is: ").append(e.getMessage()).toString(), e.getCause() == null ? e : e.getCause());
	}
}
