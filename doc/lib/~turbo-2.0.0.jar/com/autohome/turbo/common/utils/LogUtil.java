// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LogUtil.java

package com.autohome.turbo.common.utils;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Level;

// Referenced classes of package com.autohome.turbo.common.utils:
//			Log, DubboAppender

public class LogUtil
{

	private static Logger Log = LoggerFactory.getLogger(com/autohome/turbo/common/utils/LogUtil);

	public LogUtil()
	{
	}

	public static void start()
	{
		DubboAppender.doStart();
	}

	public static void stop()
	{
		DubboAppender.doStop();
	}

	public static boolean checkNoError()
	{
		return findLevel(Level.ERROR) == 0;
	}

	public static int findName(String expectedLogName)
	{
		int count = 0;
		List logList = DubboAppender.logList;
		for (int i = 0; i < logList.size(); i++)
		{
			String logName = ((Log)logList.get(i)).getLogName();
			if (logName.contains(expectedLogName))
				count++;
		}

		return count;
	}

	public static int findLevel(Level expectedLevel)
	{
		int count = 0;
		List logList = DubboAppender.logList;
		for (int i = 0; i < logList.size(); i++)
		{
			Level logLevel = ((Log)logList.get(i)).getLogLevel();
			if (logLevel.equals(expectedLevel))
				count++;
		}

		return count;
	}

	public static int findLevelWithThreadName(Level expectedLevel, String threadName)
	{
		int count = 0;
		List logList = DubboAppender.logList;
		for (int i = 0; i < logList.size(); i++)
		{
			Log log = (Log)logList.get(i);
			if (log.getLogLevel().equals(expectedLevel) && log.getLogThread().equals(threadName))
				count++;
		}

		return count;
	}

	public static int findThread(String expectedThread)
	{
		int count = 0;
		List logList = DubboAppender.logList;
		for (int i = 0; i < logList.size(); i++)
		{
			String logThread = ((Log)logList.get(i)).getLogThread();
			if (logThread.contains(expectedThread))
				count++;
		}

		return count;
	}

	public static int findMessage(String expectedMessage)
	{
		int count = 0;
		List logList = DubboAppender.logList;
		for (int i = 0; i < logList.size(); i++)
		{
			String logMessage = ((Log)logList.get(i)).getLogMessage();
			if (logMessage.contains(expectedMessage))
				count++;
		}

		return count;
	}

	public static int findMessage(Level expectedLevel, String expectedMessage)
	{
		int count = 0;
		List logList = DubboAppender.logList;
		for (int i = 0; i < logList.size(); i++)
		{
			Level logLevel = ((Log)logList.get(i)).getLogLevel();
			if (!logLevel.equals(expectedLevel))
				continue;
			String logMessage = ((Log)logList.get(i)).getLogMessage();
			if (logMessage.contains(expectedMessage))
				count++;
		}

		return count;
	}

	public static void printList(List list)
	{
		Log.info("PrintList:");
		for (Iterator it = list.iterator(); it.hasNext(); Log.info(it.next().toString()));
	}

}
