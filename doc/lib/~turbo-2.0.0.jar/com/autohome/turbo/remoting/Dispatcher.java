// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Dispatcher.java

package com.autohome.turbo.remoting;

import com.autohome.turbo.common.URL;

// Referenced classes of package com.autohome.turbo.remoting:
//			ChannelHandler

public interface Dispatcher
{

	public abstract ChannelHandler dispatch(ChannelHandler channelhandler, URL url);
}
