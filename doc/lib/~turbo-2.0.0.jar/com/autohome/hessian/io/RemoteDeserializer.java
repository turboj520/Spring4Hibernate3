// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RemoteDeserializer.java

package com.autohome.hessian.io;

import java.util.logging.Logger;

// Referenced classes of package com.autohome.hessian.io:
//			JavaDeserializer, HessianRemote, HessianRemoteResolver, AbstractHessianInput, 
//			Deserializer

public class RemoteDeserializer extends JavaDeserializer
{

	private static final Logger log = Logger.getLogger(com/autohome/hessian/io/RemoteDeserializer.getName());
	public static final Deserializer DESER = new RemoteDeserializer();

	public RemoteDeserializer()
	{
		super(com/autohome/hessian/io/HessianRemote);
	}

	public boolean isReadResolve()
	{
		return true;
	}

	protected Object resolve(AbstractHessianInput in, Object obj)
		throws Exception
	{
		HessianRemote remote = (HessianRemote)obj;
		HessianRemoteResolver resolver = in.getRemoteResolver();
		if (resolver != null)
		{
			Object proxy = resolver.lookup(remote.getType(), remote.getURL());
			return proxy;
		} else
		{
			return remote;
		}
	}

}
