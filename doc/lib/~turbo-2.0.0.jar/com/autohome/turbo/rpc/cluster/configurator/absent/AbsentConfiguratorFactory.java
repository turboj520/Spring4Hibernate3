// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbsentConfiguratorFactory.java

package com.autohome.turbo.rpc.cluster.configurator.absent;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.cluster.Configurator;
import com.autohome.turbo.rpc.cluster.ConfiguratorFactory;

// Referenced classes of package com.autohome.turbo.rpc.cluster.configurator.absent:
//			AbsentConfigurator

public class AbsentConfiguratorFactory
	implements ConfiguratorFactory
{

	public AbsentConfiguratorFactory()
	{
	}

	public Configurator getConfigurator(URL url)
	{
		return new AbsentConfigurator(url);
	}
}
