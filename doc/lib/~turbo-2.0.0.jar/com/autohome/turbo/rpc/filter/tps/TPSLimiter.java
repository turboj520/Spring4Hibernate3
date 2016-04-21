// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TPSLimiter.java

package com.autohome.turbo.rpc.filter.tps;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;

public interface TPSLimiter
{

	public abstract boolean isAllowable(URL url, Invocation invocation);
}
