// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractValidation.java

package com.autohome.turbo.validation.support;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.validation.Validation;
import com.autohome.turbo.validation.Validator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractValidation
	implements Validation
{

	private final ConcurrentMap validators = new ConcurrentHashMap();

	public AbstractValidation()
	{
	}

	public Validator getValidator(URL url)
	{
		String key = url.toFullString();
		Validator validator = (Validator)validators.get(key);
		if (validator == null)
		{
			validators.put(key, createValidator(url));
			validator = (Validator)validators.get(key);
		}
		return validator;
	}

	protected abstract Validator createValidator(URL url);
}
