// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StackTraceElementDeserializer.java

package com.autohome.com.caucho.hessian.io;


// Referenced classes of package com.autohome.com.caucho.hessian.io:
//			JavaDeserializer

public class StackTraceElementDeserializer extends JavaDeserializer
{

	public StackTraceElementDeserializer()
	{
		super(java/lang/StackTraceElement);
	}

	protected Object instantiate()
		throws Exception
	{
		return new StackTraceElement("", "", "", 0);
	}
}
