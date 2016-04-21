// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AvailableCluster.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.*;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class AvailableCluster
	implements Cluster
{

	public static final String NAME = "available";

	public AvailableCluster()
	{
	}

	public Invoker join(Directory directory)
		throws RpcException
	{
		return new AbstractClusterInvoker(directory) {

			final AvailableCluster this$0;

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

			
			{
				this$0 = AvailableCluster.this;
				super(x0);
			}
		};
	}
}
