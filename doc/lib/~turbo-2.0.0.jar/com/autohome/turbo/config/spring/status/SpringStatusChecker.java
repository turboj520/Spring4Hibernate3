// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SpringStatusChecker.java

package com.autohome.turbo.config.spring.status;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.status.Status;
import com.autohome.turbo.common.status.StatusChecker;
import com.autohome.turbo.config.spring.ServiceBean;
import java.lang.reflect.Method;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;

public class SpringStatusChecker
	implements StatusChecker
{

	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/config/spring/status/SpringStatusChecker);

	public SpringStatusChecker()
	{
	}

	public Status check()
	{
		ApplicationContext context = ServiceBean.getSpringContext();
		if (context == null)
			return new Status(com.autohome.turbo.common.status.Status.Level.UNKNOWN);
		com.autohome.turbo.common.status.Status.Level level = com.autohome.turbo.common.status.Status.Level.OK;
		if (context instanceof Lifecycle)
		{
			if (((Lifecycle)context).isRunning())
				level = com.autohome.turbo.common.status.Status.Level.OK;
			else
				level = com.autohome.turbo.common.status.Status.Level.ERROR;
		} else
		{
			level = com.autohome.turbo.common.status.Status.Level.UNKNOWN;
		}
		StringBuilder buf = new StringBuilder();
		try
		{
			Class cls = context.getClass();
			Method method;
			for (method = null; cls != null && method == null;)
				try
				{
					method = cls.getDeclaredMethod("getConfigLocations", new Class[0]);
				}
				catch (NoSuchMethodException t)
				{
					cls = cls.getSuperclass();
				}

			if (method != null)
			{
				if (!method.isAccessible())
					method.setAccessible(true);
				String configs[] = (String[])(String[])method.invoke(context, new Object[0]);
				if (configs != null && configs.length > 0)
				{
					String arr$[] = configs;
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++)
					{
						String config = arr$[i$];
						if (buf.length() > 0)
							buf.append(",");
						buf.append(config);
					}

				}
			}
		}
		catch (Throwable t)
		{
			logger.warn(t.getMessage(), t);
		}
		return new Status(level, buf.toString());
	}

}
