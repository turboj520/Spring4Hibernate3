// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Log.java

package com.autohome.turbo.common.utils;

import java.io.Serializable;
import org.apache.log4j.Level;

public class Log
	implements Serializable
{

	private static final long serialVersionUID = 0xf89672ee4866518fL;
	private String logName;
	private Level logLevel;
	private String logMessage;
	private String logThread;

	public Log()
	{
	}

	public String getLogName()
	{
		return logName;
	}

	public void setLogName(String logName)
	{
		this.logName = logName;
	}

	public Level getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(Level logLevel)
	{
		this.logLevel = logLevel;
	}

	public String getLogMessage()
	{
		return logMessage;
	}

	public void setLogMessage(String logMessage)
	{
		this.logMessage = logMessage;
	}

	public String getLogThread()
	{
		return logThread;
	}

	public void setLogThread(String logThread)
	{
		this.logThread = logThread;
	}

	public int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = 31 * result + (logLevel != null ? logLevel.hashCode() : 0);
		result = 31 * result + (logMessage != null ? logMessage.hashCode() : 0);
		result = 31 * result + (logName != null ? logName.hashCode() : 0);
		result = 31 * result + (logThread != null ? logThread.hashCode() : 0);
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Log other = (Log)obj;
		if (logLevel == null)
		{
			if (other.logLevel != null)
				return false;
		} else
		if (!logLevel.equals(other.logLevel))
			return false;
		if (logMessage == null)
		{
			if (other.logMessage != null)
				return false;
		} else
		if (!logMessage.equals(other.logMessage))
			return false;
		if (logName == null)
		{
			if (other.logName != null)
				return false;
		} else
		if (!logName.equals(other.logName))
			return false;
		if (logThread == null)
		{
			if (other.logThread != null)
				return false;
		} else
		if (!logThread.equals(other.logThread))
			return false;
		return true;
	}
}
