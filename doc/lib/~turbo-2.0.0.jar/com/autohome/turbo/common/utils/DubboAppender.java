// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DubboAppender.java

package com.autohome.turbo.common.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

// Referenced classes of package com.autohome.turbo.common.utils:
//			Log

public class DubboAppender extends ConsoleAppender
{

	public static boolean available = false;
	public static List logList = new ArrayList();

	public DubboAppender()
	{
	}

	public static void doStart()
	{
		available = true;
	}

	public static void doStop()
	{
		available = false;
	}

	public static void clear()
	{
		logList.clear();
	}

	public void append(LoggingEvent event)
	{
		super.append(event);
		if (available)
		{
			Log temp = parseLog(event);
			logList.add(temp);
		}
	}

	private Log parseLog(LoggingEvent event)
	{
		Log log = new Log();
		log.setLogName(event.getLogger().getName());
		log.setLogLevel(event.getLevel());
		log.setLogThread(event.getThreadName());
		log.setLogMessage(event.getMessage().toString());
		return log;
	}

}
