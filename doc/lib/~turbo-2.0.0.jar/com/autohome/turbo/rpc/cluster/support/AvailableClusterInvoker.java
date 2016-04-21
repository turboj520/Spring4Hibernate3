// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AvailableClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class AvailableClusterInvoker extends AbstractClusterInvoker
{

	public AvailableClusterInvoker(Directory directory)
	{
		super(directory);
	}

	public Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
		throws RpcException
	{
		for (Iterator i$ = invokers.iterator(); i$.hasNext();)
		{
			Invoker invoker = (Invoker)i$.next();
			if (invoker.isAvailable())
				return invoker.invoke(invocation);
		}

		throw new RpcException((new StringBuilder()).append("No provider available in ").append(invokers).toString());
	}
}
