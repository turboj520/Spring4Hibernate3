// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ObjectNameDeserializer.java

package com.autohome.hessian.io;

import com.autohome.hessian.HessianException;
import javax.management.ObjectName;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractStringValueDeserializer

public class ObjectNameDeserializer extends AbstractStringValueDeserializer
{

	public ObjectNameDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/ObjectName;
	}

	protected Object create(String value)
	{
		return new ObjectName(value);
		RuntimeException e;
		e;
		throw e;
		e;
		throw new HessianException(e);
	}
}
