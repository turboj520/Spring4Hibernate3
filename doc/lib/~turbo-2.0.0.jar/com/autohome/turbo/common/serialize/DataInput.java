// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DataInput.java

package com.autohome.turbo.common.serialize;

import java.io.IOException;

public interface DataInput
{

	public abstract boolean readBool()
		throws IOException;

	public abstract byte readByte()
		throws IOException;

	public abstract short readShort()
		throws IOException;

	public abstract int readInt()
		throws IOException;

	public abstract long readLong()
		throws IOException;

	public abstract float readFloat()
		throws IOException;

	public abstract double readDouble()
		throws IOException;

	public abstract String readUTF()
		throws IOException;

	public abstract byte[] readBytes()
		throws IOException;
}
