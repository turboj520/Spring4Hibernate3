// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MBeanInfoDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanInfo;

public class MBeanInfoDeserializer extends AbstractDeserializer
{

	public MBeanInfoDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/MBeanInfo;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String className;
		String description;
		javax.management.MBeanAttributeInfo attributes[];
		javax.management.MBeanConstructorInfo constructors[];
		javax.management.MBeanOperationInfo operations[];
		javax.management.MBeanNotificationInfo notifications[];
		className = null;
		description = null;
		attributes = null;
		constructors = null;
		operations = null;
		notifications = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("className".equals(key))
				className = in.readString();
			else
			if ("description".equals(key))
				description = in.readString();
			else
			if ("attributes".equals(key))
				attributes = (javax.management.MBeanAttributeInfo[])(javax.management.MBeanAttributeInfo[])in.readObject([Ljavax/management/MBeanAttributeInfo;);
			else
				in.readObject();
		}
		in.readMapEnd();
		MBeanInfo info = new MBeanInfo(className, description, attributes, constructors, operations, notifications);
		return info;
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
