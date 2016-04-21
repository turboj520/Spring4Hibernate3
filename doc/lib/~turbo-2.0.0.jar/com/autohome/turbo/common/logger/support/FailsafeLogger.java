// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   FailsafeLogger.java

package com.autohome.turbo.common.logger.support;

import com.autohome.turbo.common.Version;
import com.autohome.turbo.common.logger.Logger;
import com.autohome.turbo.common.utils.NetUtils;

public class FailsafeLogger
	implements Logger
{

	private Logger logger;

	public FailsafeLogger(Logger logger)
	{
		this.logger = logger;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	private String appendContextMessage(String msg)
	{
		return (new StringBuilder()).append(" [DUBBO] ").append(msg).append(", dubbo version: ").append(Version.getVersion()).append(", current host: ").append(NetUtils.getLogHost()).toString();
	}

	public void trace(String msg, Throwable e)
	{
		try
		{
			logger.trace(appendContextMessage(msg), e);
		}
		catch (Throwable t) { }
	}

	public void trace(Throwable e)
	{
		try
		{
			logger.trace(e);
		}
		catch (Throwable t) { }
	}

	public void trace(String msg)
	{
		try
		{
			logger.trace(appendContextMessage(msg));
		}
		catch (Throwable t) { }
	}

	public void debug(String msg, Throwable e)
	{
		try
		{
			logger.debug(appendContextMessage(msg), e);
		}
		catch (Throwable t) { }
	}

	public void debug(Throwable e)
	{
		try
		{
			logger.debug(e);
		}
		catch (Throwable t) { }
	}

	public void debug(String msg)
	{
		try
		{
			logger.debug(appendContextMessage(msg));
		}
		catch (Throwable t) { }
	}

	public void info(String msg, Throwable e)
	{
		try
		{
			logger.info(appendContextMessage(msg), e);
		}
		catch (Throwable t) { }
	}

	public void info(String msg)
	{
		try
		{
			logger.info(appendContextMessage(msg));
		}
		catch (Throwable t) { }
	}

	public void warn(String msg, Throwable e)
	{
		try
		{
			logger.warn(appendContextMessage(msg), e);
		}
		catch (Throwable t) { }
	}

	public void warn(String msg)
	{
		try
		{
			logger.warn(appendContextMessage(msg));
		}
		catch (Throwable t) { }
	}

	public void error(String msg, Throwable e)
	{
		try
		{
			logger.error(appendContextMessage(msg), e);
		}
		catch (Throwable t) { }
	}

	public void error(String msg)
	{
		try
		{
			logger.error(appendContextMessage(msg));
		}
		catch (Throwable t) { }
	}

	public void error(Throwable e)
	{
		try
		{
			logger.error(e);
		}
		catch (Throwable t) { }
	}

	public void info(Throwable e)
	{
		try
		{
			logger.info(e);
		}
		catch (Throwable t) { }
	}

	public void warn(Throwable e)
	{
		try
		{
			logger.warn(e);
		}
		catch (Throwable t) { }
	}

	public boolean isTraceEnabled()
	{
		return logger.isTraceEnabled();
		Throwable t;
		t;
		return false;
	}

	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
		Throwable t;
		t;
		return false;
	}

	public boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
		Throwable t;
		t;
		return false;
	}

	public boolean isWarnEnabled()
	{
		return logger.isWarnEnabled();
		Throwable t;
		t;
		return false;
	}

	public boolean isErrorEnabled()
	{
		return logger.isErrorEnabled();
		Throwable t;
		t;
		return false;
	}
}
