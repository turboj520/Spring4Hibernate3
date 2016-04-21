// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BeanSerializerFactory.java

package com.autohome.com.caucho.hessian.io;


// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			SerializerFactory, BeanSerializer, BeanDeserializer, Serializer, 
//			Deserializer

public class BeanSerializerFactory extends SerializerFactory
{

	public BeanSerializerFactory()
	{
	}

	protected Serializer getDefaultSerializer(Class cl)
	{
		return new BeanSerializer(cl, getClassLoader());
	}

	protected Deserializer getDefaultDeserializer(Class cl)
	{
		return new BeanDeserializer(cl);
	}
}
