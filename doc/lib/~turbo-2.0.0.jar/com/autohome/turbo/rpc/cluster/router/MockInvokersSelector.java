// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MockInvokersSelector.java

package com.autohome.turbo.rpc.cluster.router;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Router;
import java.util.*;

public class MockInvokersSelector
	implements Router
{

	public MockInvokersSelector()
	{
	}

	public List route(List invokers, URL url, Invocation invocation)
		throws RpcException
	{
		if (invocation.getAttachments() == null)
			return getNormalInvokers(invokers);
		String value = (String)invocation.getAttachments().get("invocation.need.mock");
		if (value == null)
			return getNormalInvokers(invokers);
		if (Boolean.TRUE.toString().equalsIgnoreCase(value))
			return getMockedInvokers(invokers);
		else
			return invokers;
	}

	private List getMockedInvokers(List invokers)
	{
		if (!hasMockProviders(invokers))
			return null;
		List sInvokers = new ArrayList(1);
		Iterator i$ = invokers.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Invoker invoker = (Invoker)i$.next();
			if (invoker.getUrl().getProtocol().equals("mock"))
				sInvokers.add(invoker);
		} while (true);
		return sInvokers;
	}

	private List getNormalInvokers(List invokers)
	{
		if (!hasMockProviders(invokers))
			return invokers;
		List sInvokers = new ArrayList(invokers.size());
		Iterator i$ = invokers.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Invoker invoker = (Invoker)i$.next();
			if (!invoker.getUrl().getProtocol().equals("mock"))
				sInvokers.add(invoker);
		} while (true);
		return sInvokers;
	}

	private boolean hasMockProviders(List invokers)
	{
		boolean hasMockProvider = false;
		Iterator i$ = invokers.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Invoker invoker = (Invoker)i$.next();
			if (!invoker.getUrl().getProtocol().equals("mock"))
				continue;
			hasMockProvider = true;
			break;
		} while (true);
		return hasMockProvider;
	}

	public URL getUrl()
	{
		return null;
	}

	public int compareTo(Router o)
	{
		return 1;
	}

	public volatile int compareTo(Object x0)
	{
		return compareTo((Router)x0);
	}
}
