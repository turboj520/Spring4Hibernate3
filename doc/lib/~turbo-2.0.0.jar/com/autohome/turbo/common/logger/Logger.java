// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Logger.java

package com.autohome.turbo.common.logger;


public interface Logger
{

	public abstract void trace(String s);

	public abstract void trace(Throwable throwable);

	public abstract void trace(String s, Throwable throwable);

	public abstract void debug(String s);

	public abstract void debug(Throwable throwable);

	public abstract void debug(String s, Throwable throwable);

	public abstract void info(String s);

	public abstract void info(Throwable throwable);

	public abstract void info(String s, Throwable throwable);

	public abstract void warn(String s);

	public abstract void warn(Throwable throwable);

	public abstract void warn(String s, Throwable throwable);

	public abstract void error(String s);

	public abstract void error(Throwable throwable);

	public abstract void error(String s, Throwable throwable);

	public abstract boolean isTraceEnabled();

	public abstract boolean isDebugEnabled();

	public abstract boolean isInfoEnabled();

	public abstract boolean isWarnEnabled();

	public abstract boolean isErrorEnabled();
}
