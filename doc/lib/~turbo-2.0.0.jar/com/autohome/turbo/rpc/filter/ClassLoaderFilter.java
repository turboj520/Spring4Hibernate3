// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClassLoaderFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.rpc.*;

public class ClassLoaderFilter
	implements Filter
{

	public ClassLoaderFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		ClassLoader ocl;
		ocl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(invoker.getInterface().getClassLoader());
		Result result = invoker.invoke(invocation);
		Thread.currentThread().setContextClassLoader(ocl);
		return result;
		Exception exception;
		exception;
		Thread.currentThread().setContextClassLoader(ocl);
		throw exception;
	}
}
