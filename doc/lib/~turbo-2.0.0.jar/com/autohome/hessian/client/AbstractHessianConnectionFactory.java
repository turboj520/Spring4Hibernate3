// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractHessianConnectionFactory.java

package com.autohome.hessian.client;

import java.io.IOException;
import java.net.URL;

// Referenced classes of package com.autohome.hessian.client:
//			HessianConnectionFactory, HessianProxyFactory, HessianConnection

public abstract class AbstractHessianConnectionFactory
	implements HessianConnectionFactory
{

	private HessianProxyFactory _factory;

	public AbstractHessianConnectionFactory()
	{
	}

	public void setHessianProxyFactory(HessianProxyFactory factory)
	{
		_factory = factory;
	}

	public HessianProxyFactory getHessianProxyFactory()
	{
		return _factory;
	}

	public abstract HessianConnection open(URL url)
		throws IOException;
}
