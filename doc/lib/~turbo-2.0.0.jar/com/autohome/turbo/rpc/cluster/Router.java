// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Router.java

package com.autohome.turbo.rpc.cluster;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.RpcException;
import java.util.List;

public interface Router
	extends Comparable
{

	public abstract URL getUrl();

	public abstract List route(List list, URL url, Invocation invocation)
		throws RpcException;
}
