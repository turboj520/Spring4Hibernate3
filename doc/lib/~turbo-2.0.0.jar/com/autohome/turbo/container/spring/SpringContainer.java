// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SpringContainer.java

package com.autohome.turbo.container.spring;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.container.Container;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContainer
	implements Container
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/container/spring/SpringContainer);
	public static final String SPRING_CONFIG = "dubbo.spring.config";
	public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";
	static ClassPathXmlApplicationContext context;

	public SpringContainer()
	{
	}

	public static ClassPathXmlApplicationContext getContext()
	{
		return context;
	}

	public void start()
	{
		String configPath = ConfigUtils.getProperty("dubbo.spring.config");
		if (configPath == null || configPath.length() == 0)
			configPath = "classpath*:META-INF/spring/*.xml";
		context = new ClassPathXmlApplicationContext(configPath.split("[,\\s]+"));
		context.start();
	}

	public void stop()
	{
		try
		{
			if (context != null)
			{
				context.stop();
				context.close();
				context = null;
			}
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
