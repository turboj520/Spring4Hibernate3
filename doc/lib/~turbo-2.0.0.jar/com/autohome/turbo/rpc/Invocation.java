// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Invocation.java

package com.autohome.turbo.rpc;

import java.util.Map;

// Referenced classes of package com.autohome.turbo.rpc:
//			Invoker

public interface Invocation
{

	public abstract String getMethodName();

	public abstract Class[] getParameterTypes();

	public abstract Object[] getArguments();

	public abstract Map getAttachments();

	public abstract String getAttachment(String s);

	public abstract String getAttachment(String s, String s1);

	public abstract Invoker getInvoker();
}
