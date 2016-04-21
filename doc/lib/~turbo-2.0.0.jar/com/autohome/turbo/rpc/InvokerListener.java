// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InvokerListener.java

package com.autohome.turbo.rpc;


// Referenced classes of package com.autohome.turbo.rpc:
//			RpcException, Invoker

public interface InvokerListener
{

	public abstract void referred(Invoker invoker)
		throws RpcException;

	public abstract void destroyed(Invoker invoker);
}
