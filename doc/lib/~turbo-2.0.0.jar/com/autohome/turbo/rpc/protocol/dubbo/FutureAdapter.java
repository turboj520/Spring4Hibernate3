// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FutureAdapter.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.TimeoutException;
import com.autohome.turbo.remoting.exchange.ResponseFuture;
import com.autohome.turbo.rpc.Result;
import com.autohome.turbo.rpc.RpcException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureAdapter
	implements Future
{

	private final ResponseFuture future;

	public FutureAdapter(ResponseFuture future)
	{
		this.future = future;
	}

	public ResponseFuture getFuture()
	{
		return future;
	}

	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return false;
	}

	public boolean isCancelled()
	{
		return false;
	}

	public boolean isDone()
	{
		return future.isDone();
	}

	public Object get()
		throws InterruptedException, ExecutionException
	{
		return ((Result)future.get()).recreate();
		RemotingException e;
		e;
		throw new ExecutionException(e.getMessage(), e);
		e;
		throw new RpcException(e);
	}

	public Object get(long timeout, TimeUnit unit)
		throws InterruptedException, ExecutionException, java.util.concurrent.TimeoutException
	{
		int timeoutInMillis = (int)unit.convert(timeout, TimeUnit.MILLISECONDS);
		return ((Result)future.get(timeoutInMillis)).recreate();
		TimeoutException e;
		e;
		throw new java.util.concurrent.TimeoutException(StringUtils.toString(e));
		e;
		throw new ExecutionException(e.getMessage(), e);
		e;
		throw new RpcException(e);
	}
}
