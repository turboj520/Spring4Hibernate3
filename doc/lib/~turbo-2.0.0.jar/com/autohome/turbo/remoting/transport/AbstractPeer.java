// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractPeer.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			ChannelHandlerDelegate

public abstract class AbstractPeer
	implements Endpoint, ChannelHandler
{

	private final ChannelHandler handler;
	private volatile URL url;
	private volatile boolean closed;

	public AbstractPeer(URL url, ChannelHandler handler)
	{
		if (url == null)
			throw new IllegalArgumentException("url == null");
		if (handler == null)
		{
			throw new IllegalArgumentException("handler == null");
		} else
		{
			this.url = url;
			this.handler = handler;
			return;
		}
	}

	public void send(Object message)
		throws RemotingException
	{
		send(message, url.getParameter("sent", false));
	}

	public void close()
	{
		closed = true;
	}

	public void close(int timeout)
	{
		close();
	}

	public URL getUrl()
	{
		return url;
	}

	protected void setUrl(URL url)
	{
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		} else
		{
			this.url = url;
			return;
		}
	}

	public ChannelHandler getChannelHandler()
	{
		if (handler instanceof ChannelHandlerDelegate)
			return ((ChannelHandlerDelegate)handler).getHandler();
		else
			return handler;
	}

	/**
	 * @deprecated Method getHandler is deprecated
	 */

	public ChannelHandler getHandler()
	{
		return getDelegateHandler();
	}

	public ChannelHandler getDelegateHandler()
	{
		return handler;
	}

	public boolean isClosed()
	{
		return closed;
	}

	public void connected(Channel ch)
		throws RemotingException
	{
		if (closed)
		{
			return;
		} else
		{
			handler.connected(ch);
			return;
		}
	}

	public void disconnected(Channel ch)
		throws RemotingException
	{
		handler.disconnected(ch);
	}

	public void sent(Channel ch, Object msg)
		throws RemotingException
	{
		if (closed)
		{
			return;
		} else
		{
			handler.sent(ch, msg);
			return;
		}
	}

	public void received(Channel ch, Object msg)
		throws RemotingException
	{
		if (closed)
		{
			return;
		} else
		{
			handler.received(ch, msg);
			return;
		}
	}

	public void caught(Channel ch, Throwable ex)
		throws RemotingException
	{
		handler.caught(ch, ex);
	}
}
