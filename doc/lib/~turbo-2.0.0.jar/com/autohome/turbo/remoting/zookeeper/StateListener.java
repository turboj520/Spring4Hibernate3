// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StateListener.java

package com.autohome.turbo.remoting.zookeeper;


public interface StateListener
{

	public static final int DISCONNECTED = 0;
	public static final int CONNECTED = 1;
	public static final int RECONNECTED = 2;

	public abstract void stateChanged(int i);
}
