// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThreadPool.java

package com.autohome.turbo.common.threadpool;

import com.autohome.turbo.common.URL;
import java.util.concurrent.Executor;

public interface ThreadPool
{

	public abstract Executor getExecutor(URL url);
}
