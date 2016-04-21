// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TokenFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.rpc.*;
import java.util.Map;

public class TokenFilter
	implements Filter
{

	public TokenFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation inv)
		throws RpcException
	{
		String token = invoker.getUrl().getParameter("token");
		if (ConfigUtils.isNotEmpty(token))
		{
			Class serviceType = invoker.getInterface();
			Map attachments = inv.getAttachments();
			String remoteToken = attachments != null ? (String)attachments.get("token") : null;
			if (!token.equals(remoteToken))
				throw new RpcException((new StringBuilder()).append("Invalid token! Forbid invoke remote service ").append(serviceType).append(" method ").append(inv.getMethodName()).append("() from consumer ").append(RpcContext.getContext().getRemoteHost()).append(" to provider ").append(RpcContext.getContext().getLocalHost()).toString());
		}
		return invoker.invoke(inv);
	}
}
