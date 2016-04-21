// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   OverrideConfiguratorFactory.java

package com.autohome.turbo.rpc.cluster.configurator.override;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.Configurator;
import com.autohome.turbo.rpc.cluster.ConfiguratorFactory;

// Referenced classes of package com.autohome.turbo.rpc.cluster.configurator.override:
//			OverrideConfigurator

public class OverrideConfiguratorFactory
	implements ConfiguratorFactory
{

	public OverrideConfiguratorFactory()
	{
	}

	public Configurator getConfigurator(URL url)
	{
		return new OverrideConfigurator(url);
	}
}
