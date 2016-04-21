// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MBeanNotificationInfoDeserializer.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.AbstractDeserializer;
import com.autohome.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanNotificationInfo;

public class MBeanNotificationInfoDeserializer extends AbstractDeserializer
{

	public MBeanNotificationInfoDeserializer()
	{
	}

	public Class getType()
	{
		return javax/management/MBeanNotificationInfo;
	}

	public Object readMap(AbstractHessianInput in)
		throws IOException
	{
		String name;
		String description;
		String types[];
		name = null;
		description = null;
		types = null;
		while (!in.isEnd()) 
		{
			String key = in.readString();
			if ("name".equals(key))
				name = in.readString();
			else
			if ("description".equals(key))
				description = in.readString();
			else
			if ("types".equals(key))
				types = (String[])(String[])in.readObject([Ljava/lang/String;);
			else
				in.readObject();
		}
		in.readMapEnd();
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return info;
		Exception e;
		e;
		throw new IOException(String.valueOf(e));
	}
}
