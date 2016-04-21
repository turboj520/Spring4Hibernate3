// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FstSerialization.java

package com.autohome.turbo.common.serialize.support.fst;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.fst:
//			FstObjectOutput, FstObjectInput

public class FstSerialization
	implements OptimizedSerialization
{

	public FstSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 9;
	}

	public String getContentType()
	{
		return "x-application/fst";
	}

	public ObjectOutput serialize(URL url, OutputStream out)
		throws IOException
	{
		return new FstObjectOutput(out);
	}

	public ObjectInput deserialize(URL url, InputStream is)
		throws IOException
	{
		return new FstObjectInput(is);
	}
}
