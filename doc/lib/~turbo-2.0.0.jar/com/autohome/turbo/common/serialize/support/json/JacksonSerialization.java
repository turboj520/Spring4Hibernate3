// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JacksonSerialization.java

package com.autohome.turbo.common.serialize.support.json;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.json:
//			JacksonObjectOutput, JacksonObjectInput

public class JacksonSerialization
	implements Serialization
{

	public JacksonSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 20;
	}

	public String getContentType()
	{
		return "text/json";
	}

	public ObjectOutput serialize(URL url, OutputStream output)
		throws IOException
	{
		return new JacksonObjectOutput(output);
	}

	public ObjectInput deserialize(URL url, InputStream input)
		throws IOException
	{
		return new JacksonObjectInput(input);
	}
}
