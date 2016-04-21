// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExtSerializerFactory.java

package com.autohome.com.caucho.hessian.io;

import java.util.HashMap;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractSerializerFactory, Serializer, Deserializer, HessianProtocolException

public class ExtSerializerFactory extends AbstractSerializerFactory
{

	private HashMap _serializerMap;
	private HashMap _deserializerMap;

	public ExtSerializerFactory()
	{
		_serializerMap = new HashMap();
		_deserializerMap = new HashMap();
	}

	public void addSerializer(Class cl, Serializer serializer)
	{
		_serializerMap.put(cl, serializer);
	}

	public void addDeserializer(Class cl, Deserializer deserializer)
	{
		_deserializerMap.put(cl, deserializer);
	}

	public Serializer getSerializer(Class cl)
		throws HessianProtocolException
	{
		return (Serializer)_serializerMap.get(cl);
	}

	public Deserializer getDeserializer(Class cl)
		throws HessianProtocolException
	{
		return (Deserializer)_deserializerMap.get(cl);
	}
}
