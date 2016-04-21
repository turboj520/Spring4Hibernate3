// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LRUCache.java

package com.autohome.turbo.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache extends LinkedHashMap
{

	private static final long serialVersionUID = 0xb848e23fa267c717L;
	private static final float DEFAULT_LOAD_FACTOR = 0.75F;
	private static final int DEFAULT_MAX_CAPACITY = 1000;
	private volatile int maxCapacity;
	private final Lock lock;

	public LRUCache()
	{
		this(1000);
	}

	public LRUCache(int maxCapacity)
	{
		super(16, 0.75F, true);
		lock = new ReentrantLock();
		this.maxCapacity = maxCapacity;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry eldest)
	{
		return size() > maxCapacity;
	}

	public boolean containsKey(Object key)
	{
		boolean flag;
		lock.lock();
		flag = super.containsKey(key);
		lock.unlock();
		return flag;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
	}

	public Object get(Object key)
	{
		Object obj;
		lock.lock();
		obj = super.get(key);
		lock.unlock();
		return obj;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
	}

	public Object put(Object key, Object value)
	{
		Object obj;
		lock.lock();
		obj = super.put(key, value);
		lock.unlock();
		return obj;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
	}

	public Object remove(Object key)
	{
		Object obj;
		lock.lock();
		obj = super.remove(key);
		lock.unlock();
		return obj;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
	}

	public int size()
	{
		int i;
		lock.lock();
		i = super.size();
		lock.unlock();
		return i;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
	}

	public void clear()
	{
		lock.lock();
		super.clear();
		lock.unlock();
		break MISSING_BLOCK_LABEL_37;
		Exception exception;
		exception;
		lock.unlock();
		throw exception;
	}

	public int getMaxCapacity()
	{
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity)
	{
		this.maxCapacity = maxCapacity;
	}
}
