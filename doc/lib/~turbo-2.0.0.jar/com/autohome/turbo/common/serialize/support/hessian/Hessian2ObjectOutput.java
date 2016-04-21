// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2ObjectOutput.java

package com.autohome.turbo.common.serialize.support.hessian;

import com.autohome.com.caucho.hessian.io.Hessian2Output;
import com.autohome.turbo.common.serialize.ObjectOutput;
import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.autohome.turbo.common.serialize.support.hessian:
//			Hessian2SerializerFactory

public class Hessian2ObjectOutput
	implements ObjectOutput
{

	private final Hessian2Output mH2o;

	public Hessian2ObjectOutput(OutputStream os)
	{
		mH2o = new Hessian2Output(os);
		mH2o.setSerializerFactory(Hessian2SerializerFactory.SERIALIZER_FACTORY);
	}

	public void writeBool(boolean v)
		throws IOException
	{
		mH2o.writeBoolean(v);
	}

	public void writeByte(byte v)
		throws IOException
	{
		mH2o.writeInt(v);
	}

	public void writeShort(short v)
		throws IOException
	{
		mH2o.writeInt(v);
	}

	public void writeInt(int v)
		throws IOException
	{
		mH2o.writeInt(v);
	}

	public void writeLong(long v)
		throws IOException
	{
		mH2o.writeLong(v);
	}

	public void writeFloat(float v)
		throws IOException
	{
		mH2o.writeDouble(v);
	}

	public void writeDouble(double v)
		throws IOException
	{
		mH2o.writeDouble(v);
	}

	public void writeBytes(byte b[])
		throws IOException
	{
		mH2o.writeBytes(b);
	}

	public void writeBytes(byte b[], int off, int len)
		throws IOException
	{
		mH2o.writeBytes(b, off, len);
	}

	public void writeUTF(String v)
		throws IOException
	{
		mH2o.writeString(v);
	}

	public void writeObject(Object obj)
		throws IOException
	{
		mH2o.writeObject(obj);
	}

	public void flushBuffer()
		throws IOException
	{
		mH2o.flushBuffer();
	}
}
