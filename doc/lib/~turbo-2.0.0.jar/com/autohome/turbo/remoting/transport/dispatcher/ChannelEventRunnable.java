// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ChannelEventRunnable.java

package com.autohome.turbo.remoting.transport.dispatcher;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.ChannelHandler;

public class ChannelEventRunnable
	implements Runnable
{
	public static final class ChannelState extends Enum
	{

		public static final ChannelState CONNECTED;
		public static final ChannelState DISCONNECTED;
		public static final ChannelState SENT;
		public static final ChannelState RECEIVED;
		public static final ChannelState CAUGHT;
		private static final ChannelState $VALUES[];

		public static ChannelState[] values()
		{
			return (ChannelState[])$VALUES.clone();
		}

		public static ChannelState valueOf(String name)
		{
			return (ChannelState)Enum.valueOf(com/autohome/turbo/remoting/transport/dispatcher/ChannelEventRunnable$ChannelState, name);
		}

		static 
		{
			CONNECTED = new ChannelState("CONNECTED", 0);
			DISCONNECTED = new ChannelState("DISCONNECTED", 1);
			SENT = new ChannelState("SENT", 2);
			RECEIVED = new ChannelState("RECEIVED", 3);
			CAUGHT = new ChannelState("CAUGHT", 4);
			$VALUES = (new ChannelState[] {
				CONNECTED, DISCONNECTED, SENT, RECEIVED, CAUGHT
			});
		}

		private ChannelState(String s, int i)
		{
			super(s, i);
		}
	}


	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/transport/dispatcher/ChannelEventRunnable);
	private final ChannelHandler handler;
	private final Channel channel;
	private final ChannelState state;
	private final Throwable exception;
	private final Object message;

	public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state)
	{
		this(channel, handler, state, ((Throwable) (null)));
	}

	public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Object message)
	{
		this(channel, handler, state, message, null);
	}

	public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Throwable t)
	{
		this(channel, handler, state, null, t);
	}

	public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Object message, Throwable exception)
	{
		this.channel = channel;
		this.handler = handler;
		this.state = state;
		this.message = message;
		this.exception = exception;
	}

	public void run()
	{
		static class 1
		{

			static final int $SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState[];

			static 
			{
				$SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState = new int[ChannelState.values().length];
				try
				{
					$SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState[ChannelState.CONNECTED.ordinal()] = 1;
				}
				catch (NoSuchFieldError ex) { }
				try
				{
					$SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState[ChannelState.DISCONNECTED.ordinal()] = 2;
				}
				catch (NoSuchFieldError ex) { }
				try
				{
					$SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState[ChannelState.SENT.ordinal()] = 3;
				}
				catch (NoSuchFieldError ex) { }
				try
				{
					$SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState[ChannelState.RECEIVED.ordinal()] = 4;
				}
				catch (NoSuchFieldError ex) { }
				try
				{
					$SwitchMap$com$autohome$turbo$remoting$transport$dispatcher$ChannelEventRunnable$ChannelState[ChannelState.CAUGHT.ordinal()] = 5;
				}
				catch (NoSuchFieldError ex) { }
			}
		}

		switch (1..SwitchMap.com.autohome.turbo.remoting.transport.dispatcher.ChannelEventRunnable.ChannelState[state.ordinal()])
		{
		case 1: // '\001'
			try
			{
				handler.connected(channel);
			}
			catch (Exception e)
			{
				logger.warn((new StringBuilder()).append("ChannelEventRunnable handle ").append(state).append(" operation error, channel is ").append(channel).toString(), e);
			}
			break;

		case 2: // '\002'
			try
			{
				handler.disconnected(channel);
			}
			catch (Exception e)
			{
				logger.warn((new StringBuilder()).append("ChannelEventRunnable handle ").append(state).append(" operation error, channel is ").append(channel).toString(), e);
			}
			break;

		case 3: // '\003'
			try
			{
				handler.sent(channel, message);
			}
			catch (Exception e)
			{
				logger.warn((new StringBuilder()).append("ChannelEventRunnable handle ").append(state).append(" operation error, channel is ").append(channel).append(", message is ").append(message).toString(), e);
			}
			break;

		case 4: // '\004'
			try
			{
				handler.received(channel, message);
			}
			catch (Exception e)
			{
				logger.warn((new StringBuilder()).append("ChannelEventRunnable handle ").append(state).append(" operation error, channel is ").append(channel).append(", message is ").append(message).toString(), e);
			}
			break;

		case 5: // '\005'
			try
			{
				handler.caught(channel, exception);
			}
			catch (Exception e)
			{
				logger.warn((new StringBuilder()).append("ChannelEventRunnable handle ").append(state).append(" operation error, channel is ").append(channel).append(", message is: ").append(message).append(", exception is ").append(exception).toString(), e);
			}
			break;

		default:
			logger.warn((new StringBuilder()).append("unknown state: ").append(state).append(", message is ").append(message).toString());
			break;
		}
	}

}
