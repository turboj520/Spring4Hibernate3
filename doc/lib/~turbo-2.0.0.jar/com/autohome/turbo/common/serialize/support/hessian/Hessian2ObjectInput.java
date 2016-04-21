// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2ObjectInput.java

package com.autohome.turbo.common.serialize.support.hessian;

import com.autohome.com.caucho.hessian.io.Hessian2Input;
import com.autohome.turbo.common.serialize.ObjectInput;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

// Referenced classes of package com.autohome.turbo.common.serialize.support.hessian:
//			Hessian2SerializerFactory

public class Hessian2ObjectInput
	implements ObjectInput
{

	private final Hessian2Input mH2i;

	public Hessian2ObjectInput(InputStream is)
	{
		mH2i = new Hessian2Input(is);
		mH2i.setSerializerFactory(Hessian2SerializerFactory.SERIALIZER_FACTORY);
	}

	public boolean readBool()
		throws IOException
	{
		return mH2i.readBoolean();
	}

	public byte readByte()
		throws IOException
	{
		return (byte)mH2i.readInt();
	}

	public short readShort()
		throws IOException
	{
		return (short)mH2i.readInt();
	}

	public int readInt()
		throws IOException
	{
		return mH2i.readInt();
	}

	public long readLong()
		throws IOException
	{
		return mH2i.readLong();
	}

	public float readFloat()
		throws IOException
	{
		return (float)mH2i.readDouble();
	}

	public double readDouble()
		throws IOException
	{
		return mH2i.readDouble();
	}

	public byte[] readBytes()
		throws IOException
	{
		return mH2i.readBytes();
	}

	public String readUTF()
		throws IOException
	{
		return mH2i.readString();
	}

	public Object readObject()
		throws IOException
	{
		return mH2i.readObject();
	}

	public Object readObject(Class cls)
		throws IOException, ClassNotFoundException
	{
		return mH2i.readObject(cls);
	}

	public Object readObject(Class cls, Type type)
		throws IOException, ClassNotFoundException
	{
		return readObject(cls);
	}
}
