// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Status.java

package com.autohome.turbo.common.status;


public class Status
{
	public static final class Level extends Enum
	{

		public static final Level OK;
		public static final Level WARN;
		public static final Level ERROR;
		public static final Level UNKNOWN;
		private static final Level $VALUES[];

		public static Level[] values()
		{
			return (Level[])$VALUES.clone();
		}

		public static Level valueOf(String name)
		{
			return (Level)Enum.valueOf(com/autohome/turbo/common/status/Status$Level, name);
		}

		static 
		{
			OK = new Level("OK", 0);
			WARN = new Level("WARN", 1);
			ERROR = new Level("ERROR", 2);
			UNKNOWN = new Level("UNKNOWN", 3);
			$VALUES = (new Level[] {
				OK, WARN, ERROR, UNKNOWN
			});
		}

		private Level(String s, int i)
		{
			super(s, i);
		}
	}


	private final Level level;
	private final String message;
	private final String description;

	public Status(Level level)
	{
		this(level, null, null);
	}

	public Status(Level level, String message)
	{
		this(level, message, null);
	}

	public Status(Level level, String message, String description)
	{
		this.level = level;
		this.message = message;
		this.description = description;
	}

	public Level getLevel()
	{
		return level;
	}

	public String getMessage()
	{
		return message;
	}

	public String getDescription()
	{
		return description;
	}
}
