// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MockClusterInvoker.java

package com.autohome.turbo.rpc.cluster.support.wrapper;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.cluster.Directory;
import com.autohome.turbo.rpc.support.MockInvoker;
import java.util.List;

public class MockClusterInvoker
	implements Invoker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/cluster/support/wrapper/MockClusterInvoker);
	private final Directory directory;
	private final Invoker invoker;

	public MockClusterInvoker(Directory directory, Invoker invoker)
	{
		this.directory = directory;
		this.invoker = invoker;
	}

	public URL getUrl()
	{
		return directory.getUrl();
	}

	public boolean isAvailable()
	{
		return directory.isAvailable();
	}

	public void destroy()
	{
		invoker.destroy();
	}

	public Class getInterface()
	{
		return directory.getInterface();
	}

	public Result invoke(Invocation invocation)
		throws RpcException
	{
		Result result = null;
		String value = directory.getUrl().getMethodParameter(invocation.getMethodName(), "mock", Boolean.FALSE.toString()).trim();
		if (value.length() == 0 || value.equalsIgnoreCase("false"))
			result = invoker.invoke(invocation);
		else
		if (value.startsWith("force"))
		{
			if (logger.isWarnEnabled())
				logger.info((new StringBuilder()).append("force-mock: ").append(invocation.getMethodName()).append(" force-mock enabled , url : ").append(directory.getUrl()).toString());
			result = doMockInvoke(invocation, null);
		} else
		{
			try
			{
				result = invoker.invoke(invocation);
			}
			catch (RpcException e)
			{
				if (e.isBiz())
					throw e;
				if (logger.isWarnEnabled())
					logger.info((new StringBuilder()).append("fail-mock: ").append(invocation.getMethodName()).append(" fail-mock enabled , url : ").append(directory.getUrl()).toString(), e);
				result = doMockInvoke(invocation, e);
			}
		}
		return result;
	}

	private Result doMockInvoke(Invocation invocation, RpcException e)
	{
		Result result = null;
		List mockInvokers = selectMockInvoker(invocation);
		Invoker minvoker;
		if (mockInvokers == null || mockInvokers.size() == 0)
			minvoker = new MockInvoker(directory.getUrl());
		else
			minvoker = (Invoker)mockInvokers.get(0);
		try
		{
			result = minvoker.invoke(invocation);
		}
		catch (RpcException me)
		{
			if (me.isBiz())
				result = new RpcResult(me.getCause());
			else
				throw new RpcException(me.getCode(), getMockExceptionMessage(e, me), me.getCause());
		}
		catch (Throwable me)
		{
			throw new RpcException(getMockExceptionMessage(e, me), me.getCause());
		}
		return result;
	}

	private String getMockExceptionMessage(Throwable t, Throwable mt)
	{
		String msg = (new StringBuilder()).append("mock error : ").append(mt.getMessage()).toString();
		if (t != null)
			msg = (new StringBuilder()).append(msg).append(", invoke error is :").append(StringUtils.toString(t)).toString();
		return msg;
	}

	private List selectMockInvoker(Invocation invocation)
	{
		if (invocation instanceof RpcInvocation)
		{
			((RpcInvocation)invocation).setAttachment("invocation.need.mock", Boolean.TRUE.toString());
			List invokers = directory.list(invocation);
			return invokers;
		} else
		{
			return null;
		}
	}

	public String toString()
	{
		return (new StringBuilder()).append("invoker :").append(invoker).append(",directory: ").append(directory).toString();
	}

}
