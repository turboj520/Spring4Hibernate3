// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JValidation.java

package com.autohome.turbo.validation.support.jvalidation;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.validation.Validator;
import com.autohome.turbo.validation.support.AbstractValidation;

// Referenced classes of package com.autohome.turbo.validation.support.jvalidation:
//			JValidator

public class JValidation extends AbstractValidation
{

	public JValidation()
	{
	}

	protected Validator createValidator(URL url)
	{
		return new JValidator(url);
	}
}
