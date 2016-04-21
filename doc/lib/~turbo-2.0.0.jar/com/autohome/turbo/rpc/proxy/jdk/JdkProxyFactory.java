// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JdkProxyFactory.java

package com.autohome.turbo.rpc.proxy.jdk;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.proxy.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyFactory extends AbstractProxyFactory
{

	public JdkProxyFactory()
	{
	}

	public Object getProxy(Invoker invoker, Class interfaces[])
	{
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
	}

	public Invoker getInvoker(Object proxy, Class type, URL url)
	{
		return new AbstractProxyInvoker(proxy, type, url) {

			final JdkProxyFactory this$0;

			protected Object doInvoke(Object proxy, String methodName, Class parameterTypes[], Object arguments[])
				throws Throwable
			{
				Method method = proxy.getClass().getMethod(methodName, parameterTypes);
				return method.invoke(proxy, arguments);
			}

			
			{
				this$0 = JdkProxyFactory.this;
				super(x0, x1, x2);
			}
		};
	}
}
