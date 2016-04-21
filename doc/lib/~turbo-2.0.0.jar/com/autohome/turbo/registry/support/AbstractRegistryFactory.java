// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractRegistryFactory.java

package com.autohome.turbo.registry.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.registry.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractRegistryFactory
	implements RegistryFactory
{

	private static final Logger LOGGER = LoggerFactory.getLogger(com/autohome/turbo/registry/support/AbstractRegistryFactory);
	private static final ReentrantLock LOCK = new ReentrantLock();
	private static final Map REGISTRIES = new ConcurrentHashMap();

	public AbstractRegistryFactory()
	{
	}

	public static Collection getRegistries()
	{
		return Collections.unmodifiableCollection(REGISTRIES.values());
	}

	public static void destroyAll()
	{
		if (LOGGER.isInfoEnabled())
			LOGGER.info((new StringBuilder()).append("Close all registries ").append(getRegistries()).toString());
		LOCK.lock();
		for (Iterator i$ = getRegistries().iterator(); i$.hasNext();)
		{
			Registry registry = (Registry)i$.next();
			try
			{
				registry.destroy();
			}
			catch (Throwable e)
			{
				LOGGER.error(e.getMessage(), e);
			}
		}

		REGISTRIES.clear();
		LOCK.unlock();
		break MISSING_BLOCK_LABEL_126;
		Exception exception;
		exception;
		LOCK.unlock();
		throw exception;
	}

	public Registry getRegistry(URL url)
	{
		String key;
		url = url.setPath(com/autohome/turbo/registry/RegistryService.getName()).addParameter("interface", com/autohome/turbo/registry/RegistryService.getName()).removeParameters(new String[] {
			"export", "refer"
		});
		key = url.toServiceString();
		LOCK.lock();
		Registry registry1;
		Registry registry = (Registry)REGISTRIES.get(key);
		if (registry == null)
			break MISSING_BLOCK_LABEL_79;
		registry1 = registry;
		LOCK.unlock();
		return registry1;
		Registry registry = createRegistry(url);
		if (registry == null)
			throw new IllegalStateException((new StringBuilder()).append("Can not create registry ").append(url).toString());
		REGISTRIES.put(key, registry);
		registry1 = registry;
		LOCK.unlock();
		return registry1;
		Exception exception;
		exception;
		LOCK.unlock();
		throw exception;
	}

	protected abstract Registry createRegistry(URL url);

}
