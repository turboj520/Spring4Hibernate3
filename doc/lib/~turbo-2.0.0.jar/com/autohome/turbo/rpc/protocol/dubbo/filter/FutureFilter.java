// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FutureFilter.java

package com.autohome.turbo.rpc.protocol.dubbo.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.exchange.ResponseCallback;
import com.autohome.turbo.remoting.exchange.ResponseFuture;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.rpc.protocol.dubbo.FutureAdapter;
import com.autohome.turbo.rpc.support.RpcUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class FutureFilter
	implements Filter
{

	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/rpc/protocol/dubbo/filter/FutureFilter);

	public FutureFilter()
	{
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);
		fireInvokeCallback(invoker, invocation);
		Result result = invoker.invoke(invocation);
		if (isAsync)
			asyncCallback(invoker, invocation);
		else
			syncCallback(invoker, invocation, result);
		return result;
	}

	private void syncCallback(Invoker invoker, Invocation invocation, Result result)
	{
		if (result.hasException())
			fireThrowCallback(invoker, invocation, result.getException());
		else
			fireReturnCallback(invoker, invocation, result.getValue());
	}

	private void asyncCallback(final Invoker invoker, final Invocation invocation)
	{
		Future f = RpcContext.getContext().getFuture();
		if (f instanceof FutureAdapter)
		{
			ResponseFuture future = ((FutureAdapter)f).getFuture();
			future.setCallback(new ResponseCallback() {

				final Invoker val$invoker;
				final Invocation val$invocation;
				final FutureFilter this$0;

				public void done(Object rpcResult)
				{
					if (rpcResult == null)
					{
						FutureFilter.logger.error(new IllegalStateException((new StringBuilder()).append("invalid result value : null, expected ").append(com/autohome/turbo/rpc/Result.getName()).toString()));
						return;
					}
					if (!(rpcResult instanceof Result))
					{
						FutureFilter.logger.error(new IllegalStateException((new StringBuilder()).append("invalid result type :").append(rpcResult.getClass()).append(", expected ").append(com/autohome/turbo/rpc/Result.getName()).toString()));
						return;
					}
					Result result = (Result)rpcResult;
					if (result.hasException())
						fireThrowCallback(invoker, invocation, result.getException());
					else
						fireReturnCallback(invoker, invocation, result.getValue());
				}

				public void caught(Throwable exception)
				{
					fireThrowCallback(invoker, invocation, exception);
				}

			
			{
				this$0 = FutureFilter.this;
				invoker = invoker1;
				invocation = invocation1;
				super();
			}
			});
		}
	}

	private void fireInvokeCallback(Invoker invoker, Invocation invocation)
	{
		Method onInvokeMethod = (Method)StaticContext.getSystemContext().get(StaticContext.getKey(invoker.getUrl(), invocation.getMethodName(), "oninvoke.method"));
		Object onInvokeInst = StaticContext.getSystemContext().get(StaticContext.getKey(invoker.getUrl(), invocation.getMethodName(), "oninvoke.instance"));
		if (onInvokeMethod == null && onInvokeInst == null)
			return;
		if (onInvokeMethod == null || onInvokeInst == null)
			throw new IllegalStateException((new StringBuilder()).append("service:").append(invoker.getUrl().getServiceKey()).append(" has a onreturn callback config , but no such ").append(onInvokeMethod != null ? "instance" : "method").append(" found. url:").append(invoker.getUrl()).toString());
		if (onInvokeMethod != null && !onInvokeMethod.isAccessible())
			onInvokeMethod.setAccessible(true);
		Object params[] = invocation.getArguments();
		try
		{
			onInvokeMethod.invoke(onInvokeInst, params);
		}
		catch (InvocationTargetException e)
		{
			fireThrowCallback(invoker, invocation, e.getTargetException());
		}
		catch (Throwable e)
		{
			fireThrowCallback(invoker, invocation, e);
		}
	}

	private void fireReturnCallback(Invoker invoker, Invocation invocation, Object result)
	{
		Method onReturnMethod = (Method)StaticContext.getSystemContext().get(StaticContext.getKey(invoker.getUrl(), invocation.getMethodName(), "onreturn.method"));
		Object onReturnInst = StaticContext.getSystemContext().get(StaticContext.getKey(invoker.getUrl(), invocation.getMethodName(), "onreturn.instance"));
		if (onReturnMethod == null && onReturnInst == null)
			return;
		if (onReturnMethod == null || onReturnInst == null)
			throw new IllegalStateException((new StringBuilder()).append("service:").append(invoker.getUrl().getServiceKey()).append(" has a onreturn callback config , but no such ").append(onReturnMethod != null ? "instance" : "method").append(" found. url:").append(invoker.getUrl()).toString());
		if (onReturnMethod != null && !onReturnMethod.isAccessible())
			onReturnMethod.setAccessible(true);
		Object args[] = invocation.getArguments();
		Class rParaTypes[] = onReturnMethod.getParameterTypes();
		Object params[];
		if (rParaTypes.length > 1)
		{
			if (rParaTypes.length == 2 && rParaTypes[1].isAssignableFrom([Ljava/lang/Object;))
			{
				params = new Object[2];
				params[0] = result;
				params[1] = ((Object) (args));
			} else
			{
				params = new Object[args.length + 1];
				params[0] = result;
				System.arraycopy(((Object) (args)), 0, ((Object) (params)), 1, args.length);
			}
		} else
		{
			params = (new Object[] {
				result
			});
		}
		try
		{
			onReturnMethod.invoke(onReturnInst, params);
		}
		catch (InvocationTargetException e)
		{
			fireThrowCallback(invoker, invocation, e.getTargetException());
		}
		catch (Throwable e)
		{
			fireThrowCallback(invoker, invocation, e);
		}
	}

	private void fireThrowCallback(Invoker invoker, Invocation invocation, Throwable exception)
	{
		Method onthrowMethod = (Method)StaticContext.getSystemContext().get(StaticContext.getKey(invoker.getUrl(), invocation.getMethodName(), "onthrow.method"));
		Object onthrowInst = StaticContext.getSystemContext().get(StaticContext.getKey(invoker.getUrl(), invocation.getMethodName(), "onthrow.instance"));
		if (onthrowMethod == null && onthrowInst == null)
			return;
		if (onthrowMethod == null || onthrowInst == null)
			throw new IllegalStateException((new StringBuilder()).append("service:").append(invoker.getUrl().getServiceKey()).append(" has a onthrow callback config , but no such ").append(onthrowMethod != null ? "instance" : "method").append(" found. url:").append(invoker.getUrl()).toString());
		if (onthrowMethod != null && !onthrowMethod.isAccessible())
			onthrowMethod.setAccessible(true);
		Class rParaTypes[] = onthrowMethod.getParameterTypes();
		if (rParaTypes[0].isAssignableFrom(exception.getClass()))
			try
			{
				Object args[] = invocation.getArguments();
				Object params[];
				if (rParaTypes.length > 1)
				{
					if (rParaTypes.length == 2 && rParaTypes[1].isAssignableFrom([Ljava/lang/Object;))
					{
						params = new Object[2];
						params[0] = exception;
						params[1] = ((Object) (args));
					} else
					{
						params = new Object[args.length + 1];
						params[0] = exception;
						System.arraycopy(((Object) (args)), 0, ((Object) (params)), 1, args.length);
					}
				} else
				{
					params = (new Object[] {
						exception
					});
				}
				onthrowMethod.invoke(onthrowInst, params);
			}
			catch (Throwable e)
			{
				logger.error((new StringBuilder()).append(invocation.getMethodName()).append(".call back method invoke error . callback method :").append(onthrowMethod).append(", url:").append(invoker.getUrl()).toString(), e);
			}
		else
			logger.error((new StringBuilder()).append(invocation.getMethodName()).append(".call back method invoke error . callback method :").append(onthrowMethod).append(", url:").append(invoker.getUrl()).toString(), exception);
	}



}
