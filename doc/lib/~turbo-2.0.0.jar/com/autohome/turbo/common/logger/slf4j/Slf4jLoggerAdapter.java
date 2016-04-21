// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Slf4jLoggerAdapter.java

package com.autohome.turbo.common.logger.slf4j;

import com.autohome.turbo.common.logger.*;
import java.io.File;
import org.slf4j.LoggerFactory;

// Referenced classes of package com.autohome.turbo.common.logger.slf4j:
//			Slf4jLogger

public class Slf4jLoggerAdapter
	implements LoggerAdapter
{

	private Level level;
	private File file;

	public Slf4jLoggerAdapter()
	{
	}

	public Logger getLogger(String key)
	{
		return new Slf4jLogger(LoggerFactory.getLogger(key));
	}

	public Logger getLogger(Class key)
	{
		return new Slf4jLogger(LoggerFactory.getLogger(key));
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public Level getLevel()
	{
		return level;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}
}
