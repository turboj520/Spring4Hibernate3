// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FastJsonObjectOutput.java

package com.autohome.turbo.common.serialize.support.json;

import com.alibaba.fastjson.serializer.*;
import com.autohome.turbo.common.serialize.ObjectOutput;
import java.io.*;

public class FastJsonObjectOutput
	implements ObjectOutput
{

	private final PrintWriter writer;

	public FastJsonObjectOutput(OutputStream out)
	{
		this(((Writer) (new OutputStreamWriter(out))));
	}

	public FastJsonObjectOutput(Writer writer)
	{
		this.writer = new PrintWriter(writer);
	}

	public void writeBool(boolean v)
		throws IOException
	{
		writeObject(Boolean.valueOf(v));
	}

	public void writeByte(byte v)
		throws IOException
	{
		writeObject(Byte.valueOf(v));
	}

	public void writeShort(short v)
		throws IOException
	{
		writeObject(Short.valueOf(v));
	}

	public void writeInt(int v)
		throws IOException
	{
		writeObject(Integer.valueOf(v));
	}

	public void writeLong(long v)
		throws IOException
	{
		writeObject(Long.valueOf(v));
	}

	public void writeFloat(float v)
		throws IOException
	{
		writeObject(Float.valueOf(v));
	}

	public void writeDouble(double v)
		throws IOException
	{
		writeObject(Double.valueOf(v));
	}

	public void writeUTF(String v)
		throws IOException
	{
		writeObject(v);
	}

	public void writeBytes(byte b[])
		throws IOException
	{
		writer.println(new String(b));
	}

	public void writeBytes(byte b[], int off, int len)
		throws IOException
	{
		writer.println(new String(b, off, len));
	}

	public void writeObject(Object obj)
		throws IOException
	{
		SerializeWriter out = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(out);
		serializer.config(SerializerFeature.WriteEnumUsingToString, true);
		serializer.write(obj);
		out.writeTo(writer);
		writer.println();
		writer.flush();
	}

	public void flushBuffer()
		throws IOException
	{
		writer.flush();
	}
}
