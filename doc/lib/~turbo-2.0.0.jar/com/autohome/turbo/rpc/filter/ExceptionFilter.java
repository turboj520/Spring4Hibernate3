// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExceptionFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.service.GenericService;
import java.lang.reflect.Method;

public class ExceptionFilter
	implements Filter
{

	private final Logger logger;

	public ExceptionFilter()
	{
		this(LoggerFactory.getLogger(com/autohome/turbo/rpc/filter/ExceptionFilter));
	}

	public ExceptionFilter(Logger logger)
	{
		this.logger = logger;
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		Result result;
		result = invoker.invoke(invocation);
		if (!result.hasException() || com/autohome/turbo/rpc/service/GenericService == invoker.getInterface())
			break MISSING_BLOCK_LABEL_438;
		Throwable exception = result.getException();
		if (!(exception instanceof RuntimeException) && (exception instanceof Exception))
			return result;
		Class arr$[];
		int len$;
		int i$;
		Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
		Class exceptionClassses[] = method.getExceptionTypes();
		arr$ = exceptionClassses;
		len$ = arr$.length;
		i$ = 0;
_L1:
		Class exceptionClass;
		if (i$ >= len$)
			break MISSING_BLOCK_LABEL_139;
		exceptionClass = arr$[i$];
		if (exception.getClass().equals(exceptionClass))
			return result;
		try
		{
			i$++;
		}
		catch (NoSuchMethodException e)
		{
			return result;
		}
		  goto _L1
		String serviceFile;
		String exceptionFile;
		logger.error((new StringBuilder()).append("Got unchecked and undeclared exception which called by ").append(RpcContext.getContext().getRemoteHost()).append(". service: ").append(invoker.getInterface().getName()).append(", method: ").append(invocation.getMethodName()).append(", exception: ").append(exception.getClass().getName()).append(": ").append(exception.getMessage()).toString(), exception);
		serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
		exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
		if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile))
			return result;
		String className = exception.getClass().getName();
		if (className.startsWith("java.") || className.startsWith("javax."))
			return result;
		if (exception instanceof RpcException)
			return result;
		return new RpcResult(new RuntimeException(StringUtils.toString(exception)));
		Throwable e;
		e;
		logger.warn((new StringBuilder()).append("Fail to ExceptionFilter when called by ").append(RpcContext.getContext().getRemoteHost()).append(". service: ").append(invoker.getInterface().getName()).append(", method: ").append(invocation.getMethodName()).append(", exception: ").append(e.getClass().getName()).append(": ").append(e.getMessage()).toString(), e);
		return result;
		return result;
		RuntimeException e;
		e;
		logger.error((new StringBuilder()).append("Got unchecked and undeclared exception which called by ").append(RpcContext.getContext().getRemoteHost()).append(". service: ").append(invoker.getInterface().getName()).append(", method: ").append(invocation.getMethodName()).append(", exception: ").append(e.getClass().getName()).append(": ").append(e.getMessage()).toString(), e);
		throw e;
	}
}
