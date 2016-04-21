// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailbackClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.*;
import java.util.concurrent.*;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class FailbackClusterInvoker extends AbstractClusterInvoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/FailbackClusterInvoker);
	private static final long RETRY_FAILED_PERIOD = 5000L;
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2, new NamedThreadFactory("failback-cluster-timer", true));
	private volatile ScheduledFuture retryFuture;
	private final ConcurrentMap failed = new ConcurrentHashMap();

	public FailbackClusterInvoker(Directory directory)
	{
		super(directory);
	}

	private void addFailed(Invocation invocation, AbstractClusterInvoker router)
	{
		if (retryFuture == null)
			synchronized (this)
			{
				if (retryFuture == null)
					retryFuture = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

						final FailbackClusterInvoker this$0;

						public void run()
						{
							try
							{
								retryFailed();
							}
							catch (Throwable t)
							{
								FailbackClusterInvoker.logger.error("Unexpected error occur at collect statistic", t);
							}
						}

			
			{
				this$0 = FailbackClusterInvoker.this;
				super();
			}
					}, 5000L, 5000L, TimeUnit.MILLISECONDS);
			}
		failed.put(invocation, router);
	}

	void retryFailed()
	{
		if (failed.size() == 0)
			return;
		for (Iterator i$ = (new HashMap(failed)).entrySet().iterator(); i$.hasNext();)
		{
			java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
			Invocation invocation = (Invocation)entry.getKey();
			Invoker invoker = (Invoker)entry.getValue();
			try
			{
				invoker.invoke(invocation);
				failed.remove(invocation);
			}
			catch (Throwable e)
			{
				logger.error((new StringBuilder()).append("Failed retry to invoke method ").append(invocation.getMethodName()).append(", waiting again.").toString(), e);
			}
		}

	}

	protected Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
		throws RpcException
	{
		Invoker invoker;
		checkInvokers(invokers, invocation);
		invoker = select(loadbalance, invocation, invokers, null);
		return invoker.invoke(invocation);
		Throwable e;
		e;
		logger.error((new StringBuilder()).append("Failback to invoke method ").append(invocation.getMethodName()).append(", wait for retry in background. Ignored exception: ").append(e.getMessage()).append(", ").toString(), e);
		addFailed(invocation, this);
		return new RpcResult();
	}


}
