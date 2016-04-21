// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Directory.java

package com.autohome.turbo.rpc.cluster;

import com.autohome.turbo.common.Node;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.RpcException;
import java.util.List;

public interface Directory
	extends Node
{

	public abstract Class getInterface();

	public abstract List list(Invocation invocation)
		throws RpcException;
}
