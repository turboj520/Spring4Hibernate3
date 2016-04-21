// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Main.java

package com.autohome.turbo.container;

import com.autohome.turbo.common.Constants;
import com.autohome.turbo.common.extension.ExtensionLoader;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import com.autohome.turbo.common.utils.ConfigUtils;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

// Referenced classes of package com.autohome.turbo.container:
//			Container

public class Main
{

	public static final String CONTAINER_KEY = "dubbo.container";
	public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";
	private static final Logger logger = LoggerFactory.getLogger(com/autohome/turbo/container/Main);
	private static final ExtensionLoader loader = ExtensionLoader.getExtensionLoader(com/autohome/turbo/container/Container);
	private static volatile boolean running = true;

	public Main()
	{
	}

	public static void main(String args[])
	{
		try
		{
			if (args == null || args.length == 0)
			{
				String config = ConfigUtils.getProperty("dubbo.container", loader.getDefaultExtensionName());
				args = Constants.COMMA_SPLIT_PATTERN.split(config);
			}
			List containers = new ArrayList();
			for (int i = 0; i < args.length; i++)
				containers.add(loader.getExtension(args[i]));

			logger.info((new StringBuilder()).append("Use container type(").append(Arrays.toString(args)).append(") to run dubbo serivce.").toString());
			if ("true".equals(System.getProperty("dubbo.shutdown.hook")))
				Runtime.getRuntime().addShutdownHook(new Thread(containers) {

					final List val$containers;

					public void run()
					{
						for (Iterator i$ = containers.iterator(); i$.hasNext();)
						{
							Container container = (Container)i$.next();
							try
							{
								container.stop();
								Main.logger.info((new StringBuilder()).append("Dubbo ").append(container.getClass().getSimpleName()).append(" stopped!").toString());
							}
							catch (Throwable t)
							{
								Main.logger.error(t.getMessage(), t);
							}
							synchronized (com/autohome/turbo/container/Main)
							{
								Main.running = false;
								com/autohome/turbo/container/Main.notify();
							}
						}

					}

			
			{
				containers = list;
				super();
			}
				});
			Container container;
			for (Iterator i$ = containers.iterator(); i$.hasNext(); logger.info((new StringBuilder()).append("Dubbo ").append(container.getClass().getSimpleName()).append(" started!").toString()))
			{
				container = (Container)i$.next();
				container.start();
			}

			System.out.println((new StringBuilder()).append((new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]")).format(new Date())).append(" Dubbo service server started!").toString());
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
		synchronized (com/autohome/turbo/container/Main)
		{
			while (running) 
				try
				{
					com/autohome/turbo/container/Main.wait();
				}
				catch (Throwable e) { }
		}
	}



}
