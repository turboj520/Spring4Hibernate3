// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ObjectInstanceDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class ObjectInstanceDeserializer extends AbstractDeserializer
{

	public ObjectInstanceDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/ObjectInstance;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String className;
		ObjectName objectName;
		className = null;
		objectName = null;
		String initValue = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("className".equals(key))
				className = in.readString();
			else
			if ("name".equals(key))
				objectName = (ObjectName)in.readObject(javax/management/ObjectName);
			else
				in.readObject();
		}
		in.readMapEnd();
		return new ObjectInstance(objectName, className);
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
