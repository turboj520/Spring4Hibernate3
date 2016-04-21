// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Node.java

package com.autohome.turbo.common;


// Referenced classes of package com.autohome.turbo.common:
//			URL

public interface Node
{

	public abstract URL getUrl();

	public abstract boolean isAvailable();

	public abstract void destroy();
}
