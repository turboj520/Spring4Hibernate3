// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DeprecatedInvokerListener.java

package com.autohome.turbo.rpc.listener;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.RpcException;

// Referenced classes of package com.autohome.turbo.rpc.listener:
//			InvokerListenerAdapter

public class DeprecatedInvokerListener extends InvokerListenerAdapter
{

	private static final Logger LOGGER = LoggerFactory.getLogger(com/autohome/turbo/rpc/listener/DeprecatedInvokerListener);

	public DeprecatedInvokerListener()
	{
	}

	public void referred(Invoker invoker)
		throws RpcException
	{
		if (invoker.getUrl().getParameter("deprecated", false))
			LOGGER.error((new StringBuilder()).append("The service ").append(invoker.getInterface().getName()).append(" is DEPRECATED! Declare from ").append(invoker.getUrl()).toString());
	}

}
