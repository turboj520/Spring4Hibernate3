// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JacksonObjectInput.java

package com.autohome.turbo.common.serialize.support.json;

import com.autohome.turbo.common.json.Jackson;
import com.autohome.turbo.common.serialize.ObjectInput;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonObjectInput
	implements ObjectInput
{

	private static Logger logger = LoggerFactory.getLogger(com/autohome/turbo/common/serialize/support/json/JacksonObjectInput);
	private final ObjectMapper objectMapper = Jackson.getObjectMapper();
	private final Map data;
	private static final String KEY_PREFIX = "$";
	private int index;

	public JacksonObjectInput(InputStream inputstream)
		throws IOException
	{
		index = 0;
		try
		{
			data = (Map)objectMapper.readValue(inputstream, java/util/Map);
		}
		catch (IOException e)
		{
			logger.error("parse inputstream error.", e);
			throw e;
		}
	}

	public boolean readBool()
		throws IOException
	{
		return ((Boolean)readObject(java/lang/Boolean)).booleanValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public byte readByte()
		throws IOException
	{
		return ((Byte)readObject(java/lang/Byte)).byteValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public short readShort()
		throws IOException
	{
		return ((Short)readObject(java/lang/Short)).shortValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public int readInt()
		throws IOException
	{
		return ((Integer)readObject(java/lang/Integer)).intValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public long readLong()
		throws IOException
	{
		return ((Long)readObject(java/lang/Long)).longValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public float readFloat()
		throws IOException
	{
		return ((Float)readObject(java/lang/Float)).floatValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public double readDouble()
		throws IOException
	{
		return ((Double)readObject(java/lang/Double)).doubleValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public String readUTF()
		throws IOException
	{
		return (String)readObject(java/lang/String);
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public byte[] readBytes()
		throws IOException
	{
		return readUTF().getBytes();
	}

	public Object readObject()
		throws IOException, ClassNotFoundException
	{
		return readObject(java/lang/Object);
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public Object readObject(Class cls)
		throws IOException, ClassNotFoundException
	{
		String json = (String)data.get((new StringBuilder()).append("$").append(++index).toString());
		String dataType = (String)data.get((new StringBuilder()).append("$").append(index).append("t").toString());
		if (dataType != null)
		{
			Class clazz = ReflectUtils.desc2class(dataType);
			if (cls.isAssignableFrom(clazz))
				cls = clazz;
			else
				throw new IllegalArgumentException((new StringBuilder()).append("Class \"").append(clazz).append("\" is not inherited from \"").append(cls).append("\"").toString());
		}
		logger.debug("index:{}, value:{}", Integer.valueOf(index), json);
		return objectMapper.readValue(json, cls);
	}

	public Object readObject(Class cls, Type type)
		throws IOException, ClassNotFoundException
	{
		return readObject(cls);
	}

}
