// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JCacheFactory.java

package com.autohome.turbo.cache.support.jcache;

import com.autohome.turbo.cache.Cache;
import com.autohome.turbo.cache.support.AbstractCacheFactory;
import com.autohome.turbo.common.URL;

// Referenced classes of package com.autohome.turbo.cache.support.jcache:
//			JCache

public class JCacheFactory extends AbstractCacheFactory
{

	public JCacheFactory()
	{
	}

	protected Cache createCache(URL url)
	{
		return new JCache(url);
	}
}
