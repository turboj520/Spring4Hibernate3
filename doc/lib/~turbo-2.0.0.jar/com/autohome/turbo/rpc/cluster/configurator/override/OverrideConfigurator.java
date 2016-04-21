// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   OverrideConfigurator.java

package com.autohome.turbo.rpc.cluster.configurator.override;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.configurator.AbstractConfigurator;

public class OverrideConfigurator extends AbstractConfigurator
{

	public OverrideConfigurator(URL url)
	{
		super(url);
	}

	public URL doConfigure(URL currentUrl, URL configUrl)
	{
		return currentUrl.addParameters(configUrl.getParameters());
	}
}
