// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InjvmProtocol.java

package com.autohome.turbo.rpc.protocol.injvm;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.utils.UrlUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.AbstractProtocol;
import com.autohome.turbo.rpc.support.ProtocolUtils;
import java.util.*;

// Referenced classes of package com.autohome.turbo.rpc.protocol.injvm:
//			InjvmExporter, InjvmInvoker

public class InjvmProtocol extends AbstractProtocol
	implements Protocol
{

	public static final String NAME = "injvm";
	public static final int DEFAULT_PORT = 0;
	private static InjvmProtocol INSTANCE;

	public int getDefaultPort()
	{
		return 0;
	}

	public InjvmProtocol()
	{
		INSTANCE = this;
	}

	public static InjvmProtocol getInjvmProtocol()
	{
		if (INSTANCE == null)
			ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/Protocol).getExtension("injvm");
		return INSTANCE;
	}

	public Exporter export(Invoker invoker)
		throws RpcException
	{
		return new InjvmExporter(invoker, invoker.getUrl().getServiceKey(), exporterMap);
	}

	public Invoker refer(Class serviceType, URL url)
		throws RpcException
	{
		return new InjvmInvoker(serviceType, url, url.getServiceKey(), exporterMap);
	}

	static Exporter getExporter(Map map, URL key)
	{
		Exporter result;
label0:
		{
			result = null;
			if (!key.getServiceKey().contains("*"))
			{
				result = (Exporter)map.get(key.getServiceKey());
				break label0;
			}
			if (map == null || map.isEmpty())
				break label0;
			Iterator i$ = map.values().iterator();
			Exporter exporter;
			do
			{
				if (!i$.hasNext())
					break label0;
				exporter = (Exporter)i$.next();
			} while (!UrlUtils.isServiceKeyMatch(key, exporter.getInvoker().getUrl()));
			result = exporter;
		}
		if (result == null)
			return null;
		if (ProtocolUtils.isGeneric(result.getInvoker().getUrl().getParameter("generic")))
			return null;
		else
			return result;
	}

	public boolean isInjvmRefer(URL url)
	{
		String scope = url.getParameter("scope");
		boolean isJvmRefer;
		if ("injvm".toString().equals(url.getProtocol()))
			isJvmRefer = false;
		else
		if ("local".equals(scope) || url.getParameter("injvm", false))
			isJvmRefer = true;
		else
		if ("remote".equals(scope))
			isJvmRefer = false;
		else
		if (url.getParameter("generic", false))
			isJvmRefer = false;
		else
		if (getExporter(exporterMap, url) != null)
			isJvmRefer = true;
		else
			isJvmRefer = false;
		return isJvmRefer;
	}
}
