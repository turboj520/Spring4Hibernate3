// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LogPageHandler.java

package com.autohome.turbo.container.page.pages;

import com.autohome.turbo.common.URL;
import com.autohome.turbo.container.page.Page;
import com.autohome.turbo.container.page.PageHandler;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.*;

public class LogPageHandler
	implements PageHandler
{

	private static final int SHOW_LOG_LENGTH = 30000;
	private File file;

	public LogPageHandler()
	{
label0:
		{
			super();
			try
			{
				Logger logger = LogManager.getRootLogger();
				if (logger == null)
					break label0;
				Enumeration appenders = logger.getAllAppenders();
				if (appenders == null)
					break label0;
				Appender appender;
				do
				{
					if (!appenders.hasMoreElements())
						break label0;
					appender = (Appender)appenders.nextElement();
				} while (!(appender instanceof FileAppender));
				FileAppender fileAppender = (FileAppender)appender;
				String filename = fileAppender.getFile();
				file = new File(filename);
			}
			catch (Throwable t) { }
		}
	}

	public Page handle(URL url)
	{
		long size = 0L;
		String content = "";
		String modified = "Not exist";
		if (file != null && file.exists())
			try
			{
				FileInputStream fis = new FileInputStream(file);
				FileChannel channel = fis.getChannel();
				size = channel.size();
				ByteBuffer bb;
				if (size <= 30000L)
				{
					bb = ByteBuffer.allocate((int)size);
					channel.read(bb, 0L);
				} else
				{
					int pos = (int)(size - 30000L);
					bb = ByteBuffer.allocate(30000);
					channel.read(bb, pos);
				}
				bb.flip();
				content = (new String(bb.array())).replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/><br/>");
				modified = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(file.lastModified()));
			}
			catch (IOException e) { }
		Level level = LogManager.getRootLogger().getLevel();
		List rows = new ArrayList();
		List row = new ArrayList();
		row.add(content);
		rows.add(row);
		return new Page("Log", "Log", new String[] {
			(new StringBuilder()).append(file != null ? file.getName() : "").append(", ").append(size).append(" bytes, ").append(modified).append(", ").append(level).toString()
		}, rows);
	}
}
