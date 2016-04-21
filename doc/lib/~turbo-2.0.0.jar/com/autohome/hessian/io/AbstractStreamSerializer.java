// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractStreamSerializer.java

package com.autohome.hessian.io;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.autohome.hessian.io:
//			AbstractSerializer, AbstractHessianOutput

public abstract class AbstractStreamSerializer extends AbstractSerializer
{

	public AbstractStreamSerializer()
	{
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		int ref;
		InputStream is;
		if (out.addRef(obj))
			return;
		ref = out.writeObjectBegin(getClassName(obj));
		if (ref >= -1)
			break MISSING_BLOCK_LABEL_68;
		out.writeString("value");
		is = getInputStream(obj);
		out.writeByteStream(is);
		is.close();
		break MISSING_BLOCK_LABEL_61;
		Exception exception;
		exception;
		is.close();
		throw exception;
		out.writeMapEnd();
		break MISSING_BLOCK_LABEL_147;
		if (ref == -1)
		{
			out.writeClassFieldLength(1);
			out.writeString("value");
			out.writeObjectBegin(getClassName(obj));
		}
		is = getInputStream(obj);
		if (is != null)
			out.writeByteStream(is);
		else
			out.writeNull();
		if (is != null)
			is.close();
		break MISSING_BLOCK_LABEL_147;
		Exception exception1;
		exception1;
		if (is != null)
			is.close();
		throw exception1;
	}

	protected String getClassName(Object obj)
	{
		return obj.getClass().getName();
	}

	protected abstract InputStream getInputStream(Object obj)
		throws IOException;
}
