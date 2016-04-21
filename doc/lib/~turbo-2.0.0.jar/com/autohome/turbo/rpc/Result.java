// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Result.java

package com.autohome.turbo.rpc;

import java.util.Map;

public interface Result
{

	public abstract Object getValue();

	public abstract Throwable getException();

	public abstract boolean hasException();

	public abstract Object recreate()
		throws Throwable;

	/**
	 * @deprecated Method getResult is deprecated
	 */

	public abstract Object getResult();

	public abstract Map getAttachments();

	public abstract String getAttachment(String s);

	public abstract String getAttachment(String s, String s1);
}
