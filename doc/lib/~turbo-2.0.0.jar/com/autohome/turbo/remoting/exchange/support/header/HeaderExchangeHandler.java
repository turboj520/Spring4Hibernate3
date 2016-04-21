// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeaderExchangeHandler.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.exchange.*;
import com.autohome.turbo.remoting.exchange.support.DefaultFuture;
import com.autohome.turbo.remoting.transport.ChannelHandlerDelegate;
import java.net.InetAddress;
import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support.header:
//			HeaderExchangeChannel, HeartbeatHandler

public class HeaderExchangeHandler
	implements ChannelHandlerDelegate
{

	protected static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/support/header/HeaderExchangeHandler);
	public static String KEY_READ_TIMESTAMP;
	public static String KEY_WRITE_TIMESTAMP;
	private final ExchangeHandler handler;

	public HeaderExchangeHandler(ExchangeHandler handler)
	{
		if (handler == null)
		{
			throw new IllegalArgumentException("handler == null");
		} else
		{
			this.handler = handler;
			return;
		}
	}

	void handlerEvent(Channel channel, Request req)
		throws RemotingException
	{
		if (req.getData() != null && req.getData().equals("R"))
			channel.setAttribute("channel.readonly", Boolean.TRUE);
	}

	Response handleRequest(ExchangeChannel channel, Request req)
		throws RemotingException
	{
		Response res = new Response(req.getId(), req.getVersion());
		if (req.isBroken())
		{
			Object data = req.getData();
			String msg;
			if (data == null)
				msg = null;
			else
			if (data instanceof Throwable)
				msg = StringUtils.toString((Throwable)data);
			else
				msg = data.toString();
			res.setErrorMessage((new StringBuilder()).append("Fail to decode request due to: ").append(msg).toString());
			res.setStatus((byte)40);
			return res;
		}
		Object msg = req.getData();
		try
		{
			Object result = handler.reply(channel, msg);
			res.setStatus((byte)20);
			res.setResult(result);
		}
		catch (Throwable e)
		{
			res.setStatus((byte)70);
			res.setErrorMessage(StringUtils.toString(e));
		}
		return res;
	}

	static void handleResponse(Channel channel, Response response)
		throws RemotingException
	{
		if (response != null && !response.isHeartbeat())
			DefaultFuture.received(channel, response);
	}

	public void connected(Channel channel)
		throws RemotingException
	{
		ExchangeChannel exchangeChannel;
		channel.setAttribute(KEY_READ_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		channel.setAttribute(KEY_WRITE_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
		handler.connected(exchangeChannel);
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		break MISSING_BLOCK_LABEL_59;
		Exception exception;
		exception;
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		throw exception;
	}

	public void disconnected(Channel channel)
		throws RemotingException
	{
		ExchangeChannel exchangeChannel;
		channel.setAttribute(KEY_READ_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		channel.setAttribute(KEY_WRITE_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
		handler.disconnected(exchangeChannel);
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		break MISSING_BLOCK_LABEL_59;
		Exception exception;
		exception;
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		throw exception;
	}

	public void sent(Channel channel, Object message)
		throws RemotingException
	{
		Throwable exception = null;
		ExchangeChannel exchangeChannel;
		channel.setAttribute(KEY_WRITE_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
		handler.sent(exchangeChannel, message);
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		break MISSING_BLOCK_LABEL_59;
		Exception exception1;
		exception1;
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		throw exception1;
		Throwable t;
		t;
		exception = t;
		if (message instanceof Request)
		{
			Request request = (Request)message;
			DefaultFuture.sent(channel, request);
		}
		if (exception != null)
		{
			if (exception instanceof RuntimeException)
				throw (RuntimeException)exception;
			if (exception instanceof RemotingException)
				throw (RemotingException)exception;
			else
				throw new RemotingException(channel.getLocalAddress(), channel.getRemoteAddress(), exception.getMessage(), exception);
		} else
		{
			return;
		}
	}

	private static boolean isClientSide(Channel channel)
	{
		InetSocketAddress address = channel.getRemoteAddress();
		URL url = channel.getUrl();
		return url.getPort() == address.getPort() && NetUtils.filterLocalHost(url.getIp()).equals(NetUtils.filterLocalHost(address.getAddress().getHostAddress()));
	}

	public void received(Channel channel, Object message)
		throws RemotingException
	{
		ExchangeChannel exchangeChannel;
		channel.setAttribute(KEY_READ_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
		if (message instanceof Request)
		{
			Request request = (Request)message;
			if (request.isEvent())
				handlerEvent(channel, request);
			else
			if (request.isTwoWay())
			{
				Response response = handleRequest(exchangeChannel, request);
				channel.send(response);
			} else
			{
				handler.received(exchangeChannel, request.getData());
			}
		} else
		if (message instanceof Response)
			handleResponse(channel, (Response)message);
		else
		if (message instanceof String)
		{
			if (isClientSide(channel))
			{
				Exception e = new Exception((new StringBuilder()).append("Dubbo client can not supported string message: ").append(message).append(" in channel: ").append(channel).append(", url: ").append(channel.getUrl()).toString());
				logger.error(e.getMessage(), e);
			} else
			{
				String echo = handler.telnet(channel, (String)message);
				if (echo != null && echo.length() > 0)
					channel.send(echo);
			}
		} else
		{
			handler.received(exchangeChannel, message);
		}
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		break MISSING_BLOCK_LABEL_265;
		Exception exception;
		exception;
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		throw exception;
	}

	public void caught(Channel channel, Throwable exception)
		throws RemotingException
	{
		ExchangeChannel exchangeChannel;
		if (exception instanceof ExecutionException)
		{
			ExecutionException e = (ExecutionException)exception;
			Object msg = e.getRequest();
			if (msg instanceof Request)
			{
				Request req = (Request)msg;
				if (req.isTwoWay() && !req.isHeartbeat())
				{
					Response res = new Response(req.getId(), req.getVersion());
					res.setStatus((byte)80);
					res.setErrorMessage(StringUtils.toString(e));
					channel.send(res);
					return;
				}
			}
		}
		exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
		handler.caught(exchangeChannel, exception);
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		break MISSING_BLOCK_LABEL_125;
		Exception exception1;
		exception1;
		HeaderExchangeChannel.removeChannelIfDisconnected(channel);
		throw exception1;
	}

	public ChannelHandler getHandler()
	{
		if (handler instanceof ChannelHandlerDelegate)
			return ((ChannelHandlerDelegate)handler).getHandler();
		else
			return handler;
	}

	static 
	{
		KEY_READ_TIMESTAMP = HeartbeatHandler.KEY_READ_TIMESTAMP;
		KEY_WRITE_TIMESTAMP = HeartbeatHandler.KEY_WRITE_TIMESTAMP;
	}
}
