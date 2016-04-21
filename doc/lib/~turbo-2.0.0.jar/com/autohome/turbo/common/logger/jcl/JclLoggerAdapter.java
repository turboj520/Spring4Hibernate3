// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JclLoggerAdapter.java

package com.autohome.turbo.common.logger.jcl;

import com.autohome.turbo.common.logger.*;
import java.io.File;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package com.autohome.turbo.common.logger.jcl:
//			JclLogger

public class JclLoggerAdapter
	implements LoggerAdapter
{

	private Level level;
	private File file;

	public JclLoggerAdapter()
	{
	}

	public Logger getLogger(String key)
	{
		return new JclLogger(LogFactory.getLog(key));
	}

	public Logger getLogger(Class key)
	{
		return new JclLogger(LogFactory.getLog(key));
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
