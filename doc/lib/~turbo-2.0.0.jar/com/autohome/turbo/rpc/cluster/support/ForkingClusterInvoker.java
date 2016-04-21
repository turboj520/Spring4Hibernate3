// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ForkingClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class ForkingClusterInvoker extends AbstractClusterInvoker
{

	private final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("forking-cluster-timer", true));

	public ForkingClusterInvoker(Directory directory)
	{
		super(directory);
	}

	public Result doInvoke(final Invocation invocation, List invokers, LoadBalance loadbalance)
		throws RpcException
	{
		final List selected;
		int timeout;
		final BlockingQueue ref;
		checkInvokers(invokers, invocation);
		int forks = getUrl().getParameter("forks", 2);
		timeout = getUrl().getParameter("timeout", 1000);
		if (forks <= 0 || forks >= invokers.size())
		{
			selected = invokers;
		} else
		{
			selected = new ArrayList();
			for (int i = 0; i < forks; i++)
			{
				Invoker invoker = select(loadbalance, invocation, invokers, selected);
				if (!selected.contains(invoker))
					selected.add(invoker);
			}

		}
		RpcContext.getContext().setInvokers(selected);
		final AtomicInteger count = new AtomicInteger();
		ref = new LinkedBlockingQueue();
		final Invoker invoker;
		for (Iterator i$ = selected.iterator(); i$.hasNext(); executor.execute(new Runnable() {

		final Invoker val$invoker;
		final Invocation val$invocation;
		final BlockingQueue val$ref;
		final AtomicInteger val$count;
		final List val$selected;
		final ForkingClusterInvoker this$0;

		public void run()
		{
			try
			{
				Result result = invoker.invoke(invocation);
				ref.offer(result);
			}
			catch (Throwable e)
			{
				int value = count.incrementAndGet();
				if (value >= selected.size())
					ref.offer(e);
			}
		}

			
			{
				this$0 = ForkingClusterInvoker.this;
				invoker = invoker1;
				invocation = invocation1;
				ref = blockingqueue;
				count = atomicinteger;
				selected = list;
				super();
			}
	}))
			invoker = (Invoker)i$.next();

		Object ret;
		ret = ref.poll(timeout, TimeUnit.MILLISECONDS);
		if (ret instanceof Throwable)
		{
			Throwable e = (Throwable)ret;
			throw new RpcException((e instanceof RpcException) ? ((RpcException)e).getCode() : 0, (new StringBuilder()).append("Failed to forking invoke provider ").append(selected).append(", but no luck to perform the invocation. Last error is: ").append(e.getMessage()).toString(), e.getCause() == null ? e : e.getCause());
		}
		return (Result)ret;
		InterruptedException e;
		e;
		throw new RpcException((new StringBuilder()).append("Failed to forking invoke provider ").append(selected).append(", but no luck to perform the invocation. Last error is: ").append(e.getMessage()).toString(), e);
	}
}
