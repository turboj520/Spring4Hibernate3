// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailsafeCluster.java

package com.autohome.turbo.rpc.cluster.support;

import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.RpcException;
import com.autohome.turbo.rpc.cluster.Cluster;
import com.autohome.turbo.rpc.cluster.Directory;

// Referenced classes of package com.autohome.turbo.rpc.cluster.support:
//			FailsafeClusterInvoker

public class FailsafeCluster
	implements Cluster
{

	public static final String NAME = "failsafe";

	public FailsafeCluster()
	{
	}

	public Invoker join(Directory directory)
		throws RpcException
	{
		return new FailsafeClusterInvoker(directory);
	}
}
