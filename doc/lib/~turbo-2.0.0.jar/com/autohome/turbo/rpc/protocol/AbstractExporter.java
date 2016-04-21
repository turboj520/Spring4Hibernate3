// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractExporter.java

package com.autohome.turbo.rpc.protocol;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.rpc.Exporter;
import com.autohome.turbo.rpc.Invoker;

public abstract class AbstractExporter
	implements Exporter
{

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final Invoker invoker;
	private volatile boolean unexported;

	public AbstractExporter(Invoker invoker)
	{
		unexported = false;
		if (invoker == null)
			throw new IllegalStateException("service invoker == null");
		if (invoker.getInterface() == null)
			throw new IllegalStateException("service type == null");
		if (invoker.getUrl() == null)
		{
			throw new IllegalStateException("service url == null");
		} else
		{
			this.invoker = invoker;
			return;
		}
	}

	public Invoker getInvoker()
	{
		return invoker;
	}

	public void unexport()
	{
		if (unexported)
		{
			return;
		} else
		{
			unexported = true;
			getInvoker().destroy();
			return;
		}
	}

	public String toString()
	{
		return getInvoker().toString();
	}
}
