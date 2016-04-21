// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaObjectInput.java

package com.autohome.turbo.common.serialize.support.java;

import com.autohome.turbo.common.serialize.support.nativejava.NativeJavaObjectInput;
import java.io.*;
import java.lang.reflect.Type;

// Referenced classes of package com.autohome.turbo.common.serialize.support.java:
//			CompactedObjectInputStream

public class JavaObjectInput extends NativeJavaObjectInput
{

	public static final int MAX_BYTE_ARRAY_LENGTH = 0x800000;

	public JavaObjectInput(InputStream is)
		throws IOException
	{
		super(new ObjectInputStream(is));
	}

	public JavaObjectInput(InputStream is, boolean compacted)
		throws IOException
	{
		super(((ObjectInputStream) (compacted ? ((ObjectInputStream) (new CompactedObjectInputStream(is))) : new ObjectInputStream(is))));
	}

	public byte[] readBytes()
		throws IOException
	{
		int len = getObjectInputStream().readInt();
		if (len < 0)
			return null;
		if (len == 0)
			return new byte[0];
		if (len > 0x800000)
		{
			throw new IOException((new StringBuilder()).append("Byte array length too large. ").append(len).toString());
		} else
		{
			byte b[] = new byte[len];
			getObjectInputStream().readFully(b);
			return b;
		}
	}

	public String readUTF()
		throws IOException
	{
		int len = getObjectInputStream().readInt();
		if (len < 0)
			return null;
		else
			return getObjectInputStream().readUTF();
	}

	public Object readObject()
		throws IOException, ClassNotFoundException
	{
		byte b = getObjectInputStream().readByte();
		if (b == 0)
			return null;
		else
			return getObjectInputStream().readObject();
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
}
