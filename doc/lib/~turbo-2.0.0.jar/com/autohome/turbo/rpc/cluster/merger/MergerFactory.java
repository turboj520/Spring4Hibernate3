// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MergerFactory.java

package com.autohome.turbo.rpc.cluster.merger;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.rpc.cluster.Merger;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Referenced classes of package com.autohome.turbo.rpc.cluster.merger:
//			ArrayMerger

public class MergerFactory
{

	private static final ConcurrentMap mergerCache = new ConcurrentHashMap();

	public MergerFactory()
	{
	}

	public static Merger getMerger(Class returnType)
	{
		Merger result;
		if (returnType.isArray())
		{
			Class type = returnType.getComponentType();
			result = (Merger)mergerCache.get(type);
			if (result == null)
			{
				loadMergers();
				result = (Merger)mergerCache.get(type);
			}
			if (result == null && !type.isPrimitive())
				result = ArrayMerger.INSTANCE;
		} else
		{
			result = (Merger)mergerCache.get(returnType);
			if (result == null)
			{
				loadMergers();
				result = (Merger)mergerCache.get(returnType);
			}
		}
		return result;
	}

	static void loadMergers()
	{
		Set names = ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/Merger).getSupportedExtensions();
		Merger m;
		for (Iterator i$ = names.iterator(); i$.hasNext(); mergerCache.putIfAbsent(ReflectUtils.getGenericClass(m.getClass()), m))
		{
			String name = (String)i$.next();
			m = (Merger)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/cluster/Merger).getExtension(name);
		}

	}

}
