// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ZookeeperRegistryFactory.java

package com.autohome.turbo.registry.zookeeper;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.registry.Registry;
import com.autohome.turbo.registry.support.AbstractRegistryFactory;
import com.autohome.turbo.remoting.zookeeper.ZookeeperTransporter;

// Referenced classes of package com.autohome.turbo.registry.zookeeper:
//			ZookeeperRegistry

public class ZookeeperRegistryFactory extends AbstractRegistryFactory
{

	private ZookeeperTransporter zookeeperTransporter;

	public ZookeeperRegistryFactory()
	{
	}

	public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter)
	{
		this.zookeeperTransporter = zookeeperTransporter;
	}

	public Registry createRegistry(URL url)
	{
		return new ZookeeperRegistry(url, zookeeperTransporter);
	}
}
