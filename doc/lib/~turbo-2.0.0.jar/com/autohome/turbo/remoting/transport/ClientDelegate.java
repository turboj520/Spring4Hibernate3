// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClientDelegate.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.Parameters;
import com.autohome.turbo.common.URL;
import com.autohome.turbo.remoting.*;
import java.net.InetSocketAddress;

public class ClientDelegate
	implements Client
{

	private transient Client client;

	public ClientDelegate()
	{
	}

	public ClientDelegate(Client client)
	{
		setClient(client);
	}

	public Client getClient()
	{
		return client;
	}

	public void setClient(Client client)
	{
		if (client == null)
		{
			throw new IllegalArgumentException("client == null");
		} else
		{
			this.client = client;
			return;
		}
	}

	public void reset(URL url)
	{
		client.reset(url);
	}

	/**
	 * @deprecated Method reset is deprecated
	 */

	public void reset(Parameters parameters)
	{
		reset(getUrl().addParameters(parameters.getParameters()));
	}

	public URL getUrl()
	{
		return client.getUrl();
	}

	public InetSocketAddress getRemoteAddress()
	{
		return client.getRemoteAddress();
	}

	public void reconnect()
		throws RemotingException
	{
		client.reconnect();
	}

	public ChannelHandler getChannelHandler()
	{
		return client.getChannelHandler();
	}

	public boolean isConnected()
	{
		return client.isConnected();
	}

	public InetSocketAddress getLocalAddress()
	{
		return client.getLocalAddress();
	}

	public boolean hasAttribute(String key)
	{
		return client.hasAttribute(key);
	}

	public void send(Object message)
		throws RemotingException
	{
		client.send(message);
	}

	public Object getAttribute(String key)
	{
		return client.getAttribute(key);
	}

	public void setAttribute(String key, Object value)
	{
		client.setAttribute(key, value);
	}

	public void send(Object message, boolean sent)
		throws RemotingException
	{
		client.send(message, sent);
	}

	public void removeAttribute(String key)
	{
		client.removeAttribute(key);
	}

	public void close()
	{
		client.close();
	}

	public void close(int timeout)
	{
		client.close(timeout);
	}

	public boolean isClosed()
	{
		return client.isClosed();
	}
}
