// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceProxyFactory.java

package com.autohome.services.client;

import java.net.MalformedURLException;

public interface ServiceProxyFactory
{

	public abstract Object create(Class class1, String s)
		throws MalformedURLException;
}
