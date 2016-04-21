// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianRemoteResolver.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

public interface HessianRemoteResolver
{

	public abstract Object lookup(String s, String s1)
		throws IOException;
}
