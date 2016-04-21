// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InvokerInvocationHandler.java

package com.autohome.turbo.rpc.proxy;

import com.autohome.turbo.rpc.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerInvocationHandler
	implements InvocationHandler
{

	private final Invoker invoker;

	public InvokerInvocationHandler(Invoker handler)
	{
		invoker = handler;
	}

	public Object invoke(Object proxy, Method method, Object args[])
		throws Throwable
	{
		String methodName = method.getName();
		Class parameterTypes[] = method.getParameterTypes();
		if (method.getDeclaringClass() == java/lang/Object)
			return method.invoke(invoker, args);
		if ("toString".equals(methodName) && parameterTypes.length == 0)
			return invoker.toString();
		if ("hashCode".equals(methodName) && parameterTypes.length == 0)
			return Integer.valueOf(invoker.hashCode());
		if ("equals".equals(methodName) && parameterTypes.length == 1)
			return Boolean.valueOf(invoker.equals(args[0]));
		else
			return invoker.invoke(new RpcInvocation(method, args)).recreate();
	}
}
