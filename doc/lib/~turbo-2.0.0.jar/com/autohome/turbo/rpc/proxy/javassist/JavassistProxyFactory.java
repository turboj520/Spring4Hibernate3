// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavassistProxyFactory.java

package com.autohome.turbo.rpc.proxy.javassist;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.bytecode.Proxy;
import com.autohome.turbo.common.bytecode.Wrapper;
import com.autohome.turbo.rpc.Invoker;
import com.autohome.turbo.rpc.proxy.*;

public class JavassistProxyFactory extends AbstractProxyFactory
{

	public JavassistProxyFactory()
	{
	}

	public Object getProxy(Invoker invoker, Class interfaces[])
	{
		return Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
	}

	public Invoker getInvoker(final Object proxy, Class type, URL url)
	{
		Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') >= 0 ? type : proxy.getClass());
		return new AbstractProxyInvoker(type, url, wrapper) {

			final Wrapper val$wrapper;
			final JavassistProxyFactory this$0;

			protected Object doInvoke(Object proxy, String methodName, Class parameterTypes[], Object arguments[])
				throws Throwable
			{
				return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
			}

			
			{
				this$0 = JavassistProxyFactory.this;
				wrapper = wrapper1;
				super(x0, x1, x2);
			}
		};
	}
}
