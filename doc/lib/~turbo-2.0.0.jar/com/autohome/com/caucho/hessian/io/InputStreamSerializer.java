// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InputStreamSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
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
		{
			out.writeNull();
		} else
		{
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf, 0, buf.length)) > 0) 
				out.writeByteBufferPart(buf, 0, len);
			out.writeByteBufferEnd(buf, 0, 0);
		}
	}
}
