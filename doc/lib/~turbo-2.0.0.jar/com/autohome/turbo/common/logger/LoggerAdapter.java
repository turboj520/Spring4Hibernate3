// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   LoggerAdapter.java

package com.autohome.turbo.common.logger;

import java.io.File;

// Referenced classes of package com.autohome.turbo.common.logger:
//			Logger, Level

public interface LoggerAdapter
{

	public abstract Logger getLogger(Class class1);

	public abstract Logger getLogger(String s);

	public abstract void setLevel(Level level);

	public abstract Level getLevel();

	public abstract File getFile();

	public abstract void setFile(File file);
}
