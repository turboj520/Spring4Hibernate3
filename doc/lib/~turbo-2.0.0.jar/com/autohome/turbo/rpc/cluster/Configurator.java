// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Configurator.java

package com.autohome.turbo.rpc.cluster;

import com.autohome.turbo.common.URL;

public interface Configurator
	extends Comparable
{

	public abstract URL getUrl();

	public abstract URL configure(URL url);
}
