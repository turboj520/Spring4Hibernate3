// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericService.java

package com.autohome.turbo.rpc.service;


// Referenced classes of package com.autohome.turbo.rpc.service:
//			GenericException

public interface GenericService
{

	public abstract Object $invoke(String s, String as[], Object aobj[])
		throws GenericException;
}
