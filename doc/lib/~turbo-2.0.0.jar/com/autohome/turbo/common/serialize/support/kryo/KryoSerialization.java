// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   KryoSerialization.java

package com.autohome.turbo.common.serialize.support.kryo;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.kryo:
//			KryoObjectOutput, KryoObjectInput

public class KryoSerialization
	implements OptimizedSerialization
{

	public KryoSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 8;
	}

	public String getContentType()
	{
		return "x-application/kryo";
	}

	public ObjectOutput serialize(URL url, OutputStream out)
		throws IOException
	{
		return new KryoObjectOutput(out);
	}

	public ObjectInput deserialize(URL url, InputStream is)
		throws IOException
	{
		return new KryoObjectInput(is);
	}
}
