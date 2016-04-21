// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConsistentHashLoadBalance.java

package com.autohome.turbo.rpc.cluster.loadbalance;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invocation;
import com.autohome.turbo.rpc.Invoker;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.rpc.cluster.loadbalance:
//			AbstractLoadBalance

public class ConsistentHashLoadBalance extends AbstractLoadBalance
{
	private static final class ConsistentHashSelector
	{

		private final TreeMap virtualInvokers = new TreeMap();
		private final int replicaNumber;
		private final int identityHashCode;
		private final int argumentIndex[];

		public int getIdentityHashCode()
		{
			return identityHashCode;
		}

		public Invoker select(Invocation invocation)
		{
			String key = toKey(invocation.getArguments());
			byte digest[] = md5(key);
			Invoker invoker = sekectForKey(hash(digest, 0));
			return invoker;
		}

		private String toKey(Object args[])
		{
			StringBuilder buf = new StringBuilder();
			int arr$[] = argumentIndex;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				int i = arr$[i$];
				if (i >= 0 && i < args.length)
					buf.append(args[i]);
			}

			return buf.toString();
		}

		private Invoker sekectForKey(long hash)
		{
			Long key = Long.valueOf(hash);
			if (!virtualInvokers.containsKey(key))
			{
				SortedMap tailMap = virtualInvokers.tailMap(key);
				if (tailMap.isEmpty())
					key = (Long)virtualInvokers.firstKey();
				else
					key = (Long)tailMap.firstKey();
			}
			Invoker invoker = (Invoker)virtualInvokers.get(key);
			return invoker;
		}

		private long hash(byte digest[], int number)
		{
			return ((long)(digest[3 + number * 4] & 0xff) << 24 | (long)(digest[2 + number * 4] & 0xff) << 16 | (long)(digest[1 + number * 4] & 0xff) << 8 | (long)(digest[0 + number * 4] & 0xff)) & 0xffffffffL;
		}

		private byte[] md5(String value)
		{
			MessageDigest md5;
			try
			{
				md5 = MessageDigest.getInstance("MD5");
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
			md5.reset();
			byte bytes[] = null;
			try
			{
				bytes = value.getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
			md5.update(bytes);
			return md5.digest();
		}

		public ConsistentHashSelector(List invokers, String methodName, int identityHashCode)
		{
			this.identityHashCode = System.identityHashCode(invokers);
			URL url = ((Invoker)invokers.get(0)).getUrl();
			replicaNumber = url.getMethodParameter(methodName, "hash.nodes", 160);
			String index[] = Constants.COMMA_SPLIT_PATTERN.split(url.getMethodParameter(methodName, "hash.arguments", "0"));
			argumentIndex = new int[index.length];
			for (int i = 0; i < index.length; i++)
				argumentIndex[i] = Integer.parseInt(index[i]);

			for (Iterator i$ = invokers.iterator(); i$.hasNext();)
			{
				Invoker invoker = (Invoker)i$.next();
				int i = 0;
				while (i < replicaNumber / 4) 
				{
					byte digest[] = md5((new StringBuilder()).append(invoker.getUrl().toFullString()).append(i).toString());
					for (int h = 0; h < 4; h++)
					{
						long m = hash(digest, h);
						virtualInvokers.put(Long.valueOf(m), invoker);
					}

					i++;
				}
			}

		}
	}


	private final ConcurrentMap selectors = new ConcurrentHashMap();

	public ConsistentHashLoadBalance()
	{
	}

	protected Invoker doSelect(List invokers, URL url, Invocation invocation)
	{
		String key = (new StringBuilder()).append(((Invoker)invokers.get(0)).getUrl().getServiceKey()).append(".").append(invocation.getMethodName()).toString();
		int identityHashCode = System.identityHashCode(invokers);
		ConsistentHashSelector selector = (ConsistentHashSelector)selectors.get(key);
		if (selector == null || selector.getIdentityHashCode() != identityHashCode)
		{
			selectors.put(key, new ConsistentHashSelector(invokers, invocation.getMethodName(), identityHashCode));
			selector = (ConsistentHashSelector)selectors.get(key);
		}
		return selector.select(invocation);
	}
}
