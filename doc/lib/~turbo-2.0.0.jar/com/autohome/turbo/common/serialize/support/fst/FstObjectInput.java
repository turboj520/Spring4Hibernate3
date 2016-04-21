// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FstObjectInput.java

package com.autohome.turbo.common.serialize.support.fst;

import com.autohome.turbo.common.serialize.ObjectInput;
import de.ruedigermoeller.serialization.FSTObjectInput;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

// Referenced classes of package com.autohome.turbo.common.serialize.support.fst:
//			FstFactory

public class FstObjectInput
	implements ObjectInput
{

	private FSTObjectInput input;

	public FstObjectInput(InputStream inputStream)
	{
		input = FstFactory.getDefaultFactory().getObjectInput(inputStream);
	}

	public boolean readBool()
		throws IOException
	{
		return input.readBoolean();
	}

	public byte readByte()
		throws IOException
	{
		return input.readByte();
	}

	public short readShort()
		throws IOException
	{
		return input.readShort();
	}

	public int readInt()
		throws IOException
	{
		return input.readInt();
	}

	public long readLong()
		throws IOException
	{
		return input.readLong();
	}

	public float readFloat()
		throws IOException
	{
		return input.readFloat();
	}

	public double readDouble()
		throws IOException
	{
		return input.readDouble();
	}

	public byte[] readBytes()
		throws IOException
	{
		int len = input.readInt();
		if (len < 0)
			return null;
		if (len == 0)
		{
			return new byte[0];
		} else
		{
			byte b[] = new byte[len];
			input.readFully(b);
			return b;
		}
	}

	public String readUTF()
		throws IOException
	{
		return input.readUTF();
	}

	public Object readObject()
		throws IOException, ClassNotFoundException
	{
		return input.readObject();
	}

	public Object readObject(Class clazz)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}

	public Object readObject(Class clazz, Type type)
		throws IOException, ClassNotFoundException
	{
		return readObject();
	}
}
