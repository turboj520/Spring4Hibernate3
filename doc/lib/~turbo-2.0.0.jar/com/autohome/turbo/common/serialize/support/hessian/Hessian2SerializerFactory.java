// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2SerializerFactory.java

package com.autohome.turbo.common.serialize.support.hessian;

import com.autohome.com.caucho.hessian.io.SerializerFactory;

public class Hessian2SerializerFactory extends SerializerFactory
{

	public static final SerializerFactory SERIALIZER_FACTORY = new Hessian2SerializerFactory();

	private Hessian2SerializerFactory()
	{
	}

	public ClassLoader getClassLoader()
	{
		return Thread.currentThread().getContextClassLoader();
	}

}
