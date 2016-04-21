// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JCache.java

package com.autohome.turbo.cache.support.jcache;

import com.autohome.turbo.cache.Cache;
import com.autohome.turbo.common.URL;
import javax.cache.CacheBuilder;
import javax.cache.CacheManager;
import javax.cache.Caching;

public class JCache
	implements Cache
{

	private final javax.cache.Cache store;

	public JCache(URL url)
	{
		String type = url.getParameter("jcache");
		CacheManager cacheManager = type != null && type.length() != 0 ? Caching.getCacheManager(type) : Caching.getCacheManager();
		CacheBuilder cacheBuilder = cacheManager.createCacheBuilder(url.getServiceKey());
		store = cacheBuilder.build();
	}

	public void put(Object key, Object value)
	{
		store.put(key, value);
	}

	public Object get(Object key)
	{
		return store.get(key);
	}
}
