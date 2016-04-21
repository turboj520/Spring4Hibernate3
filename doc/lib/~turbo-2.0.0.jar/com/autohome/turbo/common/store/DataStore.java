// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DataStore.java

package com.autohome.turbo.common.store;

import java.util.Map;

public interface DataStore
{

	public abstract Map get(String s);

	public abstract Object get(String s, String s1);

	public abstract void put(String s, String s1, Object obj);

	public abstract void remove(String s, String s1);
}
