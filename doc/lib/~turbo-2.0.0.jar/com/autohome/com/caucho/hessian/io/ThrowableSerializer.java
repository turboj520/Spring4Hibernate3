// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThrowableSerializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			JavaSerializer, AbstractHessianOutput

public class ThrowableSerializer extends JavaSerializer
{

	public ThrowableSerializer(Class cl, ClassLoader loader)
	{
		super(cl, loader);
	}

	public void writeObject(Object obj, AbstractHessianOutput out)
		throws IOException
	{
		Throwable e = (Throwable)obj;
		e.getStackTrace();
		super.writeObject(obj, out);
	}
}
