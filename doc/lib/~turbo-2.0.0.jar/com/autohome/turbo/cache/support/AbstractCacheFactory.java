// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractCacheFactory.java

package com.autohome.turbo.cache.support;

import com.autohome.turbo.cache.Cache;
import com.autohome.turbo.cache.CacheFactory;
import com.autohome.turbo.common.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCacheFactory
	implements CacheFactory
{

	private final ConcurrentMap caches = new ConcurrentHashMap();

	public AbstractCacheFactory()
	{
	}

	public Cache getCache(URL url)
	{
		String key = url.toFullString();
		Cache cache = (Cache)caches.get(key);
		if (cache == null)
		{
			caches.put(key, createCache(url));
			cache = (Cache)caches.get(key);
		}
		return cache;
	}

	protected abstract Cache createCache(URL url);
}
