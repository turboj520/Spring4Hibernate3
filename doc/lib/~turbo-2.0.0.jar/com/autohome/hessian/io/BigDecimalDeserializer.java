// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BigDecimalDeserializer.java

package com.autohome.hessian.io;

import java.math.BigDecimal;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractStringValueDeserializer

public class BigDecimalDeserializer extends AbstractStringValueDeserializer
{

	public BigDecimalDeserializer()
	{
	}

	public Class getType()
	{
		return java/math/BigDecimal;
	}

	protected Object create(String value)
	{
		return new BigDecimal(value);
	}
}
