// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSONVisitor.java

package com.autohome.turbo.common.json;


// Referenced classes of package com.autohome.turbo.common.json:
//			ParseException

public interface JSONVisitor
{

	public static final String CLASS_PROPERTY = "class";

	public abstract void begin();

	public abstract Object end(Object obj, boolean flag)
		throws ParseException;

	public abstract void objectBegin()
		throws ParseException;

	public abstract Object objectEnd(int i)
		throws ParseException;

	public abstract void objectItem(String s)
		throws ParseException;

	public abstract void objectItemValue(Object obj, boolean flag)
		throws ParseException;

	public abstract void arrayBegin()
		throws ParseException;

	public abstract Object arrayEnd(int i)
		throws ParseException;

	public abstract void arrayItem(int i)
		throws ParseException;

	public abstract void arrayItemValue(int i, Object obj, boolean flag)
		throws ParseException;
}
