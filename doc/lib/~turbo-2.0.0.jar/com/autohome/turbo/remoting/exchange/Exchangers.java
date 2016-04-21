// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Exchangers.java

package com.autohome.turbo.remoting.exchange;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.remoting.ChannelHandler;
import com.autohome.turbo.remoting.RemotingException;
import com.autohome.turbo.remoting.exchange.support.ExchangeHandlerDispatcher;
import com.autohome.turbo.remoting.exchange.support.Replier;
import com.autohome.turbo.remoting.transport.ChannelHandlerAdapter;

// Referenced classes of package com.autohome.turbo.remoting.exchange:
//			Exchanger, ExchangeServer, ExchangeHandler, ExchangeClient

public class Exchangers
{

	public static ExchangeServer bind(String url, Replier replier)
		throws RemotingException
	{
		return bind(URL.valueOf(url), replier);
	}

	public static ExchangeServer bind(URL url, Replier replier)
		throws RemotingException
	{
		return bind(url, ((ChannelHandler) (new ChannelHandlerAdapter())), replier);
	}

	public static ExchangeServer bind(String url, ChannelHandler handler, Replier replier)
		throws RemotingException
	{
		return bind(URL.valueOf(url), handler, replier);
	}

	public static ExchangeServer bind(URL url, ChannelHandler handler, Replier replier)
		throws RemotingException
	{
		return bind(url, ((ExchangeHandler) (new ExchangeHandlerDispatcher(replier, new ChannelHandler[] {
			handler
		}))));
	}

	public static ExchangeServer bind(String url, ExchangeHandler handler)
		throws RemotingException
	{
		return bind(URL.valueOf(url), handler);
	}

	public static ExchangeServer bind(URL url, ExchangeHandler handler)
		throws RemotingException
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		if (handler == null)
		{
			throw new IllegalArgumentException("handler == null");
		} else
		{
			url = url.addParameterIfAbsent("codec", "exchange");
			return getExchanger(url).bind(url, handler);
		}
	}

	public static ExchangeClient connect(String url)
		throws RemotingException
	{
		return connect(URL.valueOf(url));
	}

	public static ExchangeClient connect(URL url)
		throws RemotingException
	{
		return connect(url, ((ChannelHandler) (new ChannelHandlerAdapter())), null);
	}

	public static ExchangeClient connect(String url, Replier replier)
		throws RemotingException
	{
		return connect(URL.valueOf(url), ((ChannelHandler) (new ChannelHandlerAdapter())), replier);
	}

	public static ExchangeClient connect(URL url, Replier replier)
		throws RemotingException
	{
		return connect(url, ((ChannelHandler) (new ChannelHandlerAdapter())), replier);
	}

	public static ExchangeClient connect(String url, ChannelHandler handler, Replier replier)
		throws RemotingException
	{
		return connect(URL.valueOf(url), handler, replier);
	}

	public static ExchangeClient connect(URL url, ChannelHandler handler, Replier replier)
		throws RemotingException
	{
		return connect(url, ((ExchangeHandler) (new ExchangeHandlerDispatcher(replier, new ChannelHandler[] {
			handler
		}))));
	}

	public static ExchangeClient connect(String url, ExchangeHandler handler)
		throws RemotingException
	{
		return connect(URL.valueOf(url), handler);
	}

	public static ExchangeClient connect(URL url, ExchangeHandler handler)
		throws RemotingException
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		if (handler == null)
		{
			throw new IllegalArgumentException("handler == null");
		} else
		{
			url = url.addParameterIfAbsent("codec", "exchange");
			return getExchanger(url).connect(url, handler);
		}
	}

	public static Exchanger getExchanger(URL url)
	{
		String type = url.getParameter("exchanger", "header");
		return getExchanger(type);
	}

	public static Exchanger getExchanger(String type)
	{
		return (Exchanger)ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/exchange/Exchanger).getExtension(type);
	}

	private Exchangers()
	{
	}

	static 
	{
		Version.checkDuplicate(com/autohome/turbo/remoting/exchange/Exchangers);
	}
}
