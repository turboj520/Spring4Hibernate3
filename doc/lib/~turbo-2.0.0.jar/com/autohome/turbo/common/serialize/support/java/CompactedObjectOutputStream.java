// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CompactedObjectOutputStream.java

package com.autohome.turbo.common.serialize.support.java;

import java.io.*;

public class CompactedObjectOutputStream extends ObjectOutputStream
{

	public CompactedObjectOutputStream(OutputStream out)
		throws IOException
	{
		super(out);
	}

	protected void writeClassDescriptor(ObjectStreamClass desc)
		throws IOException
	{
		Class clazz = desc.forClass();
		if (clazz.isPrimitive() || clazz.isArray())
		{
			write(0);
			super.writeClassDescriptor(desc);
		} else
		{
			write(1);
			writeUTF(desc.getName());
		}
	}
}
