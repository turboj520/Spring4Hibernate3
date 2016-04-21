// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StaticDirectory.java

package com.autohome.turbo.rpc.cluster.directory;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.autohome.turbo.rpc.cluster.directory:
//			AbstractDirectory

public class StaticDirectory extends AbstractDirectory
{

	private final List invokers;

	public StaticDirectory(List invokers)
	{
		this(null, invokers, null);
	}

	public StaticDirectory(List invokers, List routers)
	{
		this(null, invokers, routers);
	}

	public StaticDirectory(URL url, List invokers)
	{
		this(url, invokers, null);
	}

	public StaticDirectory(URL url, List invokers, List routers)
	{
		super(url != null || invokers == null || invokers.size() <= 0 ? url : ((Invoker)invokers.get(0)).getUrl(), routers);
		if (invokers == null || invokers.size() == 0)
		{
			throw new IllegalArgumentException("invokers == null");
		} else
		{
			this.invokers = invokers;
			return;
		}
	}

	public Class getInterface()
	{
		return ((Invoker)invokers.get(0)).getInterface();
	}

	public boolean isAvailable()
	{
		if (isDestroyed())
			return false;
		for (Iterator i$ = invokers.iterator(); i$.hasNext();)
		{
			Invoker invoker = (Invoker)i$.next();
			if (invoker.isAvailable())
				return true;
		}

		return false;
	}

	public void destroy()
	{
		if (isDestroyed())
			return;
		super.destroy();
		Invoker invoker;
		for (Iterator i$ = invokers.iterator(); i$.hasNext(); invoker.destroy())
			invoker = (Invoker)i$.next();

		invokers.clear();
	}

	protected List doList(Invocation invocation)
		throws RpcException
	{
		return invokers;
	}
}
