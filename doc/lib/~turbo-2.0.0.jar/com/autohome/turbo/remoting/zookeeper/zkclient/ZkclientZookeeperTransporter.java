// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ZkclientZookeeperTransporter.java

package com.autohome.turbo.remoting.zookeeper.zkclient;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.zookeeper.ZookeeperClient;
import com.autohome.turbo.remoting.zookeeper.ZookeeperTransporter;

// Referenced classes of package com.autohome.turbo.remoting.zookeeper.zkclient:
//			ZkclientZookeeperClient

public class ZkclientZookeeperTransporter
	implements ZookeeperTransporter
{

	public ZkclientZookeeperTransporter()
	{
	}

	public ZookeeperClient connect(URL url)
	{
		return new ZkclientZookeeperClient(url);
	}
}
