// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Hessian2Serialization.java

package com.autohome.turbo.common.serialize.support.hessian;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.hessian:
//			Hessian2ObjectOutput, Hessian2ObjectInput

public class Hessian2Serialization
	implements Serialization
{

	public static final byte ID = 2;

	public Hessian2Serialization()
	{
	}

	public byte getContentTypeId()
	{
		return 2;
	}

	public String getContentType()
	{
		return "x-application/hessian2";
	}

	public ObjectOutput serialize(URL url, OutputStream out)
		throws IOException
	{
		return new Hessian2ObjectOutput(out);
	}

	public ObjectInput deserialize(URL url, InputStream is)
		throws IOException
	{
		return new Hessian2ObjectInput(is);
	}
}
