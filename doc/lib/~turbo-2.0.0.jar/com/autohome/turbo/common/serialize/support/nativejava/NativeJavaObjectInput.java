// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NativeJavaObjectInput.java

package com.autohome.turbo.common.serialize.support.nativejava;

import com.autohome.turbo.common.serialize.ObjectInput;
import com.autohome.turbo.common.utils.Assert;
import java.io.*;
import java.lang.reflect.Type;

public class NativeJavaObjectInput
	implements ObjectInput
{

	private final ObjectInputStream inputStream;

	public NativeJavaObjectInput(InputStream is)
		throws IOException
	{
		this(new ObjectInputStream(is));
	}

	protected NativeJavaObjectInput(ObjectInputStream is)
	{
		Assert.notNull(is, "input == null");
		inputStream = is;
	}

	protected ObjectInputStream getObjectInputStream()
	{
		return inputStream;
	}

	public Object readObject()
		throws IOException, ClassNotFoundException
	{
		return inputStream.readObject();
	}

	public Object readObject(Class cls)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}

	public Object readObject(Class cls, Type type)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}

	public boolean readBool()
		throws IOException
	{
		return inputStream.readBoolean();
	}

	public byte readByte()
		throws IOException
	{
		return inputStream.readByte();
	}

	public short readShort()
		throws IOException
	{
		return inputStream.readShort();
	}

	public int readInt()
		throws IOException
	{
		return inputStream.readInt();
	}

	public long readLong()
		throws IOException
	{
		return inputStream.readLong();
	}

	public float readFloat()
		throws IOException
	{
		return inputStream.readFloat();
	}

	public double readDouble()
		throws IOException
	{
		return inputStream.readDouble();
	}

	public String readUTF()
		throws IOException
	{
		return inputStream.readUTF();
	}

	public byte[] readBytes()
		throws IOException
	{
		int len = inputStream.readInt();
		if (len < 0)
			return null;
		if (len == 0)
		{
			return new byte[0];
		} else
		{
			byte result[] = new byte[len];
			inputStream.readFully(result);
			return result;
		}
	}
}
