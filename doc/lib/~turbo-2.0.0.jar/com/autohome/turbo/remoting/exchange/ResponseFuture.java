// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ResponseFuture.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.remoting.RemotingException;

// Referenced classes of package com.autohome.turbo.remoting.exchange:
//			ResponseCallback

public interface ResponseFuture
{

	public abstract Object get()
		throws RemotingException;

	public abstract Object get(int i)
		throws RemotingException;

	public abstract void setCallback(ResponseCallback responsecallback);

	public abstract boolean isDone();
}
