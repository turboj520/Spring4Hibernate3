// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TelnetCodec.java

package com.autohome.turbo.remoting.telnet.codec;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.*;
import com.autohome.turbo.remoting.buffer.ChannelBuffer;
import com.autohome.turbo.remoting.transport.codec.TransportCodec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

public class TelnetCodec extends TransportCodec
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/remoting/telnet/codec/TelnetCodec);
	private static final String HISTORY_LIST_KEY = "telnet.history.list";
	private static final String HISTORY_INDEX_KEY = "telnet.history.index";
	private static final byte UP[] = {
		27, 91, 65
	};
	private static final byte DOWN[] = {
		27, 91, 66
	};
	private static final List ENTER = Arrays.asList(new Object[] {
		new byte[] {
			13, 10
		}, new byte[] {
			10
		}
	});
	private static final List EXIT = Arrays.asList(new Object[] {
		new byte[] {
			3
		}, new byte[] {
			-1, -12, -1, -3, 6
		}, new byte[] {
			-1, -19, -1, -3, 6
		}
	});

	public TelnetCodec()
	{
	}

	public void encode(Channel channel, ChannelBuffer buffer, Object message)
		throws IOException
	{
		if (message instanceof String)
		{
			if (isClientSide(channel))
				message = (new StringBuilder()).append(message).append("\r\n").toString();
			byte msgData[] = ((String)message).getBytes(getCharset(channel).name());
			buffer.writeBytes(msgData);
		} else
		{
			super.encode(channel, buffer, message);
		}
	}

	public Object decode(Channel channel, ChannelBuffer buffer)
		throws IOException
	{
		int readable = buffer.readableBytes();
		byte message[] = new byte[readable];
		buffer.readBytes(message);
		return decode(channel, buffer, readable, message);
	}

	protected Object decode(Channel channel, ChannelBuffer buffer, int readable, byte message[])
		throws IOException
	{
		if (isClientSide(channel))
			return toString(message, getCharset(channel));
		checkPayload(channel, readable);
		if (message == null || message.length == 0)
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		if (message[message.length - 1] == 8)
		{
			try
			{
				boolean doublechar = message.length >= 3 && message[message.length - 3] < 0;
				channel.send(new String(doublechar ? (new byte[] {
					32, 32, 8, 8
				}) : (new byte[] {
					32, 8
				}), getCharset(channel).name()));
			}
			catch (RemotingException e)
			{
				throw new IOException(StringUtils.toString(e));
			}
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		}
		for (Iterator i$ = EXIT.iterator(); i$.hasNext();)
		{
			Object command = i$.next();
			if (isEquals(message, (byte[])(byte[])command))
			{
				if (logger.isInfoEnabled())
					logger.info(new Exception((new StringBuilder()).append("Close channel ").append(channel).append(" on exit command: ").append(Arrays.toString((byte[])(byte[])command)).toString()));
				channel.close();
				return null;
			}
		}

		boolean up = endsWith(message, UP);
		boolean down = endsWith(message, DOWN);
		if (up || down)
		{
			LinkedList history = (LinkedList)channel.getAttribute("telnet.history.list");
			if (history == null || history.size() == 0)
				return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
			Integer index = (Integer)channel.getAttribute("telnet.history.index");
			Integer old = index;
			if (index == null)
				index = Integer.valueOf(history.size() - 1);
			else
			if (up)
			{
				index = Integer.valueOf(index.intValue() - 1);
				if (index.intValue() < 0)
					index = Integer.valueOf(history.size() - 1);
			} else
			{
				index = Integer.valueOf(index.intValue() + 1);
				if (index.intValue() > history.size() - 1)
					index = Integer.valueOf(0);
			}
			if (old == null || !old.equals(index))
			{
				channel.setAttribute("telnet.history.index", index);
				String value = (String)history.get(index.intValue());
				if (old != null && old.intValue() >= 0 && old.intValue() < history.size())
				{
					String ov = (String)history.get(old.intValue());
					StringBuilder buf = new StringBuilder();
					for (int i = 0; i < ov.length(); i++)
						buf.append("\b");

					for (int i = 0; i < ov.length(); i++)
						buf.append(" ");

					for (int i = 0; i < ov.length(); i++)
						buf.append("\b");

					value = (new StringBuilder()).append(buf.toString()).append(value).toString();
				}
				try
				{
					channel.send(value);
				}
				catch (RemotingException e)
				{
					throw new IOException(StringUtils.toString(e));
				}
			}
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		}
		for (Iterator i$ = EXIT.iterator(); i$.hasNext();)
		{
			Object command = i$.next();
			if (isEquals(message, (byte[])(byte[])command))
			{
				if (logger.isInfoEnabled())
					logger.info(new Exception((new StringBuilder()).append("Close channel ").append(channel).append(" on exit command ").append(command).toString()));
				channel.close();
				return null;
			}
		}

		byte enter[] = null;
		Iterator i$ = ENTER.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			Object command = i$.next();
			if (!endsWith(message, (byte[])(byte[])command))
				continue;
			enter = (byte[])(byte[])command;
			break;
		} while (true);
		if (enter == null)
			return com.autohome.turbo.remoting.Codec2.DecodeResult.NEED_MORE_INPUT;
		LinkedList history = (LinkedList)channel.getAttribute("telnet.history.list");
		Integer index = (Integer)channel.getAttribute("telnet.history.index");
		channel.removeAttribute("telnet.history.index");
		if (history != null && history.size() > 0 && index != null && index.intValue() >= 0 && index.intValue() < history.size())
		{
			String value = (String)history.get(index.intValue());
			if (value != null)
			{
				byte b1[] = value.getBytes();
				if (message != null && message.length > 0)
				{
					byte b2[] = new byte[b1.length + message.length];
					System.arraycopy(b1, 0, b2, 0, b1.length);
					System.arraycopy(message, 0, b2, b1.length, message.length);
					message = b2;
				} else
				{
					message = b1;
				}
			}
		}
		String result = toString(message, getCharset(channel));
		if (result != null && result.trim().length() > 0)
		{
			if (history == null)
			{
				history = new LinkedList();
				channel.setAttribute("telnet.history.list", history);
			}
			if (history.size() == 0)
				history.addLast(result);
			else
			if (!result.equals(history.getLast()))
			{
				history.remove(result);
				history.addLast(result);
				if (history.size() > 10)
					history.removeFirst();
			}
		}
		return result;
	}

	private static Charset getCharset(Channel channel)
	{
		Object attribute;
		if (channel == null)
			break MISSING_BLOCK_LABEL_108;
		attribute = channel.getAttribute("charset");
		if (!(attribute instanceof String))
			break MISSING_BLOCK_LABEL_45;
		return Charset.forName((String)attribute);
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		break MISSING_BLOCK_LABEL_57;
		if (attribute instanceof Charset)
			return (Charset)attribute;
		String parameter;
		URL url = channel.getUrl();
		if (url == null)
			break MISSING_BLOCK_LABEL_108;
		parameter = url.getParameter("charset");
		if (parameter == null || parameter.length() <= 0)
			break MISSING_BLOCK_LABEL_108;
		return Charset.forName(parameter);
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		return Charset.forName("GBK");
		Throwable t;
		t;
		logger.warn(t.getMessage(), t);
		return Charset.defaultCharset();
	}

	private static String toString(byte message[], Charset charset)
		throws UnsupportedEncodingException
	{
		byte copy[] = new byte[message.length];
		int index = 0;
		for (int i = 0; i < message.length; i++)
		{
			byte b = message[i];
			if (b == 8)
			{
				if (index > 0)
					index--;
				if (i > 2 && message[i - 2] < 0 && index > 0)
					index--;
				continue;
			}
			if (b == 27)
			{
				if (i < message.length - 4 && message[i + 4] == 126)
				{
					i += 4;
					continue;
				}
				if (i < message.length - 3 && message[i + 3] == 126)
				{
					i += 3;
					continue;
				}
				if (i < message.length - 2)
					i += 2;
				continue;
			}
			if (b == -1 && i < message.length - 2 && (message[i + 1] == -3 || message[i + 1] == -5))
				i += 2;
			else
				copy[index++] = message[i];
		}

		if (index == 0)
			return "";
		else
			return (new String(copy, 0, index, charset.name())).trim();
	}

	private static boolean isEquals(byte message[], byte command[])
		throws IOException
	{
		return message.length == command.length && endsWith(message, command);
	}

	private static boolean endsWith(byte message[], byte command[])
		throws IOException
	{
		if (message.length < command.length)
			return false;
		int offset = message.length - command.length;
		for (int i = command.length - 1; i >= 0; i--)
			if (message[offset + i] != command[i])
				return false;

		return true;
	}

}
