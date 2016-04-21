// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JsonSerialization.java

package com.autohome.turbo.common.serialize.support.json;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.serialize.*;
import java.io.*;

// Referenced classes of package com.autohome.turbo.common.serialize.support.json:
//			JsonObjectOutput, JsonObjectInput

public class JsonSerialization
	implements Serialization
{

	public JsonSerialization()
	{
	}

	public byte getContentTypeId()
	{
		return 5;
	}

	public String getContentType()
	{
		return "text/json";
	}

	public ObjectOutput serialize(URL url, OutputStream output)
		throws IOException
	{
		return new JsonObjectOutput(output, url.getParameter("with.class", true));
	}

	public ObjectInput deserialize(URL url, InputStream input)
		throws IOException
	{
		return new JsonObjectInput(input);
	}
}
