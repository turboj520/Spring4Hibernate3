// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeartbeatHandler.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.Request;
import com.autohome.turbo.remoting.exchange.Response;
import com.autohome.turbo.remoting.transport.AbstractChannelHandlerDelegate;

public class HeartbeatHandler extends AbstractChannelHandlerDelegate
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/support/header/HeartbeatHandler);
	public static String KEY_READ_TIMESTAMP = "READ_TIMESTAMP";
	public static String KEY_WRITE_TIMESTAMP = "WRITE_TIMESTAMP";

	public HeartbeatHandler(ChannelHandler handler)
	{
		super(handler);
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		setReadTimestamp(channel);
		setWriteTimestamp(channel);
		handler.connected(channel);
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		clearReadTimestamp(channel);
		clearWriteTimestamp(channel);
		handler.disconnected(channel);
	}

	public void sent(Channel channel, Object message)
		throws RemotingException
	{
		setWriteTimestamp(channel);
		handler.sent(channel, message);
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		setReadTimestamp(channel);
		if (isHeartbeatRequest(message))
		{
			Request req = (Request)message;
			if (req.isTwoWay())
			{
				Response res = new Response(req.getId(), req.getVersion());
				res.setEvent(Response.HEARTBEAT_EVENT);
				channel.send(res);
				if (logger.isInfoEnabled())
				{
					int heartbeat = channel.getUrl().getParameter("heartbeat", 0);
					if (logger.isDebugEnabled())
						logger.debug((new StringBuilder()).append("Received heartbeat from remote channel ").append(channel.getRemoteAddress()).append(", cause: The channel has no data-transmission exceeds a heartbeat period").append(heartbeat <= 0 ? "" : (new StringBuilder()).append(": ").append(heartbeat).append("ms").toString()).toString());
				}
			}
			return;
		}
		if (isHeartbeatResponse(message))
		{
			if (logger.isDebugEnabled())
				logger.debug((new StringBuilder(32)).append("Receive heartbeat response in thread ").append(Thread.currentThread().getName()).toString());
			return;
		} else
		{
			handler.received(channel, message);
			return;
		}
	}

	private void setReadTimestamp(Channel channel)
	{
		channel.setAttribute(KEY_READ_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
	}

	private void setWriteTimestamp(Channel channel)
	{
		channel.setAttribute(KEY_WRITE_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
	}

	private void clearReadTimestamp(Channel channel)
	{
		channel.removeAttribute(KEY_READ_TIMESTAMP);
	}

	private void clearWriteTimestamp(Channel channel)
	{
		channel.removeAttribute(KEY_WRITE_TIMESTAMP);
	}

	private boolean isHeartbeatRequest(Object message)
	{
		return (message instanceof Request) && ((Request)message).isHeartbeat();
	}

	private boolean isHeartbeatResponse(Object message)
	{
		return (message instanceof Response) && ((Response)message).isHeartbeat();
	}

}
