// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LoggerFactory.java

package com.autohome.turbo.common.logger;

import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.jcl.JclLoggerAdapter;
import com.autohome.turbo.common.logger.jdk.JdkLoggerAdapter;
import com.autohome.turbo.common.logger.log4j.Log4jLoggerAdapter;
import com.autohome.turbo.common.logger.slf4j.Slf4jLoggerAdapter;
import com.autohome.turbo.common.logger.support.FailsafeLogger;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Referenced classes of package com.autohome.turbo.common.logger:
//			LoggerAdapter, Logger, Level

public class LoggerFactory
{

	private static volatile LoggerAdapter LOGGER_ADAPTER;
	private static final ConcurrentMap LOGGERS = new ConcurrentHashMap();

	private LoggerFactory()
	{
	}

	public static void setLoggerAdapter(String loggerAdapter)
	{
		if (loggerAdapter != null && loggerAdapter.length() > 0)
			setLoggerAdapter((LoggerAdapter)ExtensionLoader.getExtensionLoader(com/autohome/turbo/common/logger/LoggerAdapter).getExtension(loggerAdapter));
	}

	public static void setLoggerAdapter(LoggerAdapter loggerAdapter)
	{
		if (loggerAdapter != null)
		{
			Logger logger = loggerAdapter.getLogger(com/autohome/turbo/common/logger/LoggerFactory.getName());
			logger.info((new StringBuilder()).append("using logger: ").append(loggerAdapter.getClass().getName()).toString());
			LOGGER_ADAPTER = loggerAdapter;
			java.util.Map.Entry entry;
			for (Iterator i$ = LOGGERS.entrySet().iterator(); i$.hasNext(); ((FailsafeLogger)entry.getValue()).setLogger(LOGGER_ADAPTER.getLogger((String)entry.getKey())))
				entry = (java.util.Map.Entry)i$.next();

		}
	}

	public static Logger getLogger(Class key)
	{
		FailsafeLogger logger = (FailsafeLogger)LOGGERS.get(key.getName());
		if (logger == null)
		{
			LOGGERS.putIfAbsent(key.getName(), new FailsafeLogger(LOGGER_ADAPTER.getLogger(key)));
			logger = (FailsafeLogger)LOGGERS.get(key.getName());
		}
		return logger;
	}

	public static Logger getLogger(String key)
	{
		FailsafeLogger logger = (FailsafeLogger)LOGGERS.get(key);
		if (logger == null)
		{
			LOGGERS.putIfAbsent(key, new FailsafeLogger(LOGGER_ADAPTER.getLogger(key)));
			logger = (FailsafeLogger)LOGGERS.get(key);
		}
		return logger;
	}

	public static void setLevel(Level level)
	{
		LOGGER_ADAPTER.setLevel(level);
	}

	public static Level getLevel()
	{
		return LOGGER_ADAPTER.getLevel();
	}

	public static File getFile()
	{
		return LOGGER_ADAPTER.getFile();
	}

	static 
	{
		String logger = System.getProperty("dubbo.application.logger");
		if ("slf4j".equals(logger))
			setLoggerAdapter(new Slf4jLoggerAdapter());
		else
		if ("jcl".equals(logger))
			setLoggerAdapter(new JclLoggerAdapter());
		else
		if ("log4j".equals(logger))
			setLoggerAdapter(new Log4jLoggerAdapter());
		else
		if ("jdk".equals(logger))
			setLoggerAdapter(new JdkLoggerAdapter());
		else
			try
			{
				setLoggerAdapter(new Log4jLoggerAdapter());
			}
			catch (Throwable e1)
			{
				try
				{
					setLoggerAdapter(new Slf4jLoggerAdapter());
				}
				catch (Throwable e2)
				{
					try
					{
						setLoggerAdapter(new JclLoggerAdapter());
					}
					catch (Throwable e3)
					{
						setLoggerAdapter(new JdkLoggerAdapter());
					}
				}
			}
	}
}
