// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DelegateExporter.java

package com.autohome.turbo.rpc.support;

import com.autohome.turbo.rpc.Exporter;
import com.autohome.turbo.rpc.Invoker;

public class DelegateExporter
	implements Exporter
{

	private final Exporter exporter;

	public DelegateExporter(Exporter exporter)
	{
		if (exporter == null)
		{
			throw new IllegalArgumentException("exporter can not be null");
		} else
		{
			this.exporter = exporter;
			return;
		}
	}

	public Invoker getInvoker()
	{
		return exporter.getInvoker();
	}

	public void unexport()
	{
		exporter.unexport();
	}
}
