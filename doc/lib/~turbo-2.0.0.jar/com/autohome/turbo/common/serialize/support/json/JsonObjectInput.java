// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JsonObjectInput.java

package com.autohome.turbo.common.serialize.support.json;

import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.json.ParseException;
import com.autohome.turbo.common.serialize.ObjectInput;
import com.autohome.turbo.common.utils.PojoUtils;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonObjectInput
	implements ObjectInput
{

	private final BufferedReader reader;

	public JsonObjectInput(InputStream in)
	{
		this(((Reader) (new InputStreamReader(in))));
	}

	public JsonObjectInput(Reader reader)
	{
		this.reader = new BufferedReader(reader);
	}

	public boolean readBool()
		throws IOException
	{
		return ((Boolean)readObject(Boolean.TYPE)).booleanValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public byte readByte()
		throws IOException
	{
		return ((Byte)readObject(Byte.TYPE)).byteValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public short readShort()
		throws IOException
	{
		return ((Short)readObject(Short.TYPE)).shortValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public int readInt()
		throws IOException
	{
		return ((Integer)readObject(Integer.TYPE)).intValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public long readLong()
		throws IOException
	{
		return ((Long)readObject(Long.TYPE)).longValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public float readFloat()
		throws IOException
	{
		return ((Float)readObject(Float.TYPE)).floatValue();
		ClassNotFoundException e;
		e;
		throw new IOException(e.getMessage());
	}

	public double readDouble()
		throws IOException
	{
		return ((Double)readObject(Double.TYPE)).doubleValue();
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
		return readLine().getBytes();
	}

	public Object readObject()
		throws IOException, ClassNotFoundException
	{
		String json = readLine();
		if (json.startsWith("{"))
			return JSON.parse(json, java/util/Map);
		Map map;
		json = (new StringBuilder()).append("{\"value\":").append(json).append("}").toString();
		map = (Map)JSON.parse(json, java/util/Map);
		return map.get("value");
		ParseException e;
		e;
		throw new IOException(e.getMessage());
	}

	public Object readObject(Class cls)
		throws IOException, ClassNotFoundException
	{
		Object value = readObject();
		return PojoUtils.realize(value, cls);
	}

	public Object readObject(Class cls, Type type)
		throws IOException, ClassNotFoundException
	{
		Object value = readObject();
		return PojoUtils.realize(value, cls, type);
	}

	private String readLine()
		throws IOException, EOFException
	{
		String line = reader.readLine();
		if (line == null || line.trim().length() == 0)
			throw new EOFException();
		else
			return line;
	}
}
