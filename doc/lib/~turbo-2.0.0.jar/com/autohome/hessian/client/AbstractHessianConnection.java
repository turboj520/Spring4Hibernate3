// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractHessianConnection.java

package com.autohome.hessian.client;

import java.io.*;

// Referenced classes of package com.autohome.hessian.client:
//			HessianConnection

public abstract class AbstractHessianConnection
	implements HessianConnection
{

	public AbstractHessianConnection()
	{
	}

	public void addHeader(String s, String s1)
	{
	}

	public abstract OutputStream getOutputStream()
		throws IOException;

	public abstract void sendRequest()
		throws IOException;

	public abstract int getStatusCode();

	public abstract String getStatusMessage();

	public abstract InputStream getInputStream()
		throws IOException;

	public void close()
		throws IOException
	{
		destroy();
	}

	public abstract void destroy()
		throws IOException;
}
