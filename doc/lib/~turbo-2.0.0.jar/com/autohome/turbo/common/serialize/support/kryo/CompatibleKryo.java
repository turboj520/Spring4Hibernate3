// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CompatibleKryo.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			ReflectionUtils

public class CompatibleKryo extends Kryo
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/serialize/support/kryo/CompatibleKryo);

	public CompatibleKryo()
	{
	}

	public Serializer getDefaultSerializer(Class type)
	{
		if (type == null)
			throw new IllegalArgumentException("type cannot be null.");
		if (!type.isArray() && !ReflectionUtils.checkZeroArgConstructor(type))
		{
			if (logger.isWarnEnabled())
				logger.warn((new StringBuilder()).append(type).append(" has no zero-arg constructor and this will affect the serialization performance").toString());
			return new JavaSerializer();
		} else
		{
			return super.getDefaultSerializer(type);
		}
	}

}
