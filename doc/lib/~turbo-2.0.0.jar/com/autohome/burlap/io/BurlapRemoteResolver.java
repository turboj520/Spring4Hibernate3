// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BurlapRemoteResolver.java

package com.autohome.burlap.io;

import com.autohome.hessian.io.HessianRemoteResolver;
import java.io.IOException;

public interface BurlapRemoteResolver
	extends HessianRemoteResolver
{

	public abstract Object lookup(String s, String s1)
		throws IOException;
}
