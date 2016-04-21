// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ResponseCallback.java

package com.autohome.turbo.remoting.exchange;


public interface ResponseCallback
{

	public abstract void done(Object obj);

	public abstract void caught(Throwable throwable);
}
