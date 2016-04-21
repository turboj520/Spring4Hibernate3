// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JacksonObjectOutput.java

package com.autohome.turbo.common.serialize.support.json;

import com.autohome.turbo.common.json.Jackson;
import com.autohome.turbo.common.serialize.ObjectOutput;
import com.autohome.turbo.common.utils.ReflectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JacksonObjectOutput
	implements ObjectOutput
{

	private final ObjectMapper objectMapper;
	private final Map data;
	private static final String KEY_PREFIX = "$";
	private int index;
	private final PrintWriter writer;

	public JacksonObjectOutput(OutputStream out)
	{
		this(((Writer) (new OutputStreamWriter(out))));
	}

	public JacksonObjectOutput(Writer writer)
	{
		index = 0;
		objectMapper = Jackson.getObjectMapper();
		this.writer = new PrintWriter(writer);
		data = new HashMap();
	}

	public void writeBool(boolean v)
		throws IOException
	{
		writeObject0(Boolean.valueOf(v));
	}

	public void writeByte(byte v)
		throws IOException
	{
		writeObject0(Byte.valueOf(v));
	}

	public void writeShort(short v)
		throws IOException
	{
		writeObject0(Short.valueOf(v));
	}

	public void writeInt(int v)
		throws IOException
	{
		writeObject0(Integer.valueOf(v));
	}

	public void writeLong(long v)
		throws IOException
	{
		writeObject0(Long.valueOf(v));
	}

	public void writeFloat(float v)
		throws IOException
	{
		writeObject0(Float.valueOf(v));
	}

	public void writeDouble(double v)
		throws IOException
	{
		writeObject0(Double.valueOf(v));
	}

	public void writeUTF(String v)
		throws IOException
	{
		writeObject0(v);
	}

	public void writeBytes(byte b[])
		throws IOException
	{
		writeObject0(new String(b));
	}

	public void writeBytes(byte b[], int off, int len)
		throws IOException
	{
		writeObject0(new String(b, off, len));
	}

	public void writeObject(Object obj)
		throws IOException
	{
		if (obj == null)
		{
			writeObject0(obj);
			return;
		} else
		{
			writeObject0(obj);
			Class c = obj.getClass();
			String desc = ReflectUtils.getDesc(c);
			data.put((new StringBuilder()).append("$").append(index).append("t").toString(), desc);
			return;
		}
	}

	private void writeObject0(Object obj)
		throws IOException
	{
		data.put((new StringBuilder()).append("$").append(++index).toString(), objectMapper.writeValueAsString(obj));
	}

	public void flushBuffer()
		throws IOException
	{
		objectMapper.writeValue(writer, data);
		writer.println();
		writer.flush();
	}
}
