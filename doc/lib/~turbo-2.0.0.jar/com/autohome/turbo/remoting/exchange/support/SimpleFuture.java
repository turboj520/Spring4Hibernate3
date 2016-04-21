// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SimpleFuture.java

package com.autohome.turbo.remoting.exchange.support;

import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.ResponseCallback;
import com.autohome.turbo.remoting.exchange.ResponseFuture;

public class SimpleFuture
	implements ResponseFuture
{

	private final Object value;

	public SimpleFuture(Object value)
	{
		this.value = value;
	}

	public Object get()
		throws RemotingException
	{
		return value;
	}

	public Object get(int timeoutInMillis)
		throws RemotingException
	{
		return value;
	}

	public void setCallback(ResponseCallback callback)
	{
		callback.done(value);
	}

	public boolean isDone()
	{
		return true;
	}
}
