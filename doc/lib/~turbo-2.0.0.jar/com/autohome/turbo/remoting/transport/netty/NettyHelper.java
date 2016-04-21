// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   NettyHelper.java

package com.autohome.turbo.remoting.transport.netty;

import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.logger.LoggerFactory;
import org.jboss.netty.logging.*;

final class NettyHelper
{
	static class DubboLogger extends AbstractInternalLogger
	{

		private Logger logger;

		public boolean isDebugEnabled()
		{
			return logger.isDebugEnabled();
		}

		public boolean isInfoEnabled()
		{
			return logger.isInfoEnabled();
		}

		public boolean isWarnEnabled()
		{
			return logger.isWarnEnabled();
		}

		public boolean isErrorEnabled()
		{
			return logger.isErrorEnabled();
		}

		public void debug(String msg)
		{
			logger.debug(msg);
		}

		public void debug(String msg, Throwable cause)
		{
			logger.debug(msg, cause);
		}

		public void info(String msg)
		{
			logger.info(msg);
		}

		public void info(String msg, Throwable cause)
		{
			logger.info(msg, cause);
		}

		public void warn(String msg)
		{
			logger.warn(msg);
		}

		public void warn(String msg, Throwable cause)
		{
			logger.warn(msg, cause);
		}

		public void error(String msg)
		{
			logger.error(msg);
		}

		public void error(String msg, Throwable cause)
		{
			logger.error(msg, cause);
		}

		public String toString()
		{
			return logger.toString();
		}

		DubboLogger(Logger logger)
		{
			this.logger = logger;
		}
	}

	static class DubboLoggerFactory extends InternalLoggerFactory
	{

		public InternalLogger newInstance(String name)
		{
			return new DubboLogger(LoggerFactory.getLogger(name));
		}

		DubboLoggerFactory()
		{
		}
	}


	NettyHelper()
	{
	}

	public static void setNettyLoggerFactory()
	{
		InternalLoggerFactory factory = InternalLoggerFactory.getDefaultFactory();
		if (factory == null || !(factory instanceof DubboLoggerFactory))
			InternalLoggerFactory.setDefaultFactory(new DubboLoggerFactory());
	}
}
