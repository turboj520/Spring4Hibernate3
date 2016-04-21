// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ReferenceConfigCache.java

package com.autohome.turbo.config.utils;

import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.config.ReferenceConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReferenceConfigCache
{
	public static interface KeyGenerator
	{

		public abstract String generateKey(ReferenceConfig referenceconfig);
	}


	public static final String DEFAULT_NAME = "_DEFAULT_";
	static final ConcurrentMap cacheHolder = new ConcurrentHashMap();
	public static final KeyGenerator DEFAULT_KEY_GENERATOR = new KeyGenerator() {

		public String generateKey(ReferenceConfig referenceConfig)
		{
			String iName = referenceConfig.getInterface();
			if (StringUtils.isBlank(iName))
			{
				Class clazz = referenceConfig.getInterfaceClass();
				iName = clazz.getName();
			}
			if (StringUtils.isBlank(iName))
				throw new IllegalArgumentException((new StringBuilder()).append("No interface info in ReferenceConfig").append(referenceConfig).toString());
			StringBuilder ret = new StringBuilder();
			if (!StringUtils.isBlank(referenceConfig.getGroup()))
				ret.append(referenceConfig.getGroup()).append("/");
			ret.append(iName);
			if (!StringUtils.isBlank(referenceConfig.getVersion()))
				ret.append(":").append(referenceConfig.getVersion());
			return ret.toString();
		}

	};
	private final String name;
	private final KeyGenerator generator;
	ConcurrentMap cache;

	public static ReferenceConfigCache getCache()
	{
		return getCache("_DEFAULT_");
	}

	public static ReferenceConfigCache getCache(String name)
	{
		return getCache(name, DEFAULT_KEY_GENERATOR);
	}

	public static ReferenceConfigCache getCache(String name, KeyGenerator keyGenerator)
	{
		ReferenceConfigCache cache = (ReferenceConfigCache)cacheHolder.get(name);
		if (cache != null)
		{
			return cache;
		} else
		{
			cacheHolder.putIfAbsent(name, new ReferenceConfigCache(name, keyGenerator));
			return (ReferenceConfigCache)cacheHolder.get(name);
		}
	}

	private ReferenceConfigCache(String name, KeyGenerator generator)
	{
		cache = new ConcurrentHashMap();
		this.name = name;
		this.generator = generator;
	}

	public Object get(ReferenceConfig referenceConfig)
	{
		String key = generator.generateKey(referenceConfig);
		ReferenceConfig config = (ReferenceConfig)cache.get(key);
		if (config != null)
		{
			return config.get();
		} else
		{
			cache.putIfAbsent(key, referenceConfig);
			config = (ReferenceConfig)cache.get(key);
			return config.get();
		}
	}

	void destroyKey(String key)
	{
		ReferenceConfig config = (ReferenceConfig)cache.remove(key);
		if (config == null)
		{
			return;
		} else
		{
			config.destroy();
			return;
		}
	}

	public void destroy(ReferenceConfig referenceConfig)
	{
		String key = generator.generateKey(referenceConfig);
		destroyKey(key);
	}

	public void destroyAll()
	{
		Set set = new HashSet(cache.keySet());
		String key;
		for (Iterator i$ = set.iterator(); i$.hasNext(); destroyKey(key))
			key = (String)i$.next();

	}

	public String toString()
	{
		return (new StringBuilder()).append("ReferenceConfigCache(name: ").append(name).append(")").toString();
	}

}
