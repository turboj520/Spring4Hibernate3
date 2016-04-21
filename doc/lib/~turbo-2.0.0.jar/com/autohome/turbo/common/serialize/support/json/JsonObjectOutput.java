// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JsonObjectOutput.java

package com.autohome.turbo.common.serialize.support.json;

import com.autohome.turbo.common.json.JSON;
import com.autohome.turbo.common.serialize.ObjectOutput;
import java.io.*;

public class JsonObjectOutput
	implements ObjectOutput
{

	private final PrintWriter writer;
	private final boolean writeClass;

	public JsonObjectOutput(OutputStream out)
	{
		this(((Writer) (new OutputStreamWriter(out))), false);
	}

	public JsonObjectOutput(Writer writer)
	{
		this(writer, false);
	}

	public JsonObjectOutput(OutputStream out, boolean writeClass)
	{
		this(((Writer) (new OutputStreamWriter(out))), writeClass);
	}

	public JsonObjectOutput(Writer writer, boolean writeClass)
	{
		this.writer = new PrintWriter(writer);
		this.writeClass = writeClass;
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
		JSON.json(obj, writer, writeClass);
		writer.println();
		writer.flush();
	}

	public void flushBuffer()
		throws IOException
	{
		writer.flush();
	}
}
