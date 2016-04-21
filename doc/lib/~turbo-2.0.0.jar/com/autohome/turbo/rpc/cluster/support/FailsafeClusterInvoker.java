// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailsafeClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.List;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class FailsafeClusterInvoker extends AbstractClusterInvoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/FailsafeClusterInvoker);

	public FailsafeClusterInvoker(Directory directory)
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
		logger.error((new StringBuilder()).append("Failsafe ignore exception: ").append(e.getMessage()).toString(), e);
		return new RpcResult();
	}

}
