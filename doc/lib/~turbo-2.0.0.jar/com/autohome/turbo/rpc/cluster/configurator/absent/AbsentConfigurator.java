// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbsentConfigurator.java

package com.autohome.turbo.rpc.cluster.configurator.absent;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.configurator.AbstractConfigurator;

public class AbsentConfigurator extends AbstractConfigurator
{

	public AbsentConfigurator(URL url)
	{
		super(url);
	}

	public URL doConfigure(URL currentUrl, URL configUrl)
	{
		return currentUrl.addParametersIfAbsent(configUrl.getParameters());
	}
}
