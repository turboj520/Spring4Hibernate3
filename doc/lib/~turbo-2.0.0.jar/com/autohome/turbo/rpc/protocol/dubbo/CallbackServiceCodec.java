// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CallbackServiceCodec.java

package com.autohome.turbo.rpc.protocol.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConcurrentHashSet;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.rpc.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

// Referenced classes of package com.autohome.turbo.rpc.protocol.dubbo:
//			ChannelWrappedInvoker, DubboProtocol

class CallbackServiceCodec
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/CallbackServiceCodec);
	private static final ProxyFactory proxyFactory = (ProxyFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/ProxyFactory).getAdaptiveExtension();
	private static final DubboProtocol protocol = DubboProtocol.getDubboProtocol();
	private static final byte CALLBACK_NONE = 0;
	private static final byte CALLBACK_CREATE = 1;
	private static final byte CALLBACK_DESTROY = 2;
	private static final String INV_ATT_CALLBACK_KEY = "sys_callback_arg-";

	CallbackServiceCodec()
	{
	}

	private static byte isCallBack(URL url, String methodName, int argIndex)
	{
		byte isCallback = 0;
		if (url != null)
		{
			String callback = url.getParameter((new StringBuilder()).append(methodName).append(".").append(argIndex).append(".callback").toString());
			if (callback != null)
				if (callback.equalsIgnoreCase("true"))
					isCallback = 1;
				else
				if (callback.equalsIgnoreCase("false"))
					isCallback = 2;
		}
		return isCallback;
	}

	private static String exportOrunexportCallbackService(Channel channel, URL url, Class clazz, Object inst, Boolean export)
		throws IOException
	{
		int instid = System.identityHashCode(inst);
		Map params = new HashMap(3);
		params.put("isserver", Boolean.FALSE.toString());
		params.put("is_callback_service", Boolean.TRUE.toString());
		String group = url.getParameter("group");
		if (group != null && group.length() > 0)
			params.put("group", group);
		params.put("methods", StringUtils.join(Wrapper.getWrapper(clazz).getDeclaredMethodNames(), ","));
		Map tmpmap = new HashMap(url.getParameters());
		tmpmap.putAll(params);
		tmpmap.remove("version");
		tmpmap.put("interface", clazz.getName());
		URL exporturl = new URL("dubbo", channel.getLocalAddress().getAddress().getHostAddress(), channel.getLocalAddress().getPort(), (new StringBuilder()).append(clazz.getName()).append(".").append(instid).toString(), tmpmap);
		String cacheKey = getClientSideCallbackServiceCacheKey(instid);
		String countkey = getClientSideCountKey(clazz.getName());
		if (export.booleanValue())
		{
			if (!channel.hasAttribute(cacheKey) && !isInstancesOverLimit(channel, url, clazz.getName(), instid, false))
			{
				Invoker invoker = proxyFactory.getInvoker(inst, clazz, exporturl);
				Exporter exporter = protocol.export(invoker);
				channel.setAttribute(cacheKey, exporter);
				logger.info((new StringBuilder()).append("export a callback service :").append(exporturl).append(", on ").append(channel).append(", url is: ").append(url).toString());
				increaseInstanceCount(channel, countkey);
			}
		} else
		if (channel.hasAttribute(cacheKey))
		{
			Exporter exporter = (Exporter)channel.getAttribute(cacheKey);
			exporter.unexport();
			channel.removeAttribute(cacheKey);
			decreaseInstanceCount(channel, countkey);
		}
		return String.valueOf(instid);
	}

	private static Object referOrdestroyCallbackService(Channel channel, URL url, Class clazz, Invocation inv, int instid, boolean isRefer)
	{
		Object proxy = null;
		String invokerCacheKey = getServerSideCallbackInvokerCacheKey(channel, clazz.getName(), instid);
		String proxyCacheKey = getServerSideCallbackServiceCacheKey(channel, clazz.getName(), instid);
		proxy = channel.getAttribute(proxyCacheKey);
		String countkey = getServerSideCountKey(channel, clazz.getName());
		if (isRefer)
		{
			if (proxy == null)
			{
				URL referurl = URL.valueOf((new StringBuilder()).append("callback://").append(url.getAddress()).append("/").append(clazz.getName()).append("?").append("interface").append("=").append(clazz.getName()).toString());
				referurl = referurl.addParametersIfAbsent(url.getParameters()).removeParameter("methods");
				if (!isInstancesOverLimit(channel, referurl, clazz.getName(), instid, true))
				{
					Invoker invoker = new ChannelWrappedInvoker(clazz, channel, referurl, String.valueOf(instid));
					proxy = proxyFactory.getProxy(invoker);
					channel.setAttribute(proxyCacheKey, proxy);
					channel.setAttribute(invokerCacheKey, invoker);
					increaseInstanceCount(channel, countkey);
					Set callbackInvokers = (Set)channel.getAttribute("channel.callback.invokers.key");
					if (callbackInvokers == null)
					{
						callbackInvokers = new ConcurrentHashSet(1);
						callbackInvokers.add(invoker);
						channel.setAttribute("channel.callback.invokers.key", callbackInvokers);
					}
					logger.info((new StringBuilder()).append("method ").append(inv.getMethodName()).append(" include a callback service :").append(invoker.getUrl()).append(", a proxy :").append(invoker).append(" has been created.").toString());
				}
			}
		} else
		if (proxy != null)
		{
			Invoker invoker = (Invoker)channel.getAttribute(invokerCacheKey);
			try
			{
				Set callbackInvokers = (Set)channel.getAttribute("channel.callback.invokers.key");
				if (callbackInvokers != null)
					callbackInvokers.remove(invoker);
				invoker.destroy();
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
			channel.removeAttribute(proxyCacheKey);
			channel.removeAttribute(invokerCacheKey);
			decreaseInstanceCount(channel, countkey);
		}
		return proxy;
	}

	private static String getClientSideCallbackServiceCacheKey(int instid)
	{
		return (new StringBuilder()).append("callback.service.instid.").append(instid).toString();
	}

	private static String getServerSideCallbackServiceCacheKey(Channel channel, String interfaceClass, int instid)
	{
		return (new StringBuilder()).append("callback.service.proxy.").append(System.identityHashCode(channel)).append(".").append(interfaceClass).append(".").append(instid).toString();
	}

	private static String getServerSideCallbackInvokerCacheKey(Channel channel, String interfaceClass, int instid)
	{
		return (new StringBuilder()).append(getServerSideCallbackServiceCacheKey(channel, interfaceClass, instid)).append(".").append("invoker").toString();
	}

	private static String getClientSideCountKey(String interfaceClass)
	{
		return (new StringBuilder()).append("callback.service.instid.").append(interfaceClass).append(".COUNT").toString();
	}

	private static String getServerSideCountKey(Channel channel, String interfaceClass)
	{
		return (new StringBuilder()).append("callback.service.proxy.").append(System.identityHashCode(channel)).append(".").append(interfaceClass).append(".COUNT").toString();
	}

	private static boolean isInstancesOverLimit(Channel channel, URL url, String interfaceClass, int instid, boolean isServer)
	{
		Integer count = (Integer)channel.getAttribute(isServer ? getServerSideCountKey(channel, interfaceClass) : getClientSideCountKey(interfaceClass));
		int limit = url.getParameter("callbacks", 1);
		if (count != null && count.intValue() >= limit)
			throw new IllegalStateException((new StringBuilder()).append("interface ").append(interfaceClass).append(" `s callback instances num exceed providers limit :").append(limit).append(" ,current num: ").append(count.intValue() + 1).append(". The new callback service will not work !!! you can cancle the callback service which exported before. channel :").append(channel).toString());
		else
			return false;
	}

	private static void increaseInstanceCount(Channel channel, String countkey)
	{
		try
		{
			Integer count = (Integer)channel.getAttribute(countkey);
			if (count == null)
			{
				count = Integer.valueOf(1);
			} else
			{
				Integer integer = count;
				Integer integer1 = count = Integer.valueOf(count.intValue() + 1);
				Integer  = integer;
			}
			channel.setAttribute(countkey, count);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	private static void decreaseInstanceCount(Channel channel, String countkey)
	{
		Integer count;
		count = (Integer)channel.getAttribute(countkey);
		if (count == null || count.intValue() <= 0)
			return;
		try
		{
			Integer integer = count;
			Integer integer1 = count = Integer.valueOf(count.intValue() - 1);
			integer;
			channel.setAttribute(countkey, count);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return;
	}

	public static Object encodeInvocationArgument(Channel channel, RpcInvocation inv, int paraIndex)
		throws IOException
	{
		URL url = inv.getInvoker() != null ? inv.getInvoker().getUrl() : null;
		byte callbackstatus = isCallBack(url, inv.getMethodName(), paraIndex);
		Object args[] = inv.getArguments();
		Class pts[] = inv.getParameterTypes();
		switch (callbackstatus)
		{
		case 0: // '\0'
			return args[paraIndex];

		case 1: // '\001'
			inv.setAttachment((new StringBuilder()).append("sys_callback_arg-").append(paraIndex).toString(), exportOrunexportCallbackService(channel, url, pts[paraIndex], args[paraIndex], Boolean.valueOf(true)));
			return null;

		case 2: // '\002'
			inv.setAttachment((new StringBuilder()).append("sys_callback_arg-").append(paraIndex).toString(), exportOrunexportCallbackService(channel, url, pts[paraIndex], args[paraIndex], Boolean.valueOf(false)));
			return null;
		}
		return args[paraIndex];
	}

	public static Object decodeInvocationArgument(Channel channel, RpcInvocation inv, Class pts[], int paraIndex, Object inObject)
		throws IOException
	{
		URL url;
		byte callbackstatus;
		url = null;
		try
		{
			url = DubboProtocol.getDubboProtocol().getInvoker(channel, inv).getUrl();
		}
		catch (RemotingException e)
		{
			if (logger.isInfoEnabled())
				logger.info(e.getMessage(), e);
			return inObject;
		}
		callbackstatus = isCallBack(url, inv.getMethodName(), paraIndex);
		callbackstatus;
		JVM INSTR tableswitch 0 2: default 216
	//	               0 92
	//	               1 95
	//	               2 163;
		   goto _L1 _L2 _L3 _L4
_L2:
		return inObject;
_L3:
		return referOrdestroyCallbackService(channel, url, pts[paraIndex], inv, Integer.parseInt(inv.getAttachment((new StringBuilder()).append("sys_callback_arg-").append(paraIndex).toString())), true);
		Exception e;
		e;
		logger.error(e.getMessage(), e);
		throw new IOException(StringUtils.toString(e));
_L4:
		return referOrdestroyCallbackService(channel, url, pts[paraIndex], inv, Integer.parseInt(inv.getAttachment((new StringBuilder()).append("sys_callback_arg-").append(paraIndex).toString())), false);
		e;
		throw new IOException(StringUtils.toString(e));
_L1:
		return inObject;
	}

}
