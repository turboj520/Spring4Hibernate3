// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboExporter.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.protocol.AbstractExporter;
import java.util.Map;

public class DubboExporter extends AbstractExporter
{

	private final String key;
	private final Map exporterMap;

	public DubboExporter(Invoker invoker, String key, Map exporterMap)
	{
		super(invoker);
		this.key = key;
		this.exporterMap = exporterMap;
	}

	public void unexport()
	{
		super.unexport();
		exporterMap.remove(key);
	}
}
