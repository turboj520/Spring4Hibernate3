// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BroadcastClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class BroadcastClusterInvoker extends AbstractClusterInvoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/BroadcastClusterInvoker);

	public BroadcastClusterInvoker(Directory directory)
	{
		super(directory);
	}

	public Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
		throws RpcException
	{
		checkInvokers(invokers, invocation);
		RpcContext.getContext().setInvokers(invokers);
		RpcException exception = null;
		Result result = null;
		for (Iterator i$ = invokers.iterator(); i$.hasNext();)
		{
			Invoker invoker = (Invoker)i$.next();
			try
			{
				result = invoker.invoke(invocation);
			}
			catch (RpcException e)
			{
				exception = e;
				logger.warn(e.getMessage(), e);
			}
			catch (Throwable e)
			{
				exception = new RpcException(e.getMessage(), e);
				logger.warn(e.getMessage(), e);
			}
		}

		if (exception != null)
			throw exception;
		else
			return result;
	}

}
