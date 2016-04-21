// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboSerialization.java

package com.autohome.turbo.common.serialize.support.dubbo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.dubbo:
//			GenericObjectOutput, GenericObjectInput

public class DubboSerialization
	implements Serialization
{

	public DubboSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 1;
	}

	public String getContentType()
	{
		return "x-application/dubbo";
	}

	public ObjectOutput serialize(URL url, OutputStream out)
		throws IOException
	{
		return new GenericObjectOutput(out);
	}

	public ObjectInput deserialize(URL url, InputStream is)
		throws IOException
	{
		return new GenericObjectInput(is);
	}
}
