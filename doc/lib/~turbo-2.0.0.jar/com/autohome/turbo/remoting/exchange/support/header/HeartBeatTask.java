// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HeartBeatTask.java

package com.autohome.turbo.remoting.exchange.support.header;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.Client;
import com.autohome.turbo.remoting.exchange.Request;
import java.util.Collection;
import java.util.Iterator;

// Referenced classes of package com.autohome.turbo.remoting.exchange.support.header:
//			HeaderExchangeHandler

final class HeartBeatTask
	implements Runnable
{
	static interface ChannelProvider
	{

		public abstract Collection getChannels();
	}


	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/exchange/support/header/HeartBeatTask);
	private ChannelProvider channelProvider;
	private int heartbeat;
	private int heartbeatTimeout;

	HeartBeatTask(ChannelProvider provider, int heartbeat, int heartbeatTimeout)
	{
		channelProvider = provider;
		this.heartbeat = heartbeat;
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public void run()
	{
		try
		{
			long now = System.currentTimeMillis();
			Iterator i$ = channelProvider.getChannels().iterator();
			do
			{
				if (!i$.hasNext())
					break;
				Channel channel = (Channel)i$.next();
				if (!channel.isClosed())
					try
					{
						Long lastRead = (Long)channel.getAttribute(HeaderExchangeHandler.KEY_READ_TIMESTAMP);
						Long lastWrite = (Long)channel.getAttribute(HeaderExchangeHandler.KEY_WRITE_TIMESTAMP);
						if (lastRead != null && now - lastRead.longValue() > (long)heartbeat || lastWrite != null && now - lastWrite.longValue() > (long)heartbeat)
						{
							Request req = new Request();
							req.setVersion("2.0.0");
							req.setTwoWay(true);
							req.setEvent(Request.HEARTBEAT_EVENT);
							channel.send(req);
							if (logger.isDebugEnabled())
								logger.debug((new StringBuilder()).append("Send heartbeat to remote channel ").append(channel.getRemoteAddress()).append(", cause: The channel has no data-transmission exceeds a heartbeat period: ").append(heartbeat).append("ms").toString());
						}
						if (lastRead != null && now - lastRead.longValue() > (long)heartbeatTimeout)
						{
							logger.warn((new StringBuilder()).append("Close channel ").append(channel).append(", because heartbeat read idle time out: ").append(heartbeatTimeout).append("ms").toString());
							if (channel instanceof Client)
								try
								{
									((Client)channel).reconnect();
								}
								catch (Exception e) { }
							else
								channel.close();
						}
					}
					catch (Throwable t)
					{
						logger.warn((new StringBuilder()).append("Exception when heartbeat to remote channel ").append(channel.getRemoteAddress()).toString(), t);
					}
			} while (true);
		}
		catch (Throwable t)
		{
			logger.warn((new StringBuilder()).append("Unhandled exception when heartbeat, cause: ").append(t.getMessage()).toString(), t);
		}
	}

}
