// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ValidationFilter.java

package com.autohome.turbo.validation.filter;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.rpc.*;
import com.autohome.turbo.validation.Validation;
import com.autohome.turbo.validation.Validator;

public class ValidationFilter
	implements Filter
{

	private Validation validation;

	public ValidationFilter()
	{
	}

	public void setValidation(Validation validation)
	{
		this.validation = validation;
	}

	public Result invoke(Invoker invoker, Invocation invocation)
		throws RpcException
	{
		if (validation != null && !invocation.getMethodName().startsWith("$") && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), "validation")))
			try
			{
				Validator validator = validation.getValidator(invoker.getUrl());
				if (validator != null)
					validator.validate(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
			}
			catch (RpcException e)
			{
				throw e;
			}
			catch (Throwable t)
			{
				throw new RpcException(t.getMessage(), t);
			}
		return invoker.invoke(invocation);
	}
}
