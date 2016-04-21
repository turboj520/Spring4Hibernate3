// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HessianConnection.java

package com.autohome.hessian.client;

import java.io.*;

public interface HessianConnection
{

	public abstract void addHeader(String s, String s1);

	public abstract OutputStream getOutputStream()
		throws IOException;

	public abstract void sendRequest()
		throws IOException;

	public abstract int getStatusCode();

	public abstract String getStatusMessage();

	public abstract InputStream getInputStream()
		throws IOException;

	public abstract void close()
		throws IOException;

	public abstract void destroy()
		throws IOException;
}
