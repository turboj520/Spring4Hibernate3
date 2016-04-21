// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FstFactory.java

package com.autohome.turbo.common.serialize.support.fst;

import com.autohome.turbo.common.serialize.support.SerializableClassRegistry;
import de.ruedigermoeller.serialization.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

public class FstFactory
{

	private static final FstFactory factory = new FstFactory();
	private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

	public static FstFactory getDefaultFactory()
	{
		return factory;
	}

	public FstFactory()
	{
		Class clazz;
		for (Iterator i$ = SerializableClassRegistry.getRegisteredClasses().iterator(); i$.hasNext(); conf.registerClass(new Class[] {
	clazz
}))
			clazz = (Class)i$.next();

	}

	public FSTObjectOutput getObjectOutput(OutputStream outputStream)
	{
		return conf.getObjectOutput(outputStream);
	}

	public FSTObjectInput getObjectInput(InputStream inputStream)
	{
		return conf.getObjectInput(inputStream);
	}

}
