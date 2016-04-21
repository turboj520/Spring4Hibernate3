// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LogbackContainer.java

package com.autohome.turbo.container.logback;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.common.utils.StringUtils;
import com.autohome.turbo.container.Container;
import org.slf4j.LoggerFactory;

public class LogbackContainer
	implements Container
{

	public static final String LOGBACK_FILE = "dubbo.logback.file";
	public static final String LOGBACK_LEVEL = "dubbo.logback.level";
	public static final String LOGBACK_MAX_HISTORY = "dubbo.logback.maxhistory";
	public static final String DEFAULT_LOGBACK_LEVEL = "ERROR";

	public LogbackContainer()
	{
	}

	public void start()
	{
		String file = ConfigUtils.getProperty("dubbo.logback.file");
		if (file != null && file.length() > 0)
		{
			String level = ConfigUtils.getProperty("dubbo.logback.level");
			if (level == null || level.length() == 0)
				level = "ERROR";
			int maxHistory = StringUtils.parseInteger(ConfigUtils.getProperty("dubbo.logback.maxhistory"));
			doInitializer(file, level, maxHistory);
		}
	}

	public void stop()
	{
	}

	private void doInitializer(String file, String level, int maxHistory)
	{
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		Logger rootLogger = loggerContext.getLogger("ROOT");
		rootLogger.detachAndStopAllAppenders();
		RollingFileAppender fileAppender = new RollingFileAppender();
		fileAppender.setContext(loggerContext);
		fileAppender.setName("application");
		fileAppender.setFile(file);
		fileAppender.setAppend(true);
		TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
		policy.setContext(loggerContext);
		policy.setMaxHistory(maxHistory);
		policy.setFileNamePattern((new StringBuilder()).append(file).append(".%d{yyyy-MM-dd}").toString());
		policy.setParent(fileAppender);
		policy.start();
		fileAppender.setRollingPolicy(policy);
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern("%date [%thread] %-5level %logger (%file:%line\\) - %msg%n");
		encoder.start();
		fileAppender.setEncoder(encoder);
		fileAppender.start();
		rootLogger.addAppender(fileAppender);
		rootLogger.setLevel(Level.toLevel(level));
		rootLogger.setAdditive(false);
	}
}
