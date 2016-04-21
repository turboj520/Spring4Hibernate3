// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExporterListener.java

package com.autohome.turbo.rpc;


// Referenced classes of package com.autohome.turbo.rpc:
//			RpcException, Exporter

public interface ExporterListener
{

	public abstract void exported(Exporter exporter)
		throws RpcException;

	public abstract void unexported(Exporter exporter);
}
