// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CompactedJavaSerialization.java

package com.autohome.turbo.common.serialize.support.java;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.java:
//			JavaObjectOutput, JavaObjectInput

public class CompactedJavaSerialization
	implements Serialization
{

	public CompactedJavaSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 4;
	}

	public String getContentType()
	{
		return "x-application/compactedjava";
	}

	public ObjectOutput serialize(URL url, OutputStream out)
		throws IOException
	{
		return new JavaObjectOutput(out, true);
	}

	public ObjectInput deserialize(URL url, InputStream is)
		throws IOException
	{
		return new JavaObjectInput(is, true);
	}
}
