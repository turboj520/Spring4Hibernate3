// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NativeJavaSerialization.java

package com.autohome.turbo.common.serialize.support.nativejava;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.nativejava:
//			NativeJavaObjectOutput, NativeJavaObjectInput

public class NativeJavaSerialization
	implements Serialization
{

	public static final String NAME = "nativejava";

	public NativeJavaSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 7;
	}

	public String getContentType()
	{
		return "x-application/nativejava";
	}

	public ObjectOutput serialize(URL url, OutputStream output)
		throws IOException
	{
		return new NativeJavaObjectOutput(output);
	}

	public ObjectInput deserialize(URL url, InputStream input)
		throws IOException
	{
		return new NativeJavaObjectInput(input);
	}
}
