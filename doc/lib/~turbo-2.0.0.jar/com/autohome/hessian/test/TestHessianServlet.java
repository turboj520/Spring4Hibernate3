// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TestHessianServlet.java

package com.autohome.hessian.test;

import com.autohome.hessian.server.HessianServlet;
import java.io.IOException;

// Referenced classes of package com.autohome.hessian.test:
//			Test

public class TestHessianServlet extends HessianServlet
	implements Test
{

	private ThreadLocal _threadWriter;

	public TestHessianServlet()
	{
		_threadWriter = new ThreadLocal();
	}

	public void nullCall()
	{
	}

	public String hello()
	{
		return "Hello, World";
	}

	public int subtract(int a, int b)
	{
		return a - b;
	}

	public Object echo(Object value)
	{
		return value;
	}

	public void fault()
		throws IOException
	{
		throw new NullPointerException("sample exception");
	}
}
