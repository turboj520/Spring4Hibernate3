// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NameServer.java

package com.autohome.services.name;


// Referenced classes of package com.autohome.services.name:
//			NameServiceException

public interface NameServer
{

	public abstract Object lookup(String s)
		throws NameServiceException;

	public abstract String[] list()
		throws NameServiceException;
}
