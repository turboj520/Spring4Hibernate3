// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MBeanAttributeInfoDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanAttributeInfo;

public class MBeanAttributeInfoDeserializer extends AbstractDeserializer
{

	public MBeanAttributeInfoDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/MBeanAttributeInfo;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String name;
		String type;
		String description;
		boolean isRead;
		boolean isWrite;
		boolean isIs;
		name = null;
		type = null;
		description = null;
		isRead = false;
		isWrite = false;
		isIs = false;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("name".equals(key))
				name = in.readString();
			else
			if ("attributeType".equals(key))
				type = in.readString();
			else
			if ("description".equals(key))
				description = in.readString();
			else
			if ("isRead".equals(key))
				isRead = in.readBoolean();
			else
			if ("isWrite".equals(key))
				isWrite = in.readBoolean();
			else
			if ("is".equals(key))
				isIs = in.readBoolean();
			else
				in.readObject();
		}
		in.readMapEnd();
		MBeanAttributeInfo info = new MBeanAttributeInfo(name, type, description, isRead, isWrite, isIs);
		return info;
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
