// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThreadLocalCache.java

package com.autohome.turbo.cache.support.threadlocal;

import com.autohome.turbo.cache.Cache;
import com.autohome.turbo.common.URL;
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalCache
	implements Cache
{

	private final ThreadLocal store = new ThreadLocal() {

		final ThreadLocalCache this$0;

		protected Map initialValue()
		{
			return new HashMap();
		}

		protected volatile Object initialValue()
		{
			return initialValue();
		}

			
			{
				this$0 = ThreadLocalCache.this;
				super();
			}
	};

	public ThreadLocalCache(URL url)
	{
	}

	public void put(Object key, Object value)
	{
		((Map)store.get()).put(key, value);
	}

	public Object get(Object key)
	{
		return ((Map)store.get()).get(key);
	}
}
