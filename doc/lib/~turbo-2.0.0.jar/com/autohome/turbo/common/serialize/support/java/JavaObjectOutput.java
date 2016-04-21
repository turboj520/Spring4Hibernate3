// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaObjectOutput.java

package com.autohome.turbo.common.serialize.support.java;

import com.autohome.turbo.common.serialize.support.nativejava.NativeJavaObjectOutput;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.java:
//			CompactedObjectOutputStream

public class JavaObjectOutput extends NativeJavaObjectOutput
{

	public JavaObjectOutput(OutputStream os)
		throws IOException
	{
		super(new ObjectOutputStream(os));
	}

	public JavaObjectOutput(OutputStream os, boolean compact)
		throws IOException
	{
		super(((ObjectOutputStream) (compact ? ((ObjectOutputStream) (new CompactedObjectOutputStream(os))) : new ObjectOutputStream(os))));
	}

	public void writeUTF(String v)
		throws IOException
	{
		if (v == null)
		{
			getObjectOutputStream().writeInt(-1);
		} else
		{
			getObjectOutputStream().writeInt(v.length());
			getObjectOutputStream().writeUTF(v);
		}
	}

	public void writeObject(Object obj)
		throws IOException
	{
		if (obj == null)
		{
			getObjectOutputStream().writeByte(0);
		} else
		{
			getObjectOutputStream().writeByte(1);
			getObjectOutputStream().writeObject(obj);
		}
	}

	public void flushBuffer()
		throws IOException
	{
		getObjectOutputStream().flush();
	}
}
