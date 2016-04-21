// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractEndpoint.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.*;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.transport.codec.CodecAdapter;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			AbstractPeer

public abstract class AbstractEndpoint extends AbstractPeer
	implements Resetable
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/AbstractEndpoint);
	private Codec2 codec;
	private int timeout;
	private int connectTimeout;

	public AbstractEndpoint(URL url, ChannelHandler handler)
	{
		super(url, handler);
		codec = getChannelCodec(url);
		timeout = url.getPositiveParameter("timeout", 1000);
		connectTimeout = url.getPositiveParameter("connect.timeout", 3000);
	}

	public void reset(URL url)
	{
		if (isClosed())
			throw new IllegalStateException((new StringBuilder()).append("Failed to reset parameters ").append(url).append(", cause: Channel closed. channel: ").append(getLocalAddress()).toString());
		try
		{
			if (url.hasParameter("heartbeat"))
			{
				int t = url.getParameter("timeout", 0);
				if (t > 0)
					timeout = t;
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
		try
		{
			if (url.hasParameter("connect.timeout"))
			{
				int t = url.getParameter("connect.timeout", 0);
				if (t > 0)
					connectTimeout = t;
			}
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
		try
		{
			if (url.hasParameter("codec"))
				codec = getChannelCodec(url);
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
	}

	/**
	 * @deprecated Method reset is deprecated
	 */

	public void reset(Parameters parameters)
	{
		reset(getUrl().addParameters(parameters.getParameters()));
	}

	protected Codec2 getCodec()
	{
		return codec;
	}

	protected int getTimeout()
	{
		return timeout;
	}

	protected int getConnectTimeout()
	{
		return connectTimeout;
	}

	protected static Codec2 getChannelCodec(URL url)
	{
		String codecName = url.getParameter("codec", "telnet");
		if (ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Codec2).hasExtension(codecName))
			return (Codec2)ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Codec2).getExtension(codecName);
		else
			return new CodecAdapter((Codec)ExtensionLoader.getExtensionLoader(com/autohome/turbo/remoting/Codec).getExtension(codecName));
	}

}
