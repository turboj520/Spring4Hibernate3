// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FstObjectOutput.java

package com.autohome.turbo.common.serialize.support.fst;

import com.autohome.turbo.common.serialize.ObjectOutput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.turbo.common.serialize.support.fst:
//			FstFactory

public class FstObjectOutput
	implements ObjectOutput
{

	private FSTObjectOutput output;

	public FstObjectOutput(OutputStream outputStream)
	{
		output = FstFactory.getDefaultFactory().getObjectOutput(outputStream);
	}

	public void writeBool(boolean v)
		throws IOException
	{
		output.writeBoolean(v);
	}

	public void writeByte(byte v)
		throws IOException
	{
		output.writeByte(v);
	}

	public void writeShort(short v)
		throws IOException
	{
		output.writeShort(v);
	}

	public void writeInt(int v)
		throws IOException
	{
		output.writeInt(v);
	}

	public void writeLong(long v)
		throws IOException
	{
		output.writeLong(v);
	}

	public void writeFloat(float v)
		throws IOException
	{
		output.writeFloat(v);
	}

	public void writeDouble(double v)
		throws IOException
	{
		output.writeDouble(v);
	}

	public void writeBytes(byte v[])
		throws IOException
	{
		if (v == null)
			output.writeInt(-1);
		else
			writeBytes(v, 0, v.length);
	}

	public void writeBytes(byte v[], int off, int len)
		throws IOException
	{
		if (v == null)
		{
			output.writeInt(-1);
		} else
		{
			output.writeInt(len);
			output.write(v, off, len);
		}
	}

	public void writeUTF(String v)
		throws IOException
	{
		output.writeUTF(v);
	}

	public void writeObject(Object v)
		throws IOException
	{
		output.writeObject(v);
	}

	public void flushBuffer()
		throws IOException
	{
		output.flush();
	}
}
