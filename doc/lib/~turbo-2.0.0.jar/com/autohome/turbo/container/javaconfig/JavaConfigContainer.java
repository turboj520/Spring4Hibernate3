// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JavaConfigContainer.java

package com.autohome.turbo.container.javaconfig;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConfigUtils;
import com.autohome.turbo.container.Container;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JavaConfigContainer
	implements Container
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/container/javaconfig/JavaConfigContainer);
	public static final String SPRING_JAVACONFIG = "dubbo.spring.javaconfig";
	public static final String DEFAULT_SPRING_JAVACONFIG = "dubbo.spring.javaconfig";
	static AnnotationConfigApplicationContext context;

	public JavaConfigContainer()
	{
	}

	public static AnnotationConfigApplicationContext getContext()
	{
		return context;
	}

	public void start()
	{
		String configPath = ConfigUtils.getProperty("dubbo.spring.javaconfig");
		if (configPath == null || configPath.length() == 0)
			configPath = "dubbo.spring.javaconfig";
		context = new AnnotationConfigApplicationContext(new String[] {
			configPath
		});
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
