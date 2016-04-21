// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CacheFilter.java

package com.autohome.turbo.cache.filter;

import com.autohome.turbo.cache.Cache;
import com.autohome.turbo.cache.CacheFactory;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.rpc.*;

public class CacheFilter
	implements Filter
{

	private CacheFactory cacheFactory;

	public CacheFilter()
	{
	}

	public void setCacheFactory(CacheFactory cacheFactory)
	{
		this.cacheFactory = cacheFactory;
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		if (cacheFactory != null && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), "cache")))
		{
			Cache cache = cacheFactory.getCache(invoker.getUrl().addParameter("method", invocation.getMethodName()));
			if (cache != null)
			{
				String key = StringUtils.toArgumentString(invocation.getArguments());
				if (cache != null && key != null)
				{
					Object value = cache.get(key);
					if (value != null)
						return new RpcResult(value);
					Result result = invoker.invoke(invocation);
					if (!result.hasException())
						cache.put(key, result.getValue());
					return result;
				}
			}
		}
		return invoker.invoke(invocation);
	}
}
