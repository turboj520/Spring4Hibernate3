// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RpcUtils.java

package com.autohome.turbo.rpc.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.rpc.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

public class RpcUtils
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/support/RpcUtils);
	private static final AtomicLong INVOKE_ID = new AtomicLong(0L);

	public RpcUtils()
	{
	}

	public static Class getReturnType(Invocation invocation)
	{
		Method method;
		if (invocation == null || invocation.getInvoker() == null || invocation.getInvoker().getUrl() == null || invocation.getMethodName().startsWith("$"))
			break MISSING_BLOCK_LABEL_123;
		String service = invocation.getInvoker().getUrl().getServiceInterface();
		if (service == null || service.length() <= 0)
			break MISSING_BLOCK_LABEL_123;
		Class cls = ReflectUtils.forName(service);
		method = cls.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
		if (method.getReturnType() == Void.TYPE)
			return null;
		return method.getReturnType();
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		return null;
	}

	public static Type[] getReturnTypes(Invocation invocation)
	{
		Method method;
		if (invocation == null || invocation.getInvoker() == null || invocation.getInvoker().getUrl() == null || invocation.getMethodName().startsWith("$"))
			break MISSING_BLOCK_LABEL_137;
		String service = invocation.getInvoker().getUrl().getServiceInterface();
		if (service == null || service.length() <= 0)
			break MISSING_BLOCK_LABEL_137;
		Class cls = ReflectUtils.forName(service);
		method = cls.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
		if (method.getReturnType() == Void.TYPE)
			return null;
		return (new Type[] {
			method.getReturnType(), method.getGenericReturnType()
		});
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		return null;
	}

	public static Long getInvocationId(Invocation inv)
	{
		String id = inv.getAttachment("id");
		return id != null ? new Long(id) : null;
	}

	public static void attachInvocationIdIfAsync(URL url, Invocation inv)
	{
		if (isAttachInvocationId(url, inv) && getInvocationId(inv) == null && (inv instanceof RpcInvocation))
			((RpcInvocation)inv).setAttachment("id", String.valueOf(INVOKE_ID.getAndIncrement()));
	}

	private static boolean isAttachInvocationId(URL url, Invocation invocation)
	{
		String value = url.getMethodParameter(invocation.getMethodName(), "invocationid.autoattach");
		if (value == null)
			return isAsync(url, invocation);
		return Boolean.TRUE.toString().equalsIgnoreCase(value);
	}

	public static String getMethodName(Invocation invocation)
	{
		if ("$invoke".equals(invocation.getMethodName()) && invocation.getArguments() != null && invocation.getArguments().length > 0 && (invocation.getArguments()[0] instanceof String))
			return (String)invocation.getArguments()[0];
		else
			return invocation.getMethodName();
	}

	public static Object[] getArguments(Invocation invocation)
	{
		if ("$invoke".equals(invocation.getMethodName()) && invocation.getArguments() != null && invocation.getArguments().length > 2 && (invocation.getArguments()[2] instanceof Object[]))
			return (Object[])(Object[])invocation.getArguments()[2];
		else
			return invocation.getArguments();
	}

	public static Class[] getParameterTypes(Invocation invocation)
	{
		if ("$invoke".equals(invocation.getMethodName()) && invocation.getArguments() != null && invocation.getArguments().length > 1 && (invocation.getArguments()[1] instanceof String[]))
		{
			String types[] = (String[])(String[])invocation.getArguments()[1];
			if (types == null)
				return new Class[0];
			Class parameterTypes[] = new Class[types.length];
			for (int i = 0; i < types.length; i++)
				parameterTypes[i] = ReflectUtils.forName(types[0]);

			return parameterTypes;
		} else
		{
			return invocation.getParameterTypes();
		}
	}

	public static boolean isAsync(URL url, Invocation inv)
	{
		boolean isAsync;
		if (Boolean.TRUE.toString().equals(inv.getAttachment("async")))
			isAsync = true;
		else
			isAsync = url.getMethodParameter(getMethodName(inv), "async", false);
		return isAsync;
	}

	public static boolean isOneway(URL url, Invocation inv)
	{
		boolean isOneway;
		if (Boolean.FALSE.toString().equals(inv.getAttachment("return")))
			isOneway = true;
		else
			isOneway = !url.getMethodParameter(getMethodName(inv), "return", true);
		return isOneway;
	}

}
