// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConcurrentHashSet.java

package com.autohome.turbo.common.utils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet extends AbstractSet
	implements Set, Serializable
{

	private static final long serialVersionUID = 0x87a672d1a8b27cd2L;
	private static final Object PRESENT = new Object();
	private final ConcurrentHashMap map;

	public ConcurrentHashSet()
	{
		map = new ConcurrentHashMap();
	}

	public ConcurrentHashSet(int initialCapacity)
	{
		map = new ConcurrentHashMap(initialCapacity);
	}

	public Iterator iterator()
	{
		return map.keySet().iterator();
	}

	public int size()
	{
		return map.size();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public boolean contains(Object o)
	{
		return map.containsKey(o);
	}

	public boolean add(Object e)
	{
		return map.put(e, PRESENT) == null;
	}

	public boolean remove(Object o)
	{
		return map.remove(o) == PRESENT;
	}

	public void clear()
	{
		map.clear();
	}

}
