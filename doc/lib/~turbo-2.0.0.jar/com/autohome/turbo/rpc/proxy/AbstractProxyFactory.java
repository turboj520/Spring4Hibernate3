// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractProxyFactory.java

package com.autohome.turbo.rpc.proxy;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.service.EchoService;
import java.util.regex.Pattern;

public abstract class AbstractProxyFactory
	implements ProxyFactory
{

	public AbstractProxyFactory()
	{
	}

	public Object getProxy(Invoker invoker)
		throws RpcException
	{
		Class interfaces[] = null;
		String config = invoker.getUrl().getParameter("interfaces");
		if (config != null && config.length() > 0)
		{
			String types[] = Constants.COMMA_SPLIT_PATTERN.split(config);
			if (types != null && types.length > 0)
			{
				interfaces = new Class[types.length + 2];
				interfaces[0] = invoker.getInterface();
				interfaces[1] = com/autohome/turbo/rpc/service/EchoService;
				for (int i = 0; i < types.length; i++)
					interfaces[i + 1] = ReflectUtils.forName(types[i]);

			}
		}
		if (interfaces == null)
			interfaces = (new Class[] {
				invoker.getInterface(), com/autohome/turbo/rpc/service/EchoService
			});
		return getProxy(invoker, interfaces);
	}

	public abstract Object getProxy(Invoker invoker, Class aclass[]);
}
