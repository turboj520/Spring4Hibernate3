// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MergeableClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.common.utils.NamedThreadFactory;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.Merger;
import com.autohome.turbo.rpc.cluster.merger.MergerFactory;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

public class MergeableClusterInvoker
	implements Invoker
{

	private static final Logger log = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/MergeableClusterInvoker);
	private ExecutorService executor;
	private final Directory directory;

	public MergeableClusterInvoker(Directory directory)
	{
		executor = Executors.newCachedThreadPool(new NamedThreadFactory("mergeable-cluster-executor", true));
		this.directory = directory;
	}

	public Result invoke(final Invocation invocation)
		throws RpcException
	{
		List invokers = directory.list(invocation);
		String merger = getUrl().getMethodParameter(invocation.getMethodName(), "merger");
		if (ConfigUtils.isEmpty(merger))
		{
			for (Iterator i$ = invokers.iterator(); i$.hasNext();)
			{
				Invoker invoker = (Invoker)i$.next();
				if (invoker.isAvailable())
					return invoker.invoke(invocation);
			}

			return ((Invoker)invokers.iterator().next()).invoke(invocation);
		} else
		{
			Class returnType;
			try
			{
				returnType = getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes()).getReturnType();
			}
			catch (NoSuchMethodException e)
			{
				returnType = null;
			}
			Map results = new HashMap();
			final Invoker invoker;
			Future future;
			for (Iterator i$ = invokers.iterator(); i$.hasNext(); results.put(invoker.getUrl().getServiceKey(), future))
			{
				invoker = (Invoker)i$.next();
				future = executor.submit(new Callable() {

					final Invoker val$invoker;
					final Invocation val$invocation;
					final MergeableClusterInvoker this$0;

					public Result call()
						throws Exception
					{
						return invoker.invoke(new RpcInvocation(invocation, invoker));
					}

					public volatile Object call()
						throws Exception
					{
						return call();
					}

			
			{
				this$0 = MergeableClusterInvoker.this;
				invoker = invoker1;
				invocation = invocation1;
				super();
			}
				});
			}

			Object result = null;
			List resultList = new ArrayList(results.size());
			int timeout = getUrl().getMethodParameter(invocation.getMethodName(), "timeout", 1000);
			for (Iterator i$ = results.entrySet().iterator(); i$.hasNext();)
			{
				java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
				Future future = (Future)entry.getValue();
				try
				{
					Result r = (Result)future.get(timeout, TimeUnit.MILLISECONDS);
					if (r.hasException())
						log.error((new StringBuilder(32)).append("Invoke ").append(getGroupDescFromServiceKey((String)entry.getKey())).append(" failed: ").append(r.getException().getMessage()).toString(), r.getException());
					else
						resultList.add(r);
				}
				catch (Exception e)
				{
					throw new RpcException((new StringBuilder(32)).append("Failed to invoke service ").append((String)entry.getKey()).append(": ").append(e.getMessage()).toString(), e);
				}
			}

			if (resultList.size() == 0)
				return new RpcResult((Object)null);
			if (resultList.size() == 1)
				return (Result)resultList.iterator().next();
			if (returnType == Void.TYPE)
				return new RpcResult((Object)null);
			if (merger.startsWith("."))
			{
				merger = merger.substring(1);
				Method method;
				try
				{
					method = returnType.getMethod(merger, new Class[] {
						returnType
					});
				}
				catch (NoSuchMethodException e)
				{
					throw new RpcException((new StringBuilder(32)).append("Can not merge result because missing method [ ").append(merger).append(" ] in class [ ").append(returnType.getClass().getName()).append(" ]").toString());
				}
				if (method != null)
				{
					if (!Modifier.isPublic(method.getModifiers()))
						method.setAccessible(true);
					result = ((Result)resultList.remove(0)).getValue();
					try
					{
						if (method.getReturnType() != Void.TYPE && method.getReturnType().isAssignableFrom(result.getClass()))
						{
							for (Iterator i$ = resultList.iterator(); i$.hasNext();)
							{
								Result r = (Result)i$.next();
								result = method.invoke(result, new Object[] {
									r.getValue()
								});
							}

						} else
						{
							Result r;
							for (Iterator i$ = resultList.iterator(); i$.hasNext(); method.invoke(result, new Object[] {
	r.getValue()
}))
								r = (Result)i$.next();

						}
					}
					catch (Exception e)
					{
						throw new RpcException((new StringBuilder(32)).append("Can not merge result: ").append(e.getMessage()).toString(), e);
					}
				} else
				{
					throw new RpcException((new StringBuilder(32)).append("Can not merge result because missing method [ ").append(merger).append(" ] in class [ ").append(returnType.getClass().getName()).append(" ]").toString());
				}
			} else
			{
				Merger resultMerger;
				if (ConfigUtils.isDefault(merger))
					resultMerger = MergerFactory.getMerger(returnType);
				else
					resultMerger = (Merger)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/Merger).getExtension(merger);
				if (resultMerger != null)
				{
					List rets = new ArrayList(resultList.size());
					Result r;
					for (Iterator i$ = resultList.iterator(); i$.hasNext(); rets.add(r.getValue()))
						r = (Result)i$.next();

					result = resultMerger.merge(rets.toArray((Object[])(Object[])Array.newInstance(returnType, 0)));
				} else
				{
					throw new RpcException("There is no merger to merge result.");
				}
			}
			return new RpcResult(result);
		}
	}

	public Class getInterface()
	{
		return directory.getInterface();
	}

	public URL getUrl()
	{
		return directory.getUrl();
	}

	public boolean isAvailable()
	{
		return directory.isAvailable();
	}

	public void destroy()
	{
		directory.destroy();
	}

	private String getGroupDescFromServiceKey(String key)
	{
		int index = key.indexOf("/");
		if (index > 0)
			return (new StringBuilder(32)).append("group [ ").append(key.substring(0, index)).append(" ]").toString();
		else
			return key;
	}

}
