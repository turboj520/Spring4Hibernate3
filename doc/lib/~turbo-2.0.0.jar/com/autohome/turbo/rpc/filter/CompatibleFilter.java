// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CompatibleFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.CompatibleTypeUtils;
import com.autohome.turbo.common.utils.PojoUtils;
import com.autohome.turbo.rpc.*;
import java.lang.reflect.Method;

public class CompatibleFilter
	implements Filter
{

	private static Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/filter/CompatibleFilter);

	public CompatibleFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		Result result = invoker.invoke(invocation);
		if (!invocation.getMethodName().startsWith("$") && !result.hasException())
		{
			Object value = result.getValue();
			if (value != null)
				try
				{
					Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
					Class type = method.getReturnType();
					String serialization = invoker.getUrl().getParameter("serialization");
					Object newValue;
					if ("json".equals(serialization) || "fastjson".equals(serialization))
					{
						java.lang.reflect.Type gtype = method.getGenericReturnType();
						newValue = PojoUtils.realize(value, type, gtype);
					} else
					if (!type.isInstance(value))
						newValue = PojoUtils.isPojo(type) ? PojoUtils.realize(value, type) : CompatibleTypeUtils.compatibleTypeConvert(value, type);
					else
						newValue = value;
					if (newValue != value)
						result = new RpcResult(newValue);
				}
				catch (Throwable t)
				{
					logger.warn(t.getMessage(), t);
				}
		}
		return result;
	}

}
