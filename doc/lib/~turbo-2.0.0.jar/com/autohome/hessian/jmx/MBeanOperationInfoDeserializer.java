// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MBeanOperationInfoDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanOperationInfo;

public class MBeanOperationInfoDeserializer extends AbstractDeserializer
{

	public MBeanOperationInfoDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/MBeanOperationInfo;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String name;
		String type;
		String description;
		javax.management.MBeanParameterInfo sig[];
		int impact;
		name = null;
		type = null;
		description = null;
		sig = null;
		impact = 0;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("name".equals(key))
				name = in.readString();
			else
			if ("description".equals(key))
				description = in.readString();
			else
			if ("type".equals(key))
				type = in.readString();
			else
			if ("impact".equals(key))
				impact = in.readInt();
			else
			if ("signature".equals(key))
				sig = (javax.management.MBeanParameterInfo[])(javax.management.MBeanParameterInfo[])in.readObject([Ljavax/management/MBeanParameterInfo;);
			else
				in.readObject();
		}
		in.readMapEnd();
		MBeanOperationInfo info = new MBeanOperationInfo(name, description, sig, type, impact);
		return info;
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
