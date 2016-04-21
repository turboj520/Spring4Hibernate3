// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractBurlapOutput.java

package com.autohome.burlap.io;

import com.autohome.hessian.io.AbstractHessianOutput;
import java.io.IOException;

public abstract class AbstractBurlapOutput extends AbstractHessianOutput
{

	public AbstractBurlapOutput()
	{
	}

	public void startCall(String method, int length)
		throws IOException
	{
		startCall(method);
	}

	abstract void startCall(String s)
		throws IOException;
}
