// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JdkLoggerAdapter.java

package com.autohome.turbo.common.logger.jdk;

import com.autohome.turbo.common.logger.Level;
import com.autohome.turbo.common.logger.LoggerAdapter;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

// Referenced classes of package com.autohome.turbo.common.logger.jdk:
//			JdkLogger

public class JdkLoggerAdapter
	implements LoggerAdapter
{

	private static final String GLOBAL_LOGGER_NAME = "global";
	private File file;

	public JdkLoggerAdapter()
	{
		try
		{
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties");
			if (in != null)
				LogManager.getLogManager().readConfiguration(in);
			else
				System.err.println("No such logging.properties in classpath for jdk logging config!");
		}
		catch (Throwable t)
		{
			System.err.println((new StringBuilder()).append("Failed to load logging.properties in classpath for jdk logging config, cause: ").append(t.getMessage()).toString());
		}
		try
		{
			java.util.logging.Handler handlers[] = Logger.getLogger("global").getHandlers();
			java.util.logging.Handler arr$[] = handlers;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++)
			{
				java.util.logging.Handler handler = arr$[i$];
				if (handler instanceof FileHandler)
				{
					FileHandler fileHandler = (FileHandler)handler;
					Field field = fileHandler.getClass().getField("files");
					File files[] = (File[])(File[])field.get(fileHandler);
					if (files != null && files.length > 0)
						file = files[0];
				}
			}

		}
		catch (Throwable t) { }
	}

	public com.autohome.turbo.common.logger.Logger getLogger(Class key)
	{
		return new JdkLogger(Logger.getLogger(key != null ? key.getName() : ""));
	}

	public com.autohome.turbo.common.logger.Logger getLogger(String key)
	{
		return new JdkLogger(Logger.getLogger(key));
	}

	public void setLevel(Level level)
	{
		Logger.getLogger("global").setLevel(toJdkLevel(level));
	}

	public Level getLevel()
	{
		return fromJdkLevel(Logger.getLogger("global").getLevel());
	}

	public File getFile()
	{
		return file;
	}

	private static java.util.logging.Level toJdkLevel(Level level)
	{
		if (level == Level.ALL)
			return java.util.logging.Level.ALL;
		if (level == Level.TRACE)
			return java.util.logging.Level.FINER;
		if (level == Level.DEBUG)
			return java.util.logging.Level.FINE;
		if (level == Level.INFO)
			return java.util.logging.Level.INFO;
		if (level == Level.WARN)
			return java.util.logging.Level.WARNING;
		if (level == Level.ERROR)
			return java.util.logging.Level.SEVERE;
		else
			return java.util.logging.Level.OFF;
	}

	private static Level fromJdkLevel(java.util.logging.Level level)
	{
		if (level == java.util.logging.Level.ALL)
			return Level.ALL;
		if (level == java.util.logging.Level.FINER)
			return Level.TRACE;
		if (level == java.util.logging.Level.FINE)
			return Level.DEBUG;
		if (level == java.util.logging.Level.INFO)
			return Level.INFO;
		if (level == java.util.logging.Level.WARNING)
			return Level.WARN;
		if (level == java.util.logging.Level.SEVERE)
			return Level.ERROR;
		else
			return Level.OFF;
	}

	public void setFile(File file1)
	{
	}
}
