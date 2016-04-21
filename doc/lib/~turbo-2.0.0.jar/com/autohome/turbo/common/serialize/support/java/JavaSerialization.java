// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaSerialization.java

package com.autohome.turbo.common.serialize.support.java;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.java:
//			JavaObjectOutput, JavaObjectInput

public class JavaSerialization
	implements Serialization
{

	public JavaSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 3;
	}

	public String getContentType()
	{
		return "x-application/java";
	}

	public ObjectOutput serialize(URL url, OutputStream out)
		throws IOException
	{
		return new JavaObjectOutput(out);
	}

	public ObjectInput deserialize(URL url, InputStream is)
		throws IOException
	{
		return new JavaObjectInput(is);
	}
}
