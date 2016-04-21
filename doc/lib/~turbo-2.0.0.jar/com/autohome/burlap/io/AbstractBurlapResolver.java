// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractBurlapResolver.java

package com.autohome.burlap.io;

import java.io.IOException;

// Referenced classes of package com.autohome.burlap.io:
//			BurlapRemote, BurlapRemoteResolver

public class AbstractBurlapResolver
	implements BurlapRemoteResolver
{

	public AbstractBurlapResolver()
	{
	}

	public Object lookup(String type, String url)
		throws IOException
	{
		return new BurlapRemote(type, url);
	}
}
