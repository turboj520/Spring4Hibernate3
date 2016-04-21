// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MockInvoker.java

package com.autohome.turbo.rpc.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.utils.*;
import com.autohome.turbo.rpc.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.autohome.turbo.rpc.support:
//			RpcUtils

public final class MockInvoker
	implements Invoker
{

	private static final ProxyFactory proxyFactory = (ProxyFactory)ExtensionLoader.getExtensionLoader(com/autohome/turbo/rpc/ProxyFactory).getAdaptiveExtension();
	private static final Map mocks = new ConcurrentHashMap();
	private static final Map throwables = new ConcurrentHashMap();
	private final URL url;

	public MockInvoker(URL url)
	{
		this.url = url;
	}

	public Result invoke(Invocation invocation)
		throws RpcException
	{
		String mock;
		mock = getUrl().getParameter((new StringBuilder()).append(invocation.getMethodName()).append(".").append("mock").toString());
		if (invocation instanceof RpcInvocation)
			((RpcInvocation)invocation).setInvoker(this);
		if (StringUtils.isBlank(mock))
			mock = getUrl().getParameter("mock");
		if (StringUtils.isBlank(mock))
			throw new RpcException(new IllegalAccessException((new StringBuilder()).append("mock can not be null. url :").append(url).toString()));
		mock = normallizeMock(URL.decode(mock));
		if ("return ".trim().equalsIgnoreCase(mock.trim()))
		{
			RpcResult result = new RpcResult();
			result.setValue(null);
			return result;
		}
		if (!mock.startsWith("return "))
			break MISSING_BLOCK_LABEL_260;
		mock = mock.substring("return ".length()).trim();
		mock = mock.replace('`', '"');
		Object value;
		Type returnTypes[] = RpcUtils.getReturnTypes(invocation);
		value = parseMockValue(mock, returnTypes);
		return new RpcResult(value);
		Exception ew;
		ew;
		throw new RpcException((new StringBuilder()).append("mock return invoke error. method :").append(invocation.getMethodName()).append(", mock:").append(mock).append(", url: ").append(url).toString(), ew);
		if (mock.startsWith("throw"))
		{
			mock = mock.substring("throw".length()).trim();
			mock = mock.replace('`', '"');
			if (StringUtils.isBlank(mock))
			{
				throw new RpcException(" mocked exception for Service degradation. ");
			} else
			{
				Throwable t = getThrowable(mock);
				throw new RpcException(3, t);
			}
		}
		Invoker invoker = getInvoker(mock);
		return invoker.invoke(invocation);
		Throwable t;
		t;
		throw new RpcException((new StringBuilder()).append("Failed to create mock implemention class ").append(mock).toString(), t);
	}

	private Throwable getThrowable(String throwstr)
	{
		Throwable throwable = (Throwable)throwables.get(throwstr);
		if (throwable != null)
			return throwable;
		Throwable t = null;
		try
		{
			Class bizException = ReflectUtils.forName(throwstr);
			Constructor constructor = ReflectUtils.findConstructor(bizException, java/lang/String);
			t = (Throwable)constructor.newInstance(new Object[] {
				" mocked exception for Service degradation. "
			});
			if (throwables.size() < 1000)
				throwables.put(throwstr, t);
		}
		catch (Exception e)
		{
			throw new RpcException((new StringBuilder()).append("mock throw error :").append(throwstr).append(" argument error.").toString(), e);
		}
		return t;
	}

	private Invoker getInvoker(String mockService)
	{
		Invoker invoker;
		Class serviceType;
		Class mockClass;
		invoker = (Invoker)mocks.get(mockService);
		if (invoker != null)
			return invoker;
		serviceType = ReflectUtils.forName(url.getServiceInterface());
		if (ConfigUtils.isDefault(mockService))
			mockService = (new StringBuilder()).append(serviceType.getName()).append("Mock").toString();
		mockClass = ReflectUtils.forName(mockService);
		if (!serviceType.isAssignableFrom(mockClass))
			throw new IllegalArgumentException((new StringBuilder()).append("The mock implemention class ").append(mockClass.getName()).append(" not implement interface ").append(serviceType.getName()).toString());
		if (!serviceType.isAssignableFrom(mockClass))
			throw new IllegalArgumentException((new StringBuilder()).append("The mock implemention class ").append(mockClass.getName()).append(" not implement interface ").append(serviceType.getName()).toString());
		Object mockObject = mockClass.newInstance();
		invoker = proxyFactory.getInvoker(mockObject, serviceType, url);
		if (mocks.size() < 10000)
			mocks.put(mockService, invoker);
		return invoker;
		InstantiationException e;
		e;
		throw new IllegalStateException((new StringBuilder()).append("No such empty constructor \"public ").append(mockClass.getSimpleName()).append("()\" in mock implemention class ").append(mockClass.getName()).toString(), e);
		e;
		throw new IllegalStateException(e);
	}

	private String normallizeMock(String mock)
	{
		if (mock == null || mock.trim().length() == 0)
			return mock;
		if (ConfigUtils.isDefault(mock) || "fail".equalsIgnoreCase(mock.trim()) || "force".equalsIgnoreCase(mock.trim()))
			mock = (new StringBuilder()).append(url.getServiceInterface()).append("Mock").toString();
		if (mock.startsWith("fail:"))
			mock = mock.substring("fail:".length()).trim();
		else
		if (mock.startsWith("force:"))
			mock = mock.substring("force:".length()).trim();
		return mock;
	}

	public static Object parseMockValue(String mock)
		throws Exception
	{
		return parseMockValue(mock, null);
	}

	public static Object parseMockValue(String mock, Type returnTypes[])
		throws Exception
	{
		Object value = null;
		if ("empty".equals(mock))
			value = ReflectUtils.getEmptyObject(returnTypes == null || returnTypes.length <= 0 ? null : (Class)returnTypes[0]);
		else
		if ("null".equals(mock))
			value = null;
		else
		if ("true".equals(mock))
			value = Boolean.valueOf(true);
		else
		if ("false".equals(mock))
			value = Boolean.valueOf(false);
		else
		if (mock.length() >= 2 && (mock.startsWith("\"") && mock.endsWith("\"") || mock.startsWith("'") && mock.endsWith("'")))
			value = mock.subSequence(1, mock.length() - 1);
		else
		if (returnTypes != null && returnTypes.length > 0 && returnTypes[0] == java/lang/String)
			value = mock;
		else
		if (StringUtils.isNumeric(mock))
			value = JSON.parse(mock);
		else
		if (mock.startsWith("{"))
			value = JSON.parse(mock, java/util/Map);
		else
		if (mock.startsWith("["))
			value = JSON.parse(mock, java/util/List);
		else
			value = mock;
		if (returnTypes != null && returnTypes.length > 0)
			value = PojoUtils.realize(value, (Class)returnTypes[0], returnTypes.length <= 1 ? null : returnTypes[1]);
		return value;
	}

	public URL getUrl()
	{
		return url;
	}

	public boolean isAvailable()
	{
		return true;
	}

	public void destroy()
	{
	}

	public Class getInterface()
	{
		return null;
	}

}
