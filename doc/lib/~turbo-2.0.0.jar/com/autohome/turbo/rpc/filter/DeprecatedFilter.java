// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DeprecatedFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.rpc.*;
import java.util.Set;

public class DeprecatedFilter
	implements Filter
{

	private static final Logger LOGGER = LoggerFactory.getLogger(com/autohome/turbo/rpc/filter/DeprecatedFilter);
	private static final Set logged = new ConcurrentHashSet();

	public DeprecatedFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		String key = (new StringBuilder()).append(invoker.getInterface().getName()).append(".").append(invocation.getMethodName()).toString();
		if (!logged.contains(key))
		{
			logged.add(key);
			if (invoker.getUrl().getMethodParameter(invocation.getMethodName(), "deprecated", false))
				LOGGER.error((new StringBuilder()).append("The service method ").append(invoker.getInterface().getName()).append(".").append(getMethodSignature(invocation)).append(" is DEPRECATED! Declare from ").append(invoker.getUrl()).toString());
		}
		return invoker.invoke(invocation);
	}

	private String getMethodSignature(Invocation invocation)
	{
		StringBuilder buf = new StringBuilder(invocation.getMethodName());
		buf.append("(");
		Class types[] = invocation.getParameterTypes();
		if (types != null && types.length > 0)
		{
			boolean first = true;
			Class arr$[] = types;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				Class type = arr$[i$];
				if (first)
					first = false;
				else
					buf.append(", ");
				buf.append(type.getSimpleName());
			}

		}
		buf.append(")");
		return buf.toString();
	}

}
