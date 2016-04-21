// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailoverClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.cluster.LoadBalance;
import java.util.*;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			AbstractClusterInvoker

public class FailoverClusterInvoker extends AbstractClusterInvoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/FailoverClusterInvoker);

	public FailoverClusterInvoker(Directory directory)
	{
		super(directory);
	}

	public Result doInvoke(Invocation invocation, List invokers, LoadBalance loadbalance)
		throws RpcException
	{
		List copyinvokers;
		int len;
		RpcException le;
		List invoked;
		Set providers;
		int i;
		copyinvokers = invokers;
		checkInvokers(copyinvokers, invocation);
		len = getUrl().getMethodParameter(invocation.getMethodName(), "retries", 2) + 1;
		if (len <= 0)
			len = 1;
		le = null;
		invoked = new ArrayList(copyinvokers.size());
		providers = new HashSet(len);
		i = 0;
_L3:
		if (i >= len) goto _L2; else goto _L1
_L1:
		Invoker invoker;
		if (i > 0)
		{
			checkWheatherDestoried();
			copyinvokers = list(invocation);
			checkInvokers(copyinvokers, invocation);
		}
		invoker = select(loadbalance, invocation, copyinvokers, invoked);
		invoked.add(invoker);
		RpcContext.getContext().setInvokers(invoked);
		Result result1;
		Result result = invoker.invoke(invocation);
		if (le != null && logger.isWarnEnabled())
			logger.warn((new StringBuilder()).append("Although retry the method ").append(invocation.getMethodName()).append(" in the service ").append(getInterface().getName()).append(" was successful by the provider ").append(invoker.getUrl().getAddress()).append(", but there have been failed providers ").append(providers).append(" (").append(providers.size()).append("/").append(copyinvokers.size()).append(") from the registry ").append(directory.getUrl().getAddress()).append(" on the consumer ").append(NetUtils.getLocalHost()).append(" using the dubbo version ").append(Version.getVersion()).append(". Last error is: ").append(le.getMessage()).toString(), le);
		result1 = result;
		providers.add(invoker.getUrl().getAddress());
		return result1;
		RpcException e;
		e;
		if (e.isBiz())
			throw e;
		le = e;
		providers.add(invoker.getUrl().getAddress());
		continue; /* Loop/switch isn't completed */
		e;
		le = new RpcException(e.getMessage(), e);
		providers.add(invoker.getUrl().getAddress());
		continue; /* Loop/switch isn't completed */
		Exception exception;
		exception;
		providers.add(invoker.getUrl().getAddress());
		throw exception;
		i++;
		  goto _L3
_L2:
		throw new RpcException(le == null ? 0 : le.getCode(), (new StringBuilder()).append("Failed to invoke the method ").append(invocation.getMethodName()).append(" in the service ").append(getInterface().getName()).append(". Tried ").append(len).append(" times of the providers ").append(providers).append(" (").append(providers.size()).append("/").append(copyinvokers.size()).append(") from the registry ").append(directory.getUrl().getAddress()).append(" on the consumer ").append(NetUtils.getLocalHost()).append(" using the dubbo version ").append(Version.getVersion()).append(". Last error is: ").append(le == null ? "" : le.getMessage()).toString(), ((Throwable) (le == null || le.getCause() == null ? ((Throwable) (le)) : le.getCause())));
	}

}
