// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractCodec.java

package com.autohome.turbo.remoting.transport;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.serialize.Serialization;
import com.autohome.turbo.common.utils.NetUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.Codec2;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

// Referenced classes of package com.autohome.turbo.remoting.transport:
//			CodecSupport

public abstract class AbstractCodec
	implements Codec2
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/AbstractCodec);

	public AbstractCodec()
	{
	}

	protected Serialization getSerialization(Channel channel)
	{
		return CodecSupport.getSerialization(channel.getUrl());
	}

	protected static void checkPayload(Channel channel, long size)
		throws IOException
	{
		int payload = 0x800000;
		if (channel != null && channel.getUrl() != null)
			payload = channel.getUrl().getParameter("payload", 0x800000);
		if (payload > 0 && size > (long)payload)
		{
			IOException e = new IOException((new StringBuilder()).append("Data length too large: ").append(size).append(", max payload: ").append(payload).append(", channel: ").append(channel).toString());
			logger.error(e);
			throw e;
		} else
		{
			return;
		}
	}

	protected boolean isClientSide(Channel channel)
	{
		String side = (String)channel.getAttribute("side");
		if ("client".equals(side))
			return true;
		if ("server".equals(side))
		{
			return false;
		} else
		{
			InetSocketAddress address = channel.getRemoteAddress();
			URL url = channel.getUrl();
			boolean client = url.getPort() == address.getPort() && NetUtils.filterLocalHost(url.getIp()).equals(NetUtils.filterLocalHost(address.getAddress().getHostAddress()));
			channel.setAttribute("side", client ? "client" : "server");
			return client;
		}
	}

	protected boolean isServerSide(Channel channel)
	{
		return !isClientSide(channel);
	}

}
