// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Cluster.java

package com.autohome.turbo.rpc.cluster;

import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.RpcException;

// Referenced classes of package com.autohome.turbo.rpc.cluster:
//			Directory

public interface Cluster
{

	public abstract Invoker join(Directory directory)
		throws RpcException;
}
