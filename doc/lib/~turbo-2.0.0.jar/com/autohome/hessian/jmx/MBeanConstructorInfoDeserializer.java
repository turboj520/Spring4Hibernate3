// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MBeanConstructorInfoDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanConstructorInfo;

public class MBeanConstructorInfoDeserializer extends AbstractDeserializer
{

	public MBeanConstructorInfoDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/MBeanConstructorInfo;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String name;
		String description;
		javax.management.MBeanParameterInfo sig[];
		name = null;
		description = null;
		sig = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("name".equals(key))
				name = in.readString();
			else
			if ("description".equals(key))
				description = in.readString();
			else
			if ("signature".equals(key))
				sig = (javax.management.MBeanParameterInfo[])(javax.management.MBeanParameterInfo[])in.readObject([Ljavax/management/MBeanParameterInfo;);
			else
				in.readObject();
		}
		in.readMapEnd();
		MBeanConstructorInfo info = new MBeanConstructorInfo(name, description, sig);
		return info;
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
