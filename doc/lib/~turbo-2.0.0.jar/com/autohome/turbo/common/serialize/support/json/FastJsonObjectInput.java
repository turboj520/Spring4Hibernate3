// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FastJsonObjectInput.java

package com.autohome.turbo.common.serialize.support.json;

import com.alibaba.fastjson.JSON;
import com.autohome.turbo.common.serialize.ObjectInput;
import com.autohome.turbo.common.utils.PojoUtils;
import java.io.*;
import java.lang.reflect.Type;

public class FastJsonObjectInput
	implements ObjectInput
{

	private final BufferedReader reader;

	public FastJsonObjectInput(InputStream in)
	{
		this(((Reader) (new InputStreamReader(in))));
	}

	public FastJsonObjectInput(Reader reader)
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
		return JSON.parse(json);
	}

	public Object readObject(Class cls)
		throws IOException, ClassNotFoundException
	{
		String json = readLine();
		return JSON.parseObject(json, cls);
	}

	public Object readObject(Class cls, Type type)
		throws IOException, ClassNotFoundException
	{
		Object value = readObject(cls);
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
