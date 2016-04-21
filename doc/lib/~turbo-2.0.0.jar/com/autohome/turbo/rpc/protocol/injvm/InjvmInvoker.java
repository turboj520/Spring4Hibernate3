// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InjvmInvoker.java

package com.autohome.turbo.rpc.protocol.injvm;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.AbstractInvoker;
import java.util.Map;

// Referenced classes of package com.autohome.turbo.rpc.protocol.injvm:
//			InjvmExporter, InjvmProtocol

class InjvmInvoker extends AbstractInvoker
{

	private final String key;
	private final Map exporterMap;

	InjvmInvoker(Class type, URL url, String key, Map exporterMap)
	{
		super(type, url);
		this.key = key;
		this.exporterMap = exporterMap;
	}

	public boolean isAvailable()
	{
		InjvmExporter exporter = (InjvmExporter)exporterMap.get(key);
		if (exporter == null)
			return false;
		else
			return super.isAvailable();
	}

	public Result doInvoke(Invocation invocation)
		throws Throwable
	{
		Exporter exporter = InjvmProtocol.getExporter(exporterMap, getUrl());
		if (exporter == null)
		{
			throw new RpcException((new StringBuilder()).append("Service [").append(key).append("] not found.").toString());
		} else
		{
			RpcContext.getContext().setRemoteAddress("127.0.0.1", 0);
			return exporter.getInvoker().invoke(invocation);
		}
	}
}
