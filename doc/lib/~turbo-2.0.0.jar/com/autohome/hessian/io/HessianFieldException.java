// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianFieldException.java

package com.autohome.hessian.io;


// Referenced classes of package com.autohome.hessian.io:
//			HessianProtocolException

public class HessianFieldException extends HessianProtocolException
{

	public HessianFieldException()
	{
	}

	public HessianFieldException(String message)
	{
		super(message);
	}

	public HessianFieldException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public HessianFieldException(Throwable cause)
	{
		super(cause);
	}
}
