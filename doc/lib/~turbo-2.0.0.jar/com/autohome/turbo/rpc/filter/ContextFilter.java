// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ContextFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import java.util.HashMap;
import java.util.Map;

public class ContextFilter
	implements Filter
{

	public ContextFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		Map attachments = invocation.getAttachments();
		if (attachments != null)
		{
			attachments = new HashMap(attachments);
			attachments.remove("path");
			attachments.remove("group");
			attachments.remove("version");
			attachments.remove("dubbo");
			attachments.remove("token");
			attachments.remove("timeout");
		}
		RpcContext.getContext().setInvoker(invoker).setInvocation(invocation).setLocalAddress(invoker.getUrl().getHost(), invoker.getUrl().getPort());
		if (attachments != null)
			if (RpcContext.getContext().getAttachments() != null)
				RpcContext.getContext().getAttachments().putAll(attachments);
			else
				RpcContext.getContext().setAttachments(attachments);
		if (invocation instanceof RpcInvocation)
			((RpcInvocation)invocation).setInvoker(invoker);
		Result result = invoker.invoke(invocation);
		RpcContext.removeContext();
		return result;
		Exception exception;
		exception;
		RpcContext.removeContext();
		throw exception;
	}
}
