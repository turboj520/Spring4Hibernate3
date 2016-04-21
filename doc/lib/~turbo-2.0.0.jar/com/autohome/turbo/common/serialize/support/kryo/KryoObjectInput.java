// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   KryoObjectInput.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.autohome.turbo.common.serialize.Cleanable;
import com.autohome.turbo.common.serialize.ObjectInput;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			KryoFactory

public class KryoObjectInput
	implements ObjectInput, Cleanable
{

	private Kryo kryo;
	private Input input;

	public KryoObjectInput(InputStream inputStream)
	{
		kryo = KryoFactory.getDefaultFactory().getKryo();
		input = new Input(inputStream);
	}

	public boolean readBool()
		throws IOException
	{
		return input.readBoolean();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public byte readByte()
		throws IOException
	{
		return input.readByte();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public short readShort()
		throws IOException
	{
		return input.readShort();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public int readInt()
		throws IOException
	{
		return input.readInt();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public long readLong()
		throws IOException
	{
		return input.readLong();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public float readFloat()
		throws IOException
	{
		return input.readFloat();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public double readDouble()
		throws IOException
	{
		return input.readDouble();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public byte[] readBytes()
		throws IOException
	{
		int len = input.readInt();
		if (len < 0)
			return null;
		if (len == 0)
			return new byte[0];
		return input.readBytes(len);
		KryoException e;
		e;
		throw new IOException(e);
	}

	public String readUTF()
		throws IOException
	{
		return input.readString();
		KryoException e;
		e;
		throw new IOException(e);
	}

	public Object readObject()
		throws IOException, ClassNotFoundException
	{
		return kryo.readClassAndObject(input);
		KryoException e;
		e;
		throw new IOException(e);
	}

	public Object readObject(Class clazz)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}

	public Object readObject(Class clazz, Type type)
		throws IOException, ClassNotFoundException
	{
		return readObject(clazz);
	}

	public void cleanup()
	{
		KryoFactory.getDefaultFactory().returnKryo(kryo);
		kryo = null;
	}
}
