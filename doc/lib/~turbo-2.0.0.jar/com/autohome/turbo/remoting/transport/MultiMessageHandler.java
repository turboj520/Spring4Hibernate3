// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MultiMessageHandler.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.support.MultiMessage;
import java.util.Iterator;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			AbstractChannelHandlerDelegate

public class MultiMessageHandler extends AbstractChannelHandlerDelegate
{

	public MultiMessageHandler(ChannelHandler handler)
	{
		super(handler);
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		if (message instanceof MultiMessage)
		{
			MultiMessage list = (MultiMessage)message;
			Object obj;
			for (Iterator i$ = list.iterator(); i$.hasNext(); handler.received(channel, obj))
				obj = i$.next();

		} else
		{
			handler.received(channel, message);
		}
	}
}
