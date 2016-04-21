// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Level.java

package com.autohome.turbo.common.logger;


public final class Level extends Enum
{

	public static final Level ALL;
	public static final Level TRACE;
	public static final Level DEBUG;
	public static final Level INFO;
	public static final Level WARN;
	public static final Level ERROR;
	public static final Level OFF;
	private static final Level $VALUES[];

	public static Level[] values()
	{
		return (Level[])$VALUES.clone();
	}

	public static Level valueOf(String name)
	{
		return (Level)Enum.valueOf(com/autohome/turbo/common/logger/Level, name);
	}

	private Level(String s, int i)
	{
		super(s, i);
	}

	static 
	{
		ALL = new Level("ALL", 0);
		TRACE = new Level("TRACE", 1);
		DEBUG = new Level("DEBUG", 2);
		INFO = new Level("INFO", 3);
		WARN = new Level("WARN", 4);
		ERROR = new Level("ERROR", 5);
		OFF = new Level("OFF", 6);
		$VALUES = (new Level[] {
			ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF
		});
	}
}
