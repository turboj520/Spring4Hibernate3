// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapProxyResolver.java

package com.autohome.burlap.client;

import com.autohome.burlap.io.BurlapRemoteResolver;
import java.io.IOException;

// Referenced classes of package com.autohome.burlap.client:
//			BurlapProxyFactory

public class BurlapProxyResolver
	implements BurlapRemoteResolver
{

	private BurlapProxyFactory factory;

	public BurlapProxyResolver(BurlapProxyFactory factory)
	{
		this.factory = factory;
	}

	public Object lookup(String type, String url)
		throws IOException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class api = Class.forName(type, false, loader);
		return factory.create(api, url);
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
