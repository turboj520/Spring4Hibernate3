// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DecodeHandler.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.Request;
import com.autohome.turbo.remoting.exchange.Response;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			AbstractChannelHandlerDelegate

public class DecodeHandler extends AbstractChannelHandlerDelegate
{

	private static final Logger log = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/DecodeHandler);

	public DecodeHandler(ChannelHandler handler)
	{
		super(handler);
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		if (message instanceof Decodeable)
			decode(message);
		if (message instanceof Request)
			decode(((Request)message).getData());
		if (message instanceof Response)
			decode(((Response)message).getResult());
		handler.received(channel, message);
	}

	private void decode(Object message)
	{
		if (message != null && (message instanceof Decodeable))
			try
			{
				((Decodeable)message).decode();
				if (log.isDebugEnabled())
					log.debug((new StringBuilder(32)).append("Decode decodeable message ").append(message.getClass().getName()).toString());
			}
			catch (Throwable e)
			{
				if (log.isWarnEnabled())
					log.warn((new StringBuilder(32)).append("Call Decodeable.decode failed: ").append(e.getMessage()).toString(), e);
			}
	}

}
