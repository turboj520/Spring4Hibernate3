// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CompactedObjectInputStream.java

package com.autohome.turbo.common.serialize.support.java;

import com.autohome.turbo.common.utils.ClassHelper;
import java.io.*;

public class CompactedObjectInputStream extends ObjectInputStream
{

	private ClassLoader mClassLoader;

	public CompactedObjectInputStream(InputStream in)
		throws IOException
	{
		this(in, Thread.currentThread().getContextClassLoader());
	}

	public CompactedObjectInputStream(InputStream in, ClassLoader cl)
		throws IOException
	{
		super(in);
		mClassLoader = cl != null ? cl : ClassHelper.getClassLoader();
	}

	protected ObjectStreamClass readClassDescriptor()
		throws IOException, ClassNotFoundException
	{
		int type = read();
		if (type < 0)
			throw new EOFException();
		switch (type)
		{
		case 0: // '\0'
			return super.readClassDescriptor();

		case 1: // '\001'
			Class clazz = loadClass(readUTF());
			return ObjectStreamClass.lookup(clazz);
		}
		throw new StreamCorruptedException((new StringBuilder()).append("Unexpected class descriptor type: ").append(type).toString());
	}

	private Class loadClass(String className)
		throws ClassNotFoundException
	{
		return mClassLoader.loadClass(className);
	}
}
