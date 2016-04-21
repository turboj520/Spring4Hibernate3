// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CuratorZookeeperTransporter.java

package com.autohome.turbo.remoting.zookeeper.curator;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.zookeeper.ZookeeperClient;
import com.autohome.turbo.remoting.zookeeper.ZookeeperTransporter;

// Referenced classes of package com.autohome.turbo.remoting.zookeeper.curator:
//			CuratorZookeeperClient

public class CuratorZookeeperTransporter
	implements ZookeeperTransporter
{

	public CuratorZookeeperTransporter()
	{
	}

	public ZookeeperClient connect(URL url)
	{
		return new CuratorZookeeperClient(url);
	}
}
