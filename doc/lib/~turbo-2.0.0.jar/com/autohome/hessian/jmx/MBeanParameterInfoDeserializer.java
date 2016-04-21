// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MBeanParameterInfoDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanParameterInfo;

public class MBeanParameterInfoDeserializer extends AbstractDeserializer
{

	public MBeanParameterInfoDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/MBeanParameterInfo;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String name;
		String type;
		String description;
		name = null;
		type = null;
		description = null;
		boolean isRead = false;
		boolean isWrite = false;
		boolean isIs = false;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("name".equals(key))
				name = in.readString();
			else
			if ("type".equals(key))
				type = in.readString();
			else
			if ("description".equals(key))
				description = in.readString();
			else
				in.readObject();
		}
		in.readMapEnd();
		MBeanParameterInfo info = new MBeanParameterInfo(name, type, description);
		return info;
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
