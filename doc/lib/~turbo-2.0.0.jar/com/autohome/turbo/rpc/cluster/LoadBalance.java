// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LoadBalance.java

package com.autohome.turbo.rpc.cluster;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.*;
import java.util.List;

public interface LoadBalance
{

	public abstract Invoker select(List list, URL url, Invocation invocation)
		throws RpcException;
}
