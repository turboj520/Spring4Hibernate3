// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Log4jContainer.java

package com.autohome.turbo.container.log4j;

import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.container.Container;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.*;

public class Log4jContainer
	implements Container
{

	public static final String LOG4J_FILE = "dubbo.log4j.file";
	public static final String LOG4J_LEVEL = "dubbo.log4j.level";
	public static final String LOG4J_SUBDIRECTORY = "dubbo.log4j.subdirectory";
	public static final String DEFAULT_LOG4J_LEVEL = "ERROR";

	public Log4jContainer()
	{
	}

	public void start()
	{
		String file = ConfigUtils.getProperty("dubbo.log4j.file");
		if (file != null && file.length() > 0)
		{
			String level = ConfigUtils.getProperty("dubbo.log4j.level");
			if (level == null || level.length() == 0)
				level = "ERROR";
			Properties properties = new Properties();
			properties.setProperty("log4j.rootLogger", (new StringBuilder()).append(level).append(",application").toString());
			properties.setProperty("log4j.appender.application", "org.apache.log4j.DailyRollingFileAppender");
			properties.setProperty("log4j.appender.application.File", file);
			properties.setProperty("log4j.appender.application.Append", "true");
			properties.setProperty("log4j.appender.application.DatePattern", "'.'yyyy-MM-dd");
			properties.setProperty("log4j.appender.application.layout", "org.apache.log4j.PatternLayout");
			properties.setProperty("log4j.appender.application.layout.ConversionPattern", "%d [%t] %-5p %C{6} (%F:%L) - %m%n");
			PropertyConfigurator.configure(properties);
		}
		String subdirectory = ConfigUtils.getProperty("dubbo.log4j.subdirectory");
		if (subdirectory != null && subdirectory.length() > 0)
		{
			for (Enumeration ls = LogManager.getCurrentLoggers(); ls.hasMoreElements();)
			{
				Logger l = (Logger)ls.nextElement();
				if (l != null)
				{
					Enumeration as = l.getAllAppenders();
					while (as.hasMoreElements()) 
					{
						Appender a = (Appender)as.nextElement();
						if (a instanceof FileAppender)
						{
							FileAppender fa = (FileAppender)a;
							String f = fa.getFile();
							if (f != null && f.length() > 0)
							{
								int i = f.replace('\\', '/').lastIndexOf('/');
								String path;
								if (i == -1)
								{
									path = subdirectory;
								} else
								{
									path = f.substring(0, i);
									if (!path.endsWith(subdirectory))
										path = (new StringBuilder()).append(path).append("/").append(subdirectory).toString();
									f = f.substring(i + 1);
								}
								fa.setFile((new StringBuilder()).append(path).append("/").append(f).toString());
								fa.activateOptions();
							}
						}
					}
				}
			}

		}
	}

	public void stop()
	{
	}
}
