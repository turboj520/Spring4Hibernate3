// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   InputStreamDeserializer.java

package com.autohome.com.caucho.hessian.io;

import java.io.IOException;

// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			AbstractDeserializer, AbstractHessianInput

public class InputStreamDeserializer extends AbstractDeserializer
{

	public InputStreamDeserializer()
	{
	}

	public Object readObject(AbstractHessianInput in)
		throws IOException
	{
		return in.readInputStream();
	}
}
