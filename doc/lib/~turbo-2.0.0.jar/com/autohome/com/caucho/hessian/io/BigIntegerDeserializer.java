// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BigIntegerDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.math.BigInteger;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			JavaDeserializer

public class BigIntegerDeserializer extends JavaDeserializer
{

	public BigIntegerDeserializer()
	{
		super(java/math/BigInteger);
	}

	protected Object instantiate()
		throws Exception
	{
		return new BigInteger("0");
	}
}
