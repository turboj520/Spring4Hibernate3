// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractHessianResolver.java

package com.autohome.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.hessian.io:
//			HessianRemote, HessianRemoteResolver

public class AbstractHessianResolver
	implements HessianRemoteResolver
{

	public AbstractHessianResolver()
	{
	}

	public Object lookup(String type, String url)
		throws IOException
	{
		return new HessianRemote(type, url);
	}
}
