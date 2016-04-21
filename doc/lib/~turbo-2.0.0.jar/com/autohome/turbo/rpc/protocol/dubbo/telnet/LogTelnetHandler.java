// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LogTelnetHandler.java

package com.autohome.turbo.rpc.protocol.dubbo.telnet;

import com.autohome.turbo.common.logger.Level;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.remoting.Channel;
import com.autohome.turbo.remoting.telnet.TelnetHandler;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogTelnetHandler
	implements TelnetHandler
{

	public static final String SERVICE_KEY = "telnet.log";

	public LogTelnetHandler()
	{
	}

	public String telnet(Channel channel, String message)
	{
		long size = 0L;
		File file = LoggerFactory.getFile();
		StringBuffer buf = new StringBuffer();
		if (message == null || message.trim().length() == 0)
		{
			buf.append("EXAMPLE: log error / log 100");
		} else
		{
			String str[] = message.split(" ");
			if (!StringUtils.isInteger(str[0]))
			{
				LoggerFactory.setLevel(Level.valueOf(message.toUpperCase()));
			} else
			{
				int SHOW_LOG_LENGTH = Integer.parseInt(str[0]);
				if (file != null && file.exists())
				{
					try
					{
						FileInputStream fis = new FileInputStream(file);
						FileChannel filechannel = fis.getChannel();
						size = filechannel.size();
						ByteBuffer bb;
						if (size <= (long)SHOW_LOG_LENGTH)
						{
							bb = ByteBuffer.allocate((int)size);
							filechannel.read(bb, 0L);
						} else
						{
							int pos = (int)(size - (long)SHOW_LOG_LENGTH);
							bb = ByteBuffer.allocate(SHOW_LOG_LENGTH);
							filechannel.read(bb, pos);
						}
						bb.flip();
						String content = (new String(bb.array())).replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/><br/>");
						buf.append((new StringBuilder()).append("\r\ncontent:").append(content).toString());
						buf.append((new StringBuilder()).append("\r\nmodified:").append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(file.lastModified()))).toString());
						buf.append((new StringBuilder()).append("\r\nsize:").append(size).append("\r\n").toString());
					}
					catch (Exception e)
					{
						buf.append(e.getMessage());
					}
				} else
				{
					size = 0L;
					buf.append("\r\nMESSAGE: log file not exists or log appender is console .");
				}
			}
		}
		buf.append((new StringBuilder()).append("\r\nCURRENT LOG LEVEL:").append(LoggerFactory.getLevel()).toString()).append((new StringBuilder()).append("\r\nCURRENT LOG APPENDER:").append(file != null ? file.getAbsolutePath() : "console").toString());
		return buf.toString();
	}
}
