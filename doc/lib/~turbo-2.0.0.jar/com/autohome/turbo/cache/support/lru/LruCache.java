// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LruCache.java

package com.autohome.turbo.cache.support.lru;

import com.autohome.turbo.cache.Cache;
import com.autohome.turbo.common.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache
	implements Cache
{

	private final Map store;

	public LruCache(URL url)
	{
		final int max = url.getParameter("cache.size", 1000);
		store = new LinkedHashMap() {

			private static final long serialVersionUID = 0xcaca270527164b2bL;
			final int val$max;
			final LruCache this$0;

			protected boolean removeEldestEntry(java.util.Map.Entry eldest)
			{
				return size() > max;
			}

			
			{
				this$0 = LruCache.this;
				max = i;
				super();
			}
		};
	}

	public void put(Object key, Object value)
	{
		synchronized (store)
		{
			store.put(key, value);
		}
	}

	public Object get(Object key)
	{
		Map map = store;
		JVM INSTR monitorenter ;
		return store.get(key);
		Exception exception;
		exception;
		throw exception;
	}
}
