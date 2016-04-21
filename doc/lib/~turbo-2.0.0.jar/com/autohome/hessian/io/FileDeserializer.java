// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FileDeserializer.java

package com.autohome.hessian.io;

import java.io.File;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractStringValueDeserializer

public class FileDeserializer extends AbstractStringValueDeserializer
{

	public FileDeserializer()
	{
	}

	public Class getType()
	{
		return java/io/File;
	}

	protected Object create(String value)
	{
		return new File(value);
	}
}
