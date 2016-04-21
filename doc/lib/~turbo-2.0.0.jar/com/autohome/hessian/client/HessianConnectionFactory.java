// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianConnectionFactory.java

package com.autohome.hessian.client;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

// Referenced classes of package com.autohome.hessian.client:
//			HessianProxyFactory, HessianConnection

public interface HessianConnectionFactory
{

	public abstract void setHessianProxyFactory(HessianProxyFactory hessianproxyfactory);

	public abstract HessianConnection open(URL url)
		throws IOException;

	public abstract HessianConnection open(URL url, Proxy proxy)
		throws IOException;
}
