// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JMXSerializerFactory.java

package com.autohome.hessian.jmx;

import com.autohome.hessian.io.*;
import javax.management.*;

// Referenced classes of package com.autohome.hessian.jmx:
//			ObjectInstanceDeserializer, MBeanAttributeInfoDeserializer, MBeanConstructorInfoDeserializer, MBeanOperationInfoDeserializer, 
//			MBeanParameterInfoDeserializer, MBeanNotificationInfoDeserializer

public class JMXSerializerFactory extends AbstractSerializerFactory
{

	public JMXSerializerFactory()
	{
	}

	public Serializer getSerializer(Class cl)
		throws HessianProtocolException
	{
		if (javax/management/ObjectName.equals(cl))
			return new StringValueSerializer();
		else
			return null;
	}

	public Deserializer getDeserializer(Class cl)
		throws HessianProtocolException
	{
		if (javax/management/ObjectName.equals(cl))
			return new StringValueDeserializer(cl);
		if (javax/management/ObjectInstance.equals(cl))
			return new ObjectInstanceDeserializer();
		if (javax/management/MBeanAttributeInfo.isAssignableFrom(cl))
			return new MBeanAttributeInfoDeserializer();
		if (javax/management/MBeanConstructorInfo.isAssignableFrom(cl))
			return new MBeanConstructorInfoDeserializer();
		if (javax/management/MBeanOperationInfo.isAssignableFrom(cl))
			return new MBeanOperationInfoDeserializer();
		if (javax/management/MBeanParameterInfo.isAssignableFrom(cl))
			return new MBeanParameterInfoDeserializer();
		if (javax/management/MBeanNotificationInfo.isAssignableFrom(cl))
			return new MBeanNotificationInfoDeserializer();
		else
			return null;
	}
}
