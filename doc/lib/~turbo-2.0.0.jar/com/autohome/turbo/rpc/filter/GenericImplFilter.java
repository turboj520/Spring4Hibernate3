// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   GenericImplFilter.java

package com.autohome.turbo.rpc.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.beanutil.*;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.PojoUtils;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.service.GenericException;
import com.autohome.turbo.rpc.support.ProtocolUtils;
import java.lang.reflect.*;

public class GenericImplFilter
	implements Filter
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/filter/GenericImplFilter);
	private static final Class GENERIC_PARAMETER_TYPES[] = {
		java/lang/String, [Ljava/lang/String;, [Ljava/lang/Object;
	};

	public GenericImplFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		String generic;
		String methodName;
		Class parameterTypes[];
		Result result;
		Object value;
		generic = invoker.getUrl().getParameter("generic");
		if (!ProtocolUtils.isGeneric(generic) || "$invoke".equals(invocation.getMethodName()) || !(invocation instanceof RpcInvocation))
			break MISSING_BLOCK_LABEL_656;
		RpcInvocation invocation2 = (RpcInvocation)invocation;
		methodName = invocation2.getMethodName();
		parameterTypes = invocation2.getParameterTypes();
		Object arguments[] = invocation2.getArguments();
		String types[] = new String[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++)
			types[i] = ReflectUtils.getName(parameterTypes[i]);

		Object args[];
		if (ProtocolUtils.isBeanGenericSerialization(generic))
		{
			args = new Object[arguments.length];
			for (int i = 0; i < arguments.length; i++)
				args[i] = JavaBeanSerializeUtil.serialize(arguments[i], JavaBeanAccessor.METHOD);

		} else
		{
			args = PojoUtils.generalize(arguments);
		}
		invocation2.setMethodName("$invoke");
		invocation2.setParameterTypes(GENERIC_PARAMETER_TYPES);
		invocation2.setArguments(new Object[] {
			methodName, types, args
		});
		result = invoker.invoke(invocation2);
		if (result.hasException())
			break MISSING_BLOCK_LABEL_392;
		value = result.getValue();
		Method method;
		method = invoker.getInterface().getMethod(methodName, parameterTypes);
		if (!ProtocolUtils.isBeanGenericSerialization(generic))
			break MISSING_BLOCK_LABEL_352;
		if (value == null)
			return new RpcResult(value);
		if (value instanceof JavaBeanDescriptor)
			return new RpcResult(JavaBeanSerializeUtil.deserialize((JavaBeanDescriptor)value));
		throw new RpcException((new StringBuilder(64)).append("The type of result value is ").append(value.getClass().getName()).append(" other than ").append(com/autohome/turbo/common/beanutil/JavaBeanDescriptor.getName()).append(", and the result is ").append(value).toString());
		return new RpcResult(PojoUtils.realize(value, method.getReturnType(), method.getGenericReturnType()));
		NoSuchMethodException e;
		e;
		throw new RpcException(e.getMessage(), e);
		if (result.getException() instanceof GenericException)
		{
			GenericException exception = (GenericException)result.getException();
			try
			{
				String className = exception.getExceptionClass();
				Class clazz = ReflectUtils.forName(className);
				Throwable targetException = null;
				Throwable lastException = null;
				try
				{
					targetException = (Throwable)clazz.newInstance();
				}
				catch (Throwable e)
				{
					lastException = e;
					Constructor arr$[] = clazz.getConstructors();
					int len$ = arr$.length;
					int i$ = 0;
					do
					{
						if (i$ >= len$)
							break;
						Constructor constructor = arr$[i$];
						try
						{
							targetException = (Throwable)constructor.newInstance(new Object[constructor.getParameterTypes().length]);
							break;
						}
						catch (Throwable e1)
						{
							lastException = e1;
							i$++;
						}
					} while (true);
				}
				if (targetException != null)
				{
					try
					{
						Field field = java/lang/Throwable.getDeclaredField("detailMessage");
						if (!field.isAccessible())
							field.setAccessible(true);
						field.set(targetException, exception.getExceptionMessage());
					}
					catch (Throwable e)
					{
						logger.warn(e.getMessage(), e);
					}
					result = new RpcResult(targetException);
				} else
				if (lastException != null)
					throw lastException;
			}
			// Misplaced declaration of an exception variable
			catch (String className)
			{
				throw new RpcException((new StringBuilder()).append("Can not deserialize exception ").append(exception.getExceptionClass()).append(", message: ").append(exception.getExceptionMessage()).toString(), className);
			}
		}
		return result;
		if (invocation.getMethodName().equals("$invoke") && invocation.getArguments() != null && invocation.getArguments().length == 3 && ProtocolUtils.isGeneric(generic))
		{
			Object args[] = (Object[])(Object[])invocation.getArguments()[2];
			if (ProtocolUtils.isJavaGenericSerialization(generic))
			{
				Object arr$[] = args;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Object arg = arr$[i$];
					if ([B != arg.getClass())
						error([B.getName(), arg.getClass().getName());
				}

			} else
			if (ProtocolUtils.isBeanGenericSerialization(generic))
			{
				Object arr$[] = args;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					Object arg = arr$[i$];
					if (!(arg instanceof JavaBeanDescriptor))
						error(com/autohome/turbo/common/beanutil/JavaBeanDescriptor.getName(), arg.getClass().getName());
				}

			}
			((RpcInvocation)invocation).setAttachment("generic", invoker.getUrl().getParameter("generic"));
		}
		return invoker.invoke(invocation);
	}

	private void error(String expected, String actual)
		throws RpcException
	{
		throw new RpcException((new StringBuilder(32)).append("Generic serialization [").append("nativejava").append("] only support message type ").append(expected).append(" and your message type is ").append(actual).toString());
	}

}
