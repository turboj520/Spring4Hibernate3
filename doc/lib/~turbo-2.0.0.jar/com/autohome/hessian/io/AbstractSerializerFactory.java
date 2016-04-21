// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractSerializerFactory.java

package com.autohome.hessian.io;


// Referenced classes of package com.autohome.hessian.io:
//			HessianProtocolException, Serializer, Deserializer

public abstract class AbstractSerializerFactory
{

	public AbstractSerializerFactory()
	{
	}

	public abstract Serializer getSerializer(Class class1)
		throws HessianProtocolException;

	public abstract Deserializer getDeserializer(Class class1)
		throws HessianProtocolException;
}
