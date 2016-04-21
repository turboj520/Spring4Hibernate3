// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InputStreamSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public class InputStreamSerializer extends AbstractSerializer
{

	public InputStreamSerializer()
	{
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		InputStream is = (InputStream)obj;
		if (is == null)
			out.writeNull();
		else
			out.writeByteStream(is);
	}
}
