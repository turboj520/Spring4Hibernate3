// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Log4jLoggerAdapter.java

package com.autohome.turbo.common.logger.log4j;

import com.autohome.turbo.common.logger.Level;
import com.autohome.turbo.common.logger.LoggerAdapter;
import java.io.File;
import java.util.Enumeration;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

// Referenced classes of package com.autohome.turbo.common.logger.log4j:
//			Log4jLogger

public class Log4jLoggerAdapter
	implements LoggerAdapter
{

	private File file;

	public Log4jLoggerAdapter()
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

	public com.autohome.turbo.common.logger.Logger getLogger(Class key)
	{
		return new Log4jLogger(LogManager.getLogger(key));
	}

	public com.autohome.turbo.common.logger.Logger getLogger(String key)
	{
		return new Log4jLogger(LogManager.getLogger(key));
	}

	public void setLevel(Level level)
	{
		LogManager.getRootLogger().setLevel(toLog4jLevel(level));
	}

	public Level getLevel()
	{
		return fromLog4jLevel(LogManager.getRootLogger().getLevel());
	}

	public File getFile()
	{
		return file;
	}

	private static org.apache.log4j.Level toLog4jLevel(Level level)
	{
		if (level == Level.ALL)
			return org.apache.log4j.Level.ALL;
		if (level == Level.TRACE)
			return org.apache.log4j.Level.TRACE;
		if (level == Level.DEBUG)
			return org.apache.log4j.Level.DEBUG;
		if (level == Level.INFO)
			return org.apache.log4j.Level.INFO;
		if (level == Level.WARN)
			return org.apache.log4j.Level.WARN;
		if (level == Level.ERROR)
			return org.apache.log4j.Level.ERROR;
		else
			return org.apache.log4j.Level.OFF;
	}

	private static Level fromLog4jLevel(org.apache.log4j.Level level)
	{
		if (level == org.apache.log4j.Level.ALL)
			return Level.ALL;
		if (level == org.apache.log4j.Level.TRACE)
			return Level.TRACE;
		if (level == org.apache.log4j.Level.DEBUG)
			return Level.DEBUG;
		if (level == org.apache.log4j.Level.INFO)
			return Level.INFO;
		if (level == org.apache.log4j.Level.WARN)
			return Level.WARN;
		if (level == org.apache.log4j.Level.ERROR)
			return Level.ERROR;
		else
			return Level.OFF;
	}

	public void setFile(File file1)
	{
	}
}
