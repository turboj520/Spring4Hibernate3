// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelHandlerDelegate.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.remoting.ChannelHandler;

public interface ChannelHandlerDelegate
	extends ChannelHandler
{

	public abstract ChannelHandler getHandler();
}
